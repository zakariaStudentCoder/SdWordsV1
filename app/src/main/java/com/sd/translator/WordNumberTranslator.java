package com.sd.translator;

import android.util.Log;

import java.util.Arrays;

/**
 * Created by Admin on 17.02.2016.
 */
public class WordNumberTranslator {

    private String mSodukuWord;
    private char[] mCharArrayFromWord;
    private boolean isTranslatorActive = false;

    public WordNumberTranslator(boolean isTranslatorActive, String sodukuWord) {
        this.isTranslatorActive = isTranslatorActive;

        if (isTranslatorActive) {
            this.mSodukuWord = sodukuWord;
            try {
                mCharArrayFromWord = ToCharArrayFromWord(sodukuWord);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getTranslateNumberFromWord(int number) {
        int index = 0;
        try {
            if (isTranslatorActive) {
                if (number == 0) {
                    return "-1";
                }

                return mCharArrayFromWord[number - 1] + "";
            } else {
                return Integer.toString(number);
            }
        } catch (Exception e) {
            Log.e("Problem2",  "Character.forDigit(number, 10)" + Character.forDigit(number, 10) + "\n"+
                               "index : " + index + "\n" +
                               "number : " + number + "\n" +
                               e.getMessage());
            return "0";
        }
    }



    public char[] ToCharArrayFromWord(String word) throws Exception {

        char[] result = word.toCharArray();

        if(result.length != 9 )
        {
            throw  new Exception("Invalid word . The word must contain 9 chars");
        }
        return  result;
    }

}
