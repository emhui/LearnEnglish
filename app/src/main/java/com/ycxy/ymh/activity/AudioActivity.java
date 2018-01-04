package com.ycxy.ymh.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;

import com.ycxy.ymh.learnenglish.R;

public class AudioActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView iv_cd;
    private ImageView iv_handler;
    private Button btn_play;
    private boolean isPlaying = true;
    private Animation operatingAnim;
    private RotateAnimation rotate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        clsTilte();
        initView();
    }

    private void initView() {
        btn_play = findViewById(R.id.btn_audio_play);
        iv_cd = findViewById(R.id.iv_cd);
        iv_handler = findViewById(R.id.iv_handler);

        operatingAnim = AnimationUtils.loadAnimation(this, R.anim.tip);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);

        rotate = new RotateAnimation(0f, 28f, Animation.RELATIVE_TO_SELF,
                0.8f, Animation.RELATIVE_TO_SELF, 0.1f);
        rotate.setDuration(1000);
        rotate.setFillAfter(true);

        btn_play.setOnClickListener(this);


    }

    private void clsTilte() {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_audio_play:
                if (isPlaying) {
                    if (operatingAnim != null && rotate != null) {
                        iv_cd.startAnimation(operatingAnim);
                        iv_handler.startAnimation(rotate);
                    }
                    btn_play.setBackgroundResource(R.drawable.btn_audio_pause_selector);
                } else {
                    iv_cd.clearAnimation();
                    iv_handler.clearAnimation();
                    btn_play.setBackgroundResource(R.drawable.btn_audio_play_selector);
                }
                isPlaying = !isPlaying;
                break;
        }
    }
}
