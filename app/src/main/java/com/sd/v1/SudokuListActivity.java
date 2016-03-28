package com.sd.v1;

import java.text.DateFormat;
import java.util.Date;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.SimpleCursorAdapter.ViewBinder;

import com.sd.db.SudokuColumns;
import com.sd.db.SudokuDatabase;
import com.sd.games.CellCollection;
import com.sd.games.FolderInfo;
import com.sd.games.SudokuGame;
import com.sd.gui.GameTimeFormat;
import com.sd.gui.SdWordsGameView;
import com.sd.gui.SudokuListFilter;
import com.sd.utils.AndroidUtils;


/**
 * List of puzzles in folder.
 *
 * @author romario
 */
public class SudokuListActivity extends ListActivity {

    public static final String EXTRA_FOLDER_ID = "folder_id";

    public static final int MENU_ITEM_INSERT = Menu.FIRST;
    public static final int MENU_ITEM_EDIT = Menu.FIRST + 1;
    public static final int MENU_ITEM_DELETE = Menu.FIRST + 2;
    public static final int MENU_ITEM_PLAY = Menu.FIRST + 3;
    public static final int MENU_ITEM_RESET = Menu.FIRST + 4;
    public static final int MENU_ITEM_EDIT_NOTE = Menu.FIRST + 5;
    public static final int MENU_ITEM_FILTER = Menu.FIRST + 6;
    public static final int MENU_ITEM_FOLDERS = Menu.FIRST + 7;

    private static final int DIALOG_DELETE_PUZZLE = 0;
    private static final int DIALOG_RESET_PUZZLE = 1;
    private static final int DIALOG_EDIT_NOTE = 2;
    private static final int DIALOG_FILTER = 3;

    private static final String FILTER_STATE_NOT_STARTED = "filter" + SudokuGame.GAME_STATE_NOT_STARTED;
    private static final String FILTER_STATE_PLAYING = "filter" + SudokuGame.GAME_STATE_PLAYING;
    private static final String FILTER_STATE_SOLVED = "filter" + SudokuGame.GAME_STATE_COMPLETED;

    private static final String TAG = "SudokuListActivity";

    private long mFolderID;

    // input parameters for dialogs
    private long mDeletePuzzleID;
    private long mResetPuzzleID;
    private long mEditNotePuzzleID;
    private TextView mEditNoteInput;
    private SudokuListFilter mListFilter;

    private TextView mFilterStatus;

    private SimpleCursorAdapter mAdapter;
    private Cursor mCursor;
    private SudokuDatabase mDatabase;
    private FolderDetailLoader mFolderDetailLoader;

    private String mWord;
    private String mWordDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // theme must be set before setContentView
        AndroidUtils.setThemeFromPreferences(this);

        setContentView(R.layout.activity_sudoku_list);
        mFilterStatus = (TextView) findViewById(R.id.filter_status);

        getListView().setOnCreateContextMenuListener(this);
        setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);

        mDatabase = new SudokuDatabase(getApplicationContext());
        mFolderDetailLoader = new FolderDetailLoader(getApplicationContext());

        mWord = getIntent().getStringExtra(WordsList.WORD);
        mWordDescription = getIntent().getStringExtra(WordsList.WORD_DESCRIPTION);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_FOLDER_ID)) {
            mFolderID = intent.getLongExtra(EXTRA_FOLDER_ID, 0);
        } else {
            Log.d(TAG, "No 'folder_id' extra provided, exiting.");
            finish();
            return;
        }

        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mListFilter = new SudokuListFilter(getApplicationContext());
        mListFilter.showStateNotStarted = settings.getBoolean(FILTER_STATE_NOT_STARTED, true);
        mListFilter.showStatePlaying = settings.getBoolean(FILTER_STATE_PLAYING, true);
        mListFilter.showStateCompleted = settings.getBoolean(FILTER_STATE_SOLVED, true);

        mAdapter = new SimpleCursorAdapter(this, R.layout.sudoku_list_item,
                null, new String[]{SudokuColumns.DATA, SudokuColumns.STATE,
                SudokuColumns.TIME, SudokuColumns.LAST_PLAYED,
                SudokuColumns.CREATED, SudokuColumns.PUZZLE_NOTE},
                new int[]{R.id.sudoku_board, R.id.state, R.id.time,
                        R.id.last_played, R.id.created, R.id.note});
        mAdapter.setViewBinder(new SudokuListViewBinder(this));
        updateList();
        setListAdapter(mAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mDatabase.close();
        mFolderDetailLoader.destroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong("mDeletePuzzleID", mDeletePuzzleID);
        outState.putLong("mResetPuzzleID", mResetPuzzleID);
        outState.putLong("mEditNotePuzzleID", mEditNotePuzzleID);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);

        mDeletePuzzleID = state.getLong("mDeletePuzzleID");
        mResetPuzzleID = state.getLong("mResetPuzzleID");
        mEditNotePuzzleID = state.getLong("mEditNotePuzzleID");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // the puzzle list is naturally refreshed when the window
        // regains focus, so we only need to update the title
        updateTitle();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // if there is no activity in history and back button was pressed, go
        // to FolderListActivity, which is the root activity.
        if (isTaskRoot() && keyCode == KeyEvent.KEYCODE_BACK) {
            Intent i = new Intent();
            i.setClass(this, FolderListActivity.class);



            startActivity(i);
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        // This is our one standard application action -- inserting a
        // new note into the list.
        menu.add(0, MENU_ITEM_FOLDERS, 0, R.string.folders).setShortcut('1', 'f')
                .setIcon(android.R.drawable.ic_menu_sort_by_size);
        menu.add(0, MENU_ITEM_FILTER, 1, R.string.filter).setShortcut('1', 'f')
                .setIcon(android.R.drawable.ic_menu_view);
        menu.add(0, MENU_ITEM_INSERT, 2, R.string.add_sudoku).setShortcut('3', 'a')
                .setIcon(android.R.drawable.ic_menu_add);
        // I'm not sure this one is ready for release
//		menu.add(0, MENU_ITEM_GENERATE, 3, R.string.generate_sudoku).setShortcut('4', 'g')
//		.setIcon(android.R.drawable.ic_menu_add);

        // Generate any additional actions that can be performed on the
        // overall list. In a normal install, there are no additional
        // actions found here, but this allows other applications to extend
        // our menu with their own actions.
        Intent intent = new Intent(null, getIntent().getData());
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0,
                new ComponentName(this, SudokuListActivity.class), null,
                intent, 0, null);

        return true;

    }



    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);

        switch (id) {
            case DIALOG_EDIT_NOTE: {
                SudokuDatabase db = new SudokuDatabase(getApplicationContext());
                SudokuGame game = db.getSudoku(mEditNotePuzzleID);
                mEditNoteInput.setText(game.getNote());
                break;
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,
                                    ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info;
        try {
            info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        } catch (ClassCastException e) {
            Log.e(TAG, "bad menuInfo", e);
            return;
        }

        Cursor cursor = (Cursor) getListAdapter().getItem(info.position);
        if (cursor == null) {
            // For some reason the requested item isn't available, do nothing
            return;
        }

        menu.setHeaderTitle("Puzzle");

        // Add a menu item to delete the note
        menu.add(0, MENU_ITEM_PLAY, 0, R.string.play_puzzle);
        menu.add(0, MENU_ITEM_EDIT_NOTE, 1, R.string.edit_note);
        menu.add(0, MENU_ITEM_RESET, 2, R.string.reset_puzzle);
        menu.add(0, MENU_ITEM_EDIT, 3, R.string.edit_puzzle);
        menu.add(0, MENU_ITEM_DELETE, 4, R.string.delete_puzzle);
    }




    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        playSudoku(id);
    }

    /**
     * Updates whole list.
     */
    private void updateList() {
        updateTitle();
        updateFilterStatus();

        if (mCursor != null) {
            stopManagingCursor(mCursor);
        }
        mCursor = mDatabase.getSudokuList(mFolderID, mListFilter);
        startManagingCursor(mCursor);
        mAdapter.changeCursor(mCursor);
    }

    private void updateFilterStatus() {

        if (mListFilter.showStateCompleted && mListFilter.showStateNotStarted && mListFilter.showStatePlaying) {
            mFilterStatus.setVisibility(View.GONE);
        } else {
            mFilterStatus.setText(getString(R.string.filter_active, mListFilter));
            mFilterStatus.setVisibility(View.VISIBLE);
        }
    }

    private void updateTitle() {
        FolderInfo folder = mDatabase.getFolderInfo(mFolderID);
        setTitle(folder.name);

        mFolderDetailLoader.loadDetailAsync(mFolderID, new FolderDetailLoader.FolderDetailCallback() {
            @Override
            public void onLoaded(FolderInfo folderInfo) {
                if (folderInfo != null)
                    setTitle(folderInfo.name + " - " + folderInfo.getDetail(getApplicationContext()));
            }
        });
    }

    private void playSudoku(long sudokuID) {
        Intent i = new Intent(SudokuListActivity.this, SdWordPlayActivity.class);

        i.putExtra(SdWordPlayActivity.EXTRA_SUDOKU_ID, sudokuID);
        i.putExtra(WordsList.WORD, mWord);
        i.putExtra(WordsList.WORD_DESCRIPTION, mWordDescription);

        startActivity(i);
    }

    private static class SudokuListViewBinder implements ViewBinder {
        private Context mContext;
        private GameTimeFormat mGameTimeFormatter = new GameTimeFormat();
        private DateFormat mDateTimeFormatter = DateFormat.getDateTimeInstance(
                DateFormat.SHORT, DateFormat.SHORT);
        private DateFormat mTimeFormatter = DateFormat
                .getTimeInstance(DateFormat.SHORT);

        public SudokuListViewBinder(Context context) {
            mContext = context;
        }

        @Override
        public boolean setViewValue(View view, Cursor c, int columnIndex) {

            int state = c.getInt(c.getColumnIndex(SudokuColumns.STATE));

            TextView label = null;

            switch (view.getId()) {
                case R.id.sudoku_board:
                    String data = c.getString(columnIndex);
                    // TODO: still can be faster, I don't have to call initCollection and read notes
                    CellCollection cells = null;
                    ;
                    try {
                        cells = CellCollection.deserialize(data);
                    } catch (Exception e) {
                        long id = c.getLong(c.getColumnIndex(SudokuColumns._ID));
                        Log.e(TAG, String.format("Exception occurred when deserializing puzzle with id %s.", id), e);
                    }
                    SdWordsGameView board = (SdWordsGameView) view;
                    board.setReadOnly(true);
                    board.setFocusable(false);
                    ((SdWordsGameView) view).setCells(cells);
                    break;
                case R.id.state:
                    label = ((TextView) view);
                    String stateString = null;
                    switch (state) {
                        case SudokuGame.GAME_STATE_COMPLETED:
                            stateString = mContext.getString(R.string.solved);
                            break;
                        case SudokuGame.GAME_STATE_PLAYING:
                            stateString = mContext.getString(R.string.playing);
                            break;
                    }
                    label.setVisibility(stateString == null ? View.GONE
                            : View.VISIBLE);
                    label.setText(stateString);
                    if (state == SudokuGame.GAME_STATE_COMPLETED) {
                        // TODO: read colors from android resources
                        label.setTextColor(Color.rgb(187, 187, 187));
                    } else {
                        label.setTextColor(Color.rgb(255, 255, 255));
                        //label.setTextColor(SudokuListActivity.this.getResources().getColor(R.));
                    }
                    break;
                case R.id.time:
                    long time = c.getLong(columnIndex);
                    label = ((TextView) view);
                    String timeString = null;
                    if (time != 0) {
                        timeString = mGameTimeFormatter.format(time);
                    }
                    label.setVisibility(timeString == null ? View.GONE
                            : View.VISIBLE);
                    label.setText(timeString);
                    if (state == SudokuGame.GAME_STATE_COMPLETED) {
                        // TODO: read colors from android resources
                        label.setTextColor(Color.rgb(187, 187, 187));
                    } else {
                        label.setTextColor(Color.rgb(255, 255, 255));
                    }
                    break;
                case R.id.last_played:
                    long lastPlayed = c.getLong(columnIndex);
                    label = ((TextView) view);
                    String lastPlayedString = null;
                    if (lastPlayed != 0) {
                        lastPlayedString = mContext.getString(R.string.last_played_at,
                                getDateAndTimeForHumans(lastPlayed));
                    }
                    label.setVisibility(lastPlayedString == null ? View.GONE
                            : View.VISIBLE);
                    label.setText(lastPlayedString);
                    break;
                case R.id.created:
                    long created = c.getLong(columnIndex);
                    label = ((TextView) view);
                    String createdString = null;
                    if (created != 0) {
                        createdString = mContext.getString(R.string.created_at,
                                getDateAndTimeForHumans(created));
                    }
                    // TODO: when GONE, note is not correctly aligned below last_played
                    label.setVisibility(createdString == null ? View.INVISIBLE
                            : View.VISIBLE);
                    label.setText(createdString);
                    break;
                case R.id.note:
                    String note = c.getString(columnIndex);
                    label = ((TextView) view);
                    if (note == null || note.trim() == "") {
                        ((TextView) view).setVisibility(View.GONE);
                    } else {
                        ((TextView) view).setText(note);
                    }
                    label
                            .setVisibility((note == null || note.trim().equals("")) ? View.GONE
                                    : View.VISIBLE);
                    label.setText(note);
                    break;
            }

            return true;
        }

        private String getDateAndTimeForHumans(long datetime) {
            Date date = new Date(datetime);

            Date now = new Date(System.currentTimeMillis());
            Date today = new Date(now.getYear(), now.getMonth(), now.getDate());
            Date yesterday = new Date(System.currentTimeMillis()
                    - (1000 * 60 * 60 * 24));

            if (date.after(today)) {
                return mContext.getString(R.string.at_time, mTimeFormatter.format(date));
            } else if (date.after(yesterday)) {
                return mContext.getString(R.string.yesterday_at_time, mTimeFormatter.format(date));
            } else {
                return mContext.getString(R.string.on_date, mDateTimeFormatter.format(date));
            }

        }
    }

}