package com.ycxy.ymh.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.ycxy.ymh.learnenglish.R;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;

public class VideoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        Intent intent = getIntent();

        String url_video = intent.getStringExtra(MVListActivity.intent_video_url);
        String url_name = intent.getStringExtra(MVListActivity.intent_name_url);
        String url_image = intent.getStringExtra(MVListActivity.intent_image_url);

        JZVideoPlayerStandard jzVideoPlayerStandard = (JZVideoPlayerStandard) findViewById(R.id.videoplayer);
        jzVideoPlayerStandard.setUp(url_video
                , JZVideoPlayerStandard.SCREEN_WINDOW_FULLSCREEN, url_name);

        Glide.with(this)
                .load(url_image)
                .into(jzVideoPlayerStandard.thumbImageView);

    }

    @Override
    public void onBackPressed() {
        if (JZVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }
    @Override
    protected void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }
}
