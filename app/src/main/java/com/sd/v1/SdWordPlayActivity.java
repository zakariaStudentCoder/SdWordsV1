package com.sd.v1;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.sd.db.SudokuDatabase;
import com.sd.games.Cell;
import com.sd.games.CellNote;
import com.sd.games.SudokuGame;
import com.sd.gui.GameTimeFormat;
import com.sd.gui.SdWordsGameView;
import com.sd.translator.WordNumberTranslator;



import java.util.HashMap;
import java.util.Set;
import com.sd.gui.Timer;
import com.sd.translator.WordWrapper;


public class SdWordPlayActivity extends AppCompatActivity {

    private SdWordsGameView mSudokuBoard;;
    private SudokuDatabase mDatabase;
    private boolean mFullScreen;
    private SudokuGame mSudokuGame;
    private WordNumberTranslator mTranslator;
    private View mInputButtons;
    private HashMap<Integer , Integer> mDictionnaryInputButtons;
    private boolean isNoteModeActive = false;
    private SdWordPlayActivity activityInstance;

    private String mWord;
    private String mWordDescription;
    public static String EXTRA_SUDOKU_ID = "com.sd.v1.EXTRA_SUDOKU_ID";


    private Toolbar toolbar;
    private boolean mShowTime = true;
    private GameTimer mGameTimer;
    private GameTimeFormat mGameTimeFormatter = new GameTimeFormat();


    private static final int DIALOG_RESTART = 1;
    private static final int DIALOG_WELL_DONE = 2;
    private static final int DIALOG_CLEAR_NOTES = 3;
    private static final int DIALOG_UNDO_TO_CHECKPOINT = 4;
    private Cell mSelectedCell;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // go fullscreen for devices with QVGA screen (only way I found
        // how to fit UI on the screen)
        Display display = getWindowManager().getDefaultDisplay();
        if ((display.getWidth() == 240 || display.getWidth() == 320) && (display.getHeight() == 240 || display.getHeight() == 320)) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mFullScreen = true;
        }

        setContentView(R.layout.activity_sd_word_play);
        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);

        mDatabase = new SudokuDatabase(getApplicationContext());

        mWord = getIntent().getStringExtra(WordsList.WORD);
        mWordDescription = getIntent().getStringExtra(WordsList.WORD_DESCRIPTION);


        if(mWord != null) {
            mTranslator = new WordNumberTranslator(true , mWord);
        }
        else
        {
            mTranslator = new WordNumberTranslator(false , "ABLUTIONS");
        }

        mInputButtons = (View)findViewById(R.id.input_buttons);
        activityInstance = this;

        mDictionnaryInputButtons = new HashMap<Integer , Integer>();
        IntializeHashMapButtons();


        try {
            mSudokuBoard = (SdWordsGameView) findViewById(R.id.sudoku_board);
            mGameTimer = new GameTimer();

            // create sudoku game instance
            if (savedInstanceState == null) {
                // activity runs for the first time, read game from database
                long mSudokuGameID = getIntent().getLongExtra(EXTRA_SUDOKU_ID, 0);
                mSudokuGame = mDatabase.getSudoku(mSudokuGameID);
            } else {
                // activity has been running before, restore its state
                mSudokuGame = new SudokuGame();
                mSudokuGame.restoreState(savedInstanceState);
                mGameTimer.restoreState(savedInstanceState);
            }

            if (mSudokuGame.getState() == SudokuGame.GAME_STATE_NOT_STARTED) {
                Log.d("GAME_STATE_NOT_STARTED" , "GAME_STATE_NOT_STARTED");
                mSudokuGame.start();
            } else if (mSudokuGame.getState() == SudokuGame.GAME_STATE_PLAYING) {
                Log.d("GAME_STATE_PLAYING" , "GAME_STATE_PLAYING");
                mSudokuGame.resume();
            }

            if (mSudokuGame.getState() == SudokuGame.GAME_STATE_COMPLETED) {
                mSudokuBoard.setReadOnly(true);
            }

            mSudokuBoard.setGame(mSudokuGame , mTranslator, activityInstance);
            mSudokuGame.setOnPuzzleSolvedListener(onSolvedListener);

            setClickListnersToInputButtons();
        }
        catch (Exception ex) {
            Log.e("Problem1", ex.getMessage());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(activityInstance, "Click on settings", Toast.LENGTH_LONG).show();
        }
        else if (id == R.id.action_show_errors)
        {
            mSudokuBoard.setHighlightWrongVals();
        }
        else if(id == R.id.action_show_restart)
        {
            mSudokuGame.reset();
            mSudokuGame.start();
            mSudokuBoard.setReadOnly(false);
            mGameTimer.start();
            toolbar.setTitle(mGameTimeFormatter.format(mSudokuGame.getTime()));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSudokuGame.resume();

        if (mShowTime) {
            mGameTimer.start();
        }

        updateTime();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            // FIXME: When activity is resumed, title isn't sometimes hidden properly (there is black
            // empty space at the top of the screen). This is desperate workaround.

            /* TO check later

            if (mFullScreen) {
                mGuiHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                        mRootLayout.requestLayout();
                    }
                }, 1000);
            }
            */

        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // we will save game to the database as we might not be able to get back
        mDatabase.updateSudoku(mSudokuGame);

        mGameTimer.stop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mDatabase.close();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mGameTimer.stop();

        if (mSudokuGame.getState() == SudokuGame.GAME_STATE_PLAYING) {
            mSudokuGame.pause();
        }

        mSudokuGame.saveState(outState);
        mGameTimer.saveState(outState);
    }

    //region METHODS

    protected void IntializeHashMapButtons()
    {
        this.mDictionnaryInputButtons.put(1, R.id.b1);
        this.mDictionnaryInputButtons.put(2, R.id.b2);
        this.mDictionnaryInputButtons.put(3, R.id.b3);
        this.mDictionnaryInputButtons.put(4, R.id.b4);
        this.mDictionnaryInputButtons.put(5, R.id.b5);
        this.mDictionnaryInputButtons.put(6, R.id.b6);
        this.mDictionnaryInputButtons.put(7, R.id.b7);
        this.mDictionnaryInputButtons.put(8, R.id.b8);
        this.mDictionnaryInputButtons.put(9, R.id.b9);
    }

    protected void setClickListnersToInputButtons()
    {
        Set<Integer> keys = mDictionnaryInputButtons.keySet();

        // Input buttons
        for(final int i : keys)
        {
            Button button = (Button)mInputButtons.findViewById(mDictionnaryInputButtons.get(i));

            //Adjust width
            button.setText((mTranslator != null) ? mTranslator.getTranslateNumberFromWord(i) : "-1");
            button.setWidth(mSudokuBoard.getWidth());

            //Add listners
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isNoteModeActive) {
                        onInputButtonClickedNoteMode(i);
                        BindCellToNotes();
                    } else {
                        onInputButtonClicked(i);
                        BindCellToNotes();
                    }
                }
            });
        }

        //Clear button
        ((Button)mInputButtons.findViewById(R.id.bClear)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cell mSelectedCell = mSudokuBoard.getSelectedCell();

                if(mSelectedCell.isEditable()) {
                    mSudokuBoard.getSelectedCell().setValue(0);
                    mSudokuBoard.getSelectedCell().setNote(CellNote.EMPTY);
                    BindCellToNotes();
                }
            }
        });

        //Note mode
        final Button bNode = (Button)mInputButtons.findViewById(R.id.bNoteMode);
        bNode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isNoteModeActive = !isNoteModeActive;
                if (bNode.getText().equals("N")) {
                    bNode.setText("N(A)");
                    BindCellToNotes();
                } else {
                    bNode.setText("N");

                    for (int i = 1; i <= 9; i++) {
                        Button button = (Button) mInputButtons.findViewById(mDictionnaryInputButtons.get(i));
                        if(mSelectedCell.getNote() != null && mSelectedCell.getNote().getNotedNumbers() != null) {
                            if (mSelectedCell.getNote().getNotedNumbers().contains(i)) {
                                button.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_button_pressed));
                            } else {
                                button.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_button));
                            }
                        }
                    }
                }

            }
        });

    }

    protected void onInputButtonClicked(int i)
    {

        mSelectedCell = mSudokuBoard.getSelectedCell();

        int number = 0;
        if(!mSelectedCell.isEditable()) {
            return;
        }
        try {
            if (i >= 0 && i <= 9) {
                number = (i == mSelectedCell.getValue()) ? 0 : i;
                mSudokuGame.setCellValue(mSelectedCell, number);
            }
        }
        catch (Exception ex)
        {
            Log.e("onInputButtonClicked" , ex.getMessage());
        }

    }

    private SudokuGame.OnPuzzleSolvedListener onSolvedListener = new SudokuGame.OnPuzzleSolvedListener() {

        @Override
        public void onPuzzleSolved() {
            mSudokuBoard.setReadOnly(true);
            showDialog(DIALOG_WELL_DONE);
        }

    };

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_WELL_DONE:
                return new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setTitle(R.string.well_done)
                        .setMessage(getString(R.string.congrats, mGameTimeFormatter.format(mSudokuGame.getTime())))
                        .setPositiveButton(android.R.string.ok, null)
                        .create();
            case DIALOG_RESTART:
                return new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_menu_rotate)
                        .setTitle(R.string.app_name)
                        .setMessage(R.string.restart_confirm)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Restart game
                                mSudokuGame.reset();
                                mSudokuGame.start();
                                mSudokuBoard.setReadOnly(false);
                                if (mShowTime) {
                                    mGameTimer.start();
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .create();
            case DIALOG_CLEAR_NOTES:
                return new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_menu_delete)
                        .setTitle(R.string.app_name)
                        .setMessage(R.string.clear_all_notes_confirm)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                mSudokuGame.clearAllNotes();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .create();
            case DIALOG_UNDO_TO_CHECKPOINT:
                return new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_menu_delete)
                        .setTitle(R.string.app_name)
                        .setMessage(R.string.undo_to_checkpoint_confirm)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                mSudokuGame.undoToCheckpoint();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .create();

        }
        return null;
    }

    protected void onInputButtonClickedNoteMode(int i)
    {
        mSelectedCell = mSudokuBoard.getSelectedCell();
        if(!mSelectedCell.isEditable()) {
            return;
        }

        int number = 0;
        try {
            if (i >= 0 && i <= 9) {
                number = (i == mSelectedCell.getValue()) ? 0 : i;
                mSudokuGame.setCellNote(mSelectedCell, mSelectedCell.getNote().toggleNumber(number));
            }
        }
        catch (Exception ex)
        {
            Log.e("onInputButtonClicked" , ex.getMessage());
        }

    }

    public void BindCellToNotes()
    {
        mSelectedCell = mSudokuBoard.getSelectedCell();
        if(isNoteModeActive) {
            for (int i = 1; i <= 9; i++) {
                Button button = (Button) mInputButtons.findViewById(mDictionnaryInputButtons.get(i));
                if(mSelectedCell.getNote() != null && mSelectedCell.getNote().getNotedNumbers() != null) {
                    if (mSelectedCell.getNote().getNotedNumbers().contains(i)) {
                        button.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_button_pressed));
                    } else {
                        button.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_button));
                    }
                }
            }
        }
        else
        {
            for (int i = 1; i <= 9; i++) {
                Button button = (Button) mInputButtons.findViewById(mDictionnaryInputButtons.get(i));
                if(mSelectedCell.getNote() != null && mSelectedCell.getNote().getNotedNumbers() != null) {
                    button.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_button));
                }
            }

        }

    }

    //endregion

    /**
     * Update the time of game-play.
     */
    void updateTime() {
        if (mShowTime) {
            toolbar.setTitle(mGameTimeFormatter.format(mSudokuGame.getTime()));
        } else {
            setTitle(R.string.app_name);
        }

    }

    // This class implements the game clock.  All it does is update the
    // status each tick.
    private final class GameTimer extends Timer {

        GameTimer() {
            super(1000);
        }

        @Override
        protected boolean step(int count, long time) {
            updateTime();

            // Run until explicitly stopped.
            return false;
        }

    }

}
