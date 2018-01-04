package com.ycxy.ymh.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ycxy.ymh.learnenglish.IAudioPlayService;
import com.ycxy.ymh.learnenglish.R;
import com.ycxy.ymh.service.AudioPlayService;
import com.ycxy.ymh.utils.Utils;

public class AudioActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int SHOWAUDIONAME = 0;
    private static final int UPDATAUI = 2;
    private ImageView iv_cd;
    private ImageView iv_handler;
    private Button btn_play;
    private Button btn_next;
    private Button btn_pre;
    private Button btn_mode;
    private Button btn_menu;
    private SeekBar seekbar;
    private TextView tv_show_Time;

    private boolean isPlaying = true;
    private int position = 0;
    private Animation operatingAnim;
    private RotateAnimation rotate;

    private IAudioPlayService service;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOWAUDIONAME:
                    break;
                case UPDATAUI:
                    updataUI();
                    break;
            }
        }
    };
    private boolean isPlayCD = false;

    private void updataUI() {
        try {
            updataBtnPlay();
            Utils utils = new Utils();
            tv_show_Time.setText(utils.stringForTime(service.getCurrentPosition())
            + "|" + utils.stringForTime(service.getDuration()));
            seekbar.setMax(service.getDuration());
            seekbar.setProgress(service.getCurrentPosition());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        handler.removeMessages(UPDATAUI);
        handler.sendEmptyMessageDelayed(UPDATAUI, 100);
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            service = IAudioPlayService.Stub.asInterface(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        clsTilte();
        initView();
        initData();
    }

    private void initData() {
        startService();
        handler.sendEmptyMessageDelayed(UPDATAUI, 100);
    }

    private void startService() {
        Intent intent = new Intent(this, AudioPlayService.class);
        bindService(intent, conn, BIND_AUTO_CREATE);
    }

    private void initView() {
        btn_play = findViewById(R.id.btn_audio_play);
        btn_mode = findViewById(R.id.btn_audio_type);
        btn_pre = findViewById(R.id.btn_audio_next);
        btn_next = findViewById(R.id.btn_audio_next);
        tv_show_Time = findViewById(R.id.tv_show_time);
        seekbar = findViewById(R.id.seekbar);

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
        btn_pre.setOnClickListener(this);
        btn_mode.setOnClickListener(this);
        btn_next.setOnClickListener(this);
//        btn_menu.setOnClickListener(this);

        seekbar.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());
        handler.sendEmptyMessageDelayed(SHOWAUDIONAME, 100);
    }

    /**
     * 去除顶部的标题栏
     */
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
                play_method();
                isPlaying = !isPlaying;
                break;
            case R.id.btn_audio_type:
                break;
            case R.id.btn_audio_pre:
                try {
                    service.pre();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_audio_next:
                try {
                    service.next();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_audio_menu:
                break;
        }
    }

    /**
     * 封装了播放的逻辑
     */
    private void play_method() {
        try {
            if (service.isNull()) {
                openAudio();
            }

            if (service.isPlaying()) {
                stopPlayCD();
                service.pause();
                btn_play.setBackgroundResource(R.mipmap.pause);
                btn_play.setBackgroundResource(R.drawable.btn_audio_pause_selector);
            } else {
                startPlayCD();
                service.start();
                btn_play.setBackgroundResource(R.mipmap.play);
                btn_play.setBackgroundResource(R.drawable.btn_audio_play_selector);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void stopPlayCD() {
        iv_cd.clearAnimation();
        iv_handler.clearAnimation();
    }

    private void startPlayCD() {
        if (operatingAnim != null && rotate != null) {
            iv_cd.startAnimation(operatingAnim);
            iv_handler.startAnimation(rotate);
        }
    }

    /**
     * 打开播放音频文件路径
     */
    private void openAudio() {
        if (service != null) {
            try {
                service.openAudio(position);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 更新播放按钮
     *
     * @throws RemoteException
     */
    private void updataBtnPlay() throws RemoteException {
        if (!service.isPlaying()) {
            stopPlayCD();
            btn_play.setBackgroundResource(R.mipmap.pause);
        } else {
            if (isPlayCD) {
                startPlayCD();
            }
            isPlayCD = false;
            btn_play.setBackgroundResource(R.mipmap.play);
        }
    }

    private class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            if (b) {
                try {
                    service.seekTo(i);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try{
            if (service!=null) {
                if (service.isPlaying()){
                    isPlayCD = true;
                }
            }
        }catch (Exception e){

        }
    }
}
