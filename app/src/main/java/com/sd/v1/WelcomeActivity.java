package com.sd.v1;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Timer;
import com.sd.utils.ButtonSupport;
import com.sd.utils.CommonResources;
import com.sd.utils.tools.UITools;

import java.util.TimerTask;

public class WelcomeActivity extends Activity {

    private Button bWords;
    private Button bNumbers;
    private Button bRate;

    private Timer timer;
    private MyTimerTask myTimerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_welcome);

        CommonResources.loadResources(this);
        View v  = getWindow().getDecorView().findViewById(android.R.id.content);
        UITools.applyTypeface(CommonResources.getNormalTypeface(), v);

        bWords = (Button)findViewById(R.id.btnWords);
        bNumbers = (Button)findViewById(R.id.btnNumbers);
        bRate = (Button)findViewById(R.id.btnRate);

        addListnersToButtons();
        showButtons();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        super.onWindowFocusChanged(hasFocus);
        animateStartIcon();
    }

    public  void addListnersToButtons()
    {

        // Adding listners to the different buttons
        // of this activity .

        bWords.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(WelcomeActivity.this, WordsList.class);
                startActivity(i);
            }
        });

        bNumbers.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(WelcomeActivity.this, LevelsTabsActivity.class);
                startActivity(i);
            }
        });

        bRate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });
    }

    private void animateStartIcon()
    {
        final ImageView mImageViewFilling = (ImageView) findViewById(R.id.img_animation_start);
        final AnimationDrawable animation = (AnimationDrawable) mImageViewFilling.getBackground();

        mImageViewFilling.post( new Runnable()
        {
            @Override
            public void run() {
                animation.start();
            }
        });

        //Calculate the total duration
        int duration = 0;
        for(int i = 0; i < animation.getNumberOfFrames(); i++){
            duration += animation.getDuration(i);
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showButtons();
            }
        }, 10000);
    }

    private void showButtons() {
        setAnimatedVisibility(bWords);
        setAnimatedVisibility(bNumbers);
        setAnimatedVisibility(bRate);
    }

    private void setAnimatedVisibility(Button button)
    {
        button.setVisibility(View.VISIBLE);
    }


    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            timer.cancel();
            runOnUiThread(new Runnable()
            {
                @Override
                public void run() {
                    showButtons();
                }
            });
        }
    }

}
