package com.ycxy.ymh.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.ycxy.ymh.learnenglish.R;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;

public class VideoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video);
        // hindeTarBar();
        Intent intent = getIntent();

        String url_video = intent.getStringExtra(MVListActivity.intent_video_url);
        String url_name = intent.getStringExtra(MVListActivity.intent_name_url);
        String url_image = intent.getStringExtra(MVListActivity.intent_image_url);

        JZVideoPlayerStandard jzVideoPlayerStandard = (JZVideoPlayerStandard) findViewById(R.id.videoplayer);
        jzVideoPlayerStandard.setUp(url_video
                , JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, url_name);

        Glide.with(this)
                .load(url_image)
                .into(jzVideoPlayerStandard.thumbImageView);

        // JZVideoPlayer.setVideoImageDisplayType(JZVideoPlayer.VIDEO_IMAGE_DISPLAY_TYPE_FILL_PARENT);
        // JZVideoPlayer.FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        // JZVideoPlayer.NORMAL_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    private void hindeTarBar() {
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(option);
    }

    @Override
    public void onBackPressed() {
        // MVListActivity.isMVMode = false;
        if (JZVideoPlayer.backPress()) {
            return;
        }
        // finish();
        super.onBackPressed();
    }


    @Override
    protected void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }
}
