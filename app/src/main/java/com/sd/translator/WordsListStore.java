package com.sd.translator;

import android.annotation.SuppressLint;
import android.content.Context;

import com.sd.utils.tools.FileTools;
import com.sd.utils.tools.MetricellTools;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Admin on 29.02.2016.
 */
public class WordsListStore implements Serializable{

    private static final long serialVersionUID = -3505999484131343123L;
    public static final String WORDS_LIST = "com.sd.translator_words_list.ser";

    private Set<WordWrapper> mWords;

    private static WordsListStore mInstance = null;
    public synchronized static WordsListStore getInstance(Context c)
    {
        if(mInstance == null)
        {
            mInstance = new WordsListStore();
            mInstance.mWords = new HashSet<>();
            mInstance.loadQueue(c);
        }

        return mInstance;
    }

    public Set<WordWrapper> getWords() {
        return mWords;
    }

    public void setWords(Set<WordWrapper> mWords) {
        this.mWords = mWords;
    }

    @SuppressLint("UseSparseArrays")
    public synchronized void loadQueue(Context c) {

        try {
            if (FileTools.privateFileExists(c, WORDS_LIST)) {
                Object o = FileTools.loadObjectFromPrivateFile(c, WORDS_LIST);

                if (o == null) {
                    mInstance = new WordsListStore();
                    mInstance.mWords = new HashSet<>();
                } else {
                    mInstance = (WordsListStore)o;
                }
            } else {
                mInstance = new WordsListStore();
                mInstance.mWords= new HashSet<WordWrapper>();
            }

        } catch (ClassCastException cce) {
            // Oops, that shouldn't happen
            mInstance = new WordsListStore();
            MetricellTools.logException(getClass().getName(), cce);

        } catch (Exception e) {
            MetricellTools.logException(getClass().getName(), e);
            mInstance = new WordsListStore();
            mInstance.mWords = new HashSet<WordWrapper>();
        }
    }

    public synchronized void clearRecords() {
        this.mWords.clear();

    }
    public synchronized boolean isEmpty() {
        return mWords.isEmpty();
    }

    public synchronized void save(Context c) {

        try {
            FileTools.saveObjectToPrivateFile(c,WORDS_LIST, this, true);
        } catch (Exception e) {
            MetricellTools.logException(getClass().getName(), e);
        }

    }

}
