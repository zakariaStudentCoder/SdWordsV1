package com.sd.v1.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.sd.db.SudokuColumns;
import com.sd.db.SudokuDatabase;
import com.sd.db.SudokuTableObject;
import com.sd.games.CellCollection;
import com.sd.games.SudokuGame;
import com.sd.gui.GameTimeFormat;
import com.sd.gui.SdWordsGameView;
import com.sd.gui.SudokuListFilter;
import com.sd.gui.dialogs.WordDefDialog;
import com.sd.translator.WordWrapper;
import com.sd.utils.GridLevelAdapter;
import com.sd.utils.GridLevelItem;
import com.sd.v1.FolderDetailLoader;
import com.sd.v1.LevelsTabsActivity;
import com.sd.v1.R;
import com.sd.v1.SdWordPlayActivity;
import com.sd.v1.WordsList;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LevelListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LevelListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LevelListFragmentGrid extends Fragment {
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


    GridView gridview;
    GridLevelAdapter gridviewAdapter;
    ArrayList<SudokuTableObject> data = new ArrayList<SudokuTableObject>();



    public LevelListFragmentGrid() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LevelListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LevelListFragmentGrid newInstance( String word , String description , int level ) {
        LevelListFragmentGrid fragment = new LevelListFragmentGrid();
        Bundle args = new Bundle();

        args.putInt(LEVEL, level);
        args.putString(WordsList.WORD, word);
        args.putString(WordsList.WORD_DESCRIPTION, description);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mLevel = getArguments().getInt(LEVEL);
            mWord = getArguments().getString(WordsList.WORD);
            mDescrption = getArguments().getString(WordsList.WORD_DESCRIPTION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.level_grid, container, false);

        gridview = (GridView)view.findViewById(R.id.gridView);

        mDatabase = new SudokuDatabase(getActivity().getApplicationContext());

        mListFilter = new SudokuListFilter(getActivity().getApplicationContext());

        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        mListFilter.showStateNotStarted = settings.getBoolean(FILTER_STATE_NOT_STARTED, true);
        mListFilter.showStatePlaying = settings.getBoolean(FILTER_STATE_PLAYING, true);
        mListFilter.showStateCompleted = settings.getBoolean(FILTER_STATE_SOLVED, true);


        data =  mDatabase.getSodukuArrayList(mLevel, mListFilter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GridLevelAdapter.ViewHolder vw = (GridLevelAdapter.ViewHolder)view.getTag();
                Object obj = gridview.getItemAtPosition(vw.id);
                if(obj instanceof SudokuTableObject)
                {
                    if(((SudokuTableObject) obj).getState() == SudokuGame.GAME_STATE_PLAYING)
                    {
                        playSudoku(((SudokuTableObject) obj).getId());
                    }
                    else
                    {

                        Activity act = LevelListFragmentGrid.this.getActivity();
                        WordDefDialog cdd = new WordDefDialog(act , "Note" , "Please solve the previous problem first . Thanks !!");
                        cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        Display display =((WindowManager)act.getSystemService(act.WINDOW_SERVICE)).getDefaultDisplay();
                        int width = display.getWidth();
                        int height=display.getHeight();

                        cdd.show();
                        cdd.getWindow().setLayout((6 * width) / 7, (4 * height) / 5);

                        return;
                    }
                }

            }
        });

        setDataAdapter();
        return view;
    }

    // Set the Data Adapter
    private void setDataAdapter()
    {
        gridviewAdapter = new GridLevelAdapter(getActivity().getApplicationContext(), mLevel, data);
        gridview.setAdapter(gridviewAdapter);
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

}
