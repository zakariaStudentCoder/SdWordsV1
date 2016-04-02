package com.sd.gui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.sd.utils.CommonResources;
import com.sd.utils.tools.UITools;
import com.sd.v1.R;

/**
 * Created by Admin on 31.03.2016.
 */
public class WordDefDialog extends Dialog implements android.view.View.OnClickListener{

    private Activity activity;
    private Dialog dialog;
    private Button okButton;
    private String mWord;
    private String mDesc;

    private TextView mTextWord;
    private TextView mTextDescritpion;



    public WordDefDialog(Activity a , String word , String definition) {
        super(a);
        // TODO Auto-generated constructor stub
        this.activity = a;
        this.mWord = word;
        this.mDesc = definition;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.word_def_dialog);

        CommonResources.loadResources(activity);
        View v  = getWindow().getDecorView().findViewById(android.R.id.content);
        UITools.applyTypeface(CommonResources.getNormalTypeface(), v);

        okButton = (Button) findViewById(R.id.btnOK);
        mTextWord = (TextView)findViewById(R.id.textWord);
        mTextDescritpion = (TextView)findViewById(R.id.textDefinition);

        mTextWord.setText(mWord);
        mTextDescritpion.setText(mDesc);
        okButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnOK: {
                dismiss();
                break;
            }
        }
    }

}
