package com.sd.v1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.UserDictionary;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sd.db.SudokuDatabase;
import com.sd.db.WordsColumns;
import com.sd.translator.WordWrapper;
import com.sd.translator.WordsGenerator;
import com.sd.translator.WordsListStore;
import com.sd.utils.CommonResources;
import com.sd.utils.tools.UITools;

import java.sql.Wrapper;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WordsList extends Activity {

    private ListView mListView;
    private SudokuDatabase mDatabase;
    public static final String WORD = "word_name";
    public static final String WORD_DESCRIPTION = "word_description";

    private WordsListStore mWordStore;
    private List<WordWrapper> mWordListToAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_words_list);



        CommonResources.loadResources(this);
        View v  = getWindow().getDecorView().findViewById(android.R.id.content);
        UITools.applyTypeface(CommonResources.getNormalTypeface(), v);
        
        mDatabase = new SudokuDatabase(getApplicationContext());
        mWordStore = WordsListStore.getInstance(getApplicationContext());
        populateWordsList();

        mListView = (ListView)findViewById(R.id.wordsList);
        mWordListToAdapter = new ArrayList<WordWrapper>(mWordStore.getWords());

        WordsAdapter adapter = new WordsAdapter(WordsList.this , mWordListToAdapter);
        mListView.setAdapter(adapter);


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                Object w = mListView.getItemAtPosition(position);
                if(w instanceof  WordWrapper)
                {
                    Intent intent = new Intent(getBaseContext(), LevelsTabsActivity.class);

                    intent.putExtra(WORD, ((WordWrapper) w).getWord());
                    intent.putExtra(WORD_DESCRIPTION, ((WordWrapper) w).getDefinition());

                    startActivity(intent);
                }
            }
        });
    }


    private void populateWordsList()
    {
        if(mWordStore != null)
        {
            mWordStore = WordsListStore.getInstance(getApplication());
        }

        Set<WordWrapper> wordSet = mWordStore.getWords();

        if( wordSet != null) {
            for (WordWrapper w : WordsGenerator.getWordsList())
            {
                wordSet.add(w);
            }
        }
    }


    //region ADAPTERS

    public class WordsAdapter extends ArrayAdapter<WordWrapper> {

        public WordsAdapter(Context context, List<WordWrapper> words) {
            super(context, 0, words);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.word_list_item,parent, false);
            }

            WordsHolder viewHolder = (WordsHolder) convertView.getTag();
            if(viewHolder == null){
                viewHolder = new WordsHolder();
                viewHolder.text = (TextView) convertView.findViewById(R.id.textWord);
                convertView.setTag(viewHolder);
            }

            WordWrapper word = getItem(position);


            viewHolder.text.setText(word.getWord());

            return convertView;
        }

        private class WordsHolder{
            public TextView text;
        }
    }

    //endregion


}
