package com.sd.v1.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.sd.db.SudokuColumns;
import com.sd.db.SudokuDatabase;
import com.sd.games.CellCollection;
import com.sd.games.SudokuGame;
import com.sd.gui.GameTimeFormat;
import com.sd.gui.SdWordsGameView;
import com.sd.gui.SudokuListFilter;
import com.sd.v1.FolderDetailLoader;
import com.sd.v1.R;
import com.sd.v1.SdWordPlayActivity;
import com.sd.v1.WordsList;

import java.text.DateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LevelListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LevelListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LevelListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String LEVEL = "level";
    private static final String WORD = "word";
    private static final String DESCRIPTION = "description";

    public final static String EASY = "EASY";
    public final static String MEDIUM = "MEDIUM";
    public final static String HARD = "HARD";


    public int mLevel;
    public String mWord;
    public String mDescrption;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


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


    public LevelListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LevelListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LevelListFragment newInstance( String word , String description , int level ) {
        LevelListFragment fragment = new LevelListFragment();
        Bundle args = new Bundle();

        args.putInt(LEVEL, level);
        args.putString(WORD, word);
        args.putString(DESCRIPTION, description);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mLevel = getArguments().getInt(LEVEL);
            mWord = getArguments().getString(WORD);
            mDescrption = getArguments().getString(DESCRIPTION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.level_grid, container, false);


        ((ListView)view.findViewById(R.id.listView)).setOnCreateContextMenuListener(this);


        mDatabase = new SudokuDatabase(getActivity().getApplicationContext());
        mFolderDetailLoader = new FolderDetailLoader(getActivity().getApplicationContext());

        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        mListFilter = new SudokuListFilter(getActivity().getApplicationContext());
        mListFilter.showStateNotStarted = settings.getBoolean(FILTER_STATE_NOT_STARTED, true);
        mListFilter.showStatePlaying = settings.getBoolean(FILTER_STATE_PLAYING, true);
        mListFilter.showStateCompleted = settings.getBoolean(FILTER_STATE_SOLVED, true);

        mAdapter = new SimpleCursorAdapter(getActivity()
                , R.layout.sudoku_list_item,
                null, new String[]{SudokuColumns.DATA, SudokuColumns.STATE,
                SudokuColumns.TIME, SudokuColumns.LAST_PLAYED,
                SudokuColumns.CREATED, SudokuColumns.PUZZLE_NOTE},
                new int[]{R.id.sudoku_board, R.id.state, R.id.time,
                        R.id.last_played, R.id.created, R.id.note});

        mAdapter.setViewBinder(new SudokuListViewBinder(getActivity()));
        updateList();

        ((ListView)view.findViewById(R.id.listView)).setAdapter(mAdapter);
        ((ListView)view.findViewById(R.id.listView)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                playSudoku(id);
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void playSudoku(long sudokuID) {
        Intent i = new Intent(getActivity(), SdWordPlayActivity.class);

        i.putExtra(SdWordPlayActivity.EXTRA_SUDOKU_ID, sudokuID);
        i.putExtra(WordsList.WORD, mWord);
        i.putExtra(WordsList.WORD_DESCRIPTION, mDescrption);

        startActivity(i);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    private void updateList() {

        if (mCursor != null) {
            getActivity().stopManagingCursor(mCursor);
        }
        mCursor = mDatabase.getSudokuList(mLevel, mListFilter);
        getActivity().startManagingCursor(mCursor);
        mAdapter.changeCursor(mCursor);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    private static class SudokuListViewBinder implements SimpleCursorAdapter.ViewBinder {
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
