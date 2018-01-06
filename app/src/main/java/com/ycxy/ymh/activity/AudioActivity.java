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
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.ycxy.ymh.bean.Audio;
import com.ycxy.ymh.bean.LyricBean;
import com.ycxy.ymh.learnenglish.IAudioPlayService;
import com.ycxy.ymh.learnenglish.R;
import com.ycxy.ymh.service.AudioPlayService;
import com.ycxy.ymh.utils.Constants;
import com.ycxy.ymh.utils.Utils;
import com.ycxy.ymh.view.LyricView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;


import okhttp3.Call;

public class AudioActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int SHOWAUDIONAME = 0;
    private static final int UPDATAUI = 2;
    private static final String TAG = "AudioActivity";
    private static final int FAILED = 3;
    private static final int LYRICLOADSUCCESS = 4;
    private ImageView iv_cd;
    private ImageView iv_handler;
/*    private Button btn_play;
    private Button btn_next;
    private Button btn_pre;
    private Button btn_mode;  */
    private ImageView btn_play;
    private ImageView btn_next;
    private ImageView btn_pre;
    private ImageView btn_mode;
    private Button btn_menu;
    private SeekBar seekbar;
    private TextView tv_show_Time;
    private TextView tv_show_name;
    private RelativeLayout rr_cd;
    private boolean isPlaying = true;
    private int position = 0;
    private Animation operatingAnim;
    private RotateAnimation rotate;
    private LyricView lyricView;
    private IAudioPlayService service;
    private Utils utils = new Utils();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOWAUDIONAME:
                    break;
                case UPDATAUI:
                    updataUI();
                    handler.removeMessages(UPDATAUI);
                    handler.sendEmptyMessageDelayed(UPDATAUI, 100);
                    break;
                case FAILED:
                    Toast.makeText(AudioActivity.this,
                            "未找到歌词", Toast.LENGTH_SHORT);
                    break;
                // 音频下载成功
                case LYRICLOADSUCCESS:
                    String audioName = null;
                    try {
                        audioName = utils.getAudioName(service.getName());
                        File file = new File(utils.nameToPath(audioName));
                        lyricView.setLyricFile(file);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };
    private boolean isPlayCD = false;
    private Audio audio = null;

    private void updataUI() {
        try {
            updataBtnPlay();
            lyricView.setCurrentTimeMillis(service.getCurrentPosition());
            tv_show_name.setText(service.getName().split("\\.")[0]);
            tv_show_Time.setText(utils.stringForTime(service.getCurrentPosition())
                    + "/" + utils.stringForTime(service.getDuration()));
            seekbar.setMax(service.getDuration());
            seekbar.setProgress(service.getCurrentPosition());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
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
        utils.hasFile(); // 建立一个存放歌词的文件目录
        clsTilte();
        initView();
        initData();
    }

    private void initData() {
        EventBus.getDefault().register(this);
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
        btn_pre = findViewById(R.id.btn_audio_pre);
        btn_next = findViewById(R.id.btn_audio_next);
        btn_menu = findViewById(R.id.btn_audio_menu);
        tv_show_Time = findViewById(R.id.tv_show_time);
        tv_show_name = findViewById(R.id.tv_show_name);
        seekbar = findViewById(R.id.seekbar);
        lyricView = findViewById(R.id.lyric);
        rr_cd = findViewById(R.id.rr_cd);
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
        btn_menu.setOnClickListener(this);

        seekbar.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());
        handler.sendEmptyMessageDelayed(SHOWAUDIONAME, 100);
        // 原来lyric
        lyricView.setOnPlayerClickListener(new LyricView.OnPlayerClickListener() {
            @Override
            public void onPlayerClicked(long l, String s) {
                Toast.makeText(AudioActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        });
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
                switchCDorLyric();
                isPlayCD = !isPlayCD;
                break;
        }
    }

    private void switchCDorLyric() {
        try {
            if (!isPlayCD) {
                getLyric();
                lyricView.setVisibility(View.VISIBLE);
                rr_cd.setVisibility(View.GONE);
//                btn_menu.setBackgroundResource(R.drawable.btn_audio_show_lyric_stop_selector);
                btn_menu.setBackgroundResource(R.drawable.btn_audio_show_lyric_stop_selector);
            } else {
                lyricView.setVisibility(View.GONE);
                rr_cd.setVisibility(View.VISIBLE);
                btn_menu.setBackgroundResource(R.drawable.btn_audio_show_lyric_selector);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取歌词
     * @throws RemoteException
     */
    private void getLyric() throws RemoteException {
        // 获取真正名字The Piano Guys-Because of You.mp3
        String audioName = utils.getAudioName(service.getName());
        Log.d(TAG, "switchCDorLyric: " + audioName);
        // 判断本地是否已存在该歌曲
        if (utils.isLyricExit(audioName)) {
            // 存在直接将歌词放入
            File file = new File(utils.nameToPath(audioName));
            Log.d(TAG, "switchCDorLyric: " + file.getAbsolutePath());
            lyricView.setLyricFile(file);
        } else {
            // 从网络加载歌词
            loadLyricFormNet();
        }
    }


    /**
     * 封装了播放的逻辑
     */
    private void play_method() {
        try {
            if (isMediaPlayerNull()) {
                openAudio();
            }
            if (isPlaying()) {
                playAudio();
            } else {
                pauseAudio();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 是否有mediaplayer对象
     *
     * @return
     * @throws RemoteException
     */
    private boolean isMediaPlayerNull() throws RemoteException {
        return service.isNull();
    }

    /**
     * 当前歌曲是否正在播放
     *
     * @return
     * @throws RemoteException
     */
    private boolean isPlaying() throws RemoteException {
        return service.isPlaying();
    }

    /**
     * 停止音频
     *
     * @throws RemoteException
     */
    private void pauseAudio() throws RemoteException {
        startPlayCD();
        service.start();
        setBtnPlay();
    }

    /**
     * 设置按键样式为play
     */
    private void setBtnPlay() {
        btn_play.setBackgroundResource(R.drawable.btn_play_selector);
    }


    /**
     * 播放音频
     *
     * @throws RemoteException
     */
    private void playAudio() throws RemoteException {
        stopPlayCD();
        service.pause();
        setBtnPause();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updataCDandLyric(Audio audio){
        startPlayCD();
        try {
            getLyric();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置播放按键样式为pause
     */
    private void setBtnPause() {
        btn_play.setBackgroundResource(R.drawable.btn_pause_selector);
    }

    /**
     * 开始停止CD
     */
    private void stopPlayCD() {
        iv_cd.clearAnimation();
        iv_handler.clearAnimation();
    }


    public void startPlayCD() {
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
            setBtnPlay();
        } else {
/*            if (isPlayCD) {
                startPlayCD();
                isPlayCD = false;
            }*/
            setBtnPause();
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
        startPlayCD();
/*        try {
            if (service != null) {
                if (service.isPlaying()) {
                    startPlayCD();
                }
            }
        } catch (Exception e) {

        }*/
    }

    /**
     * 从网络加载歌词
     */
    private void loadLyricFormNet() {
        try {
            Log.d(TAG, "loadLyricFormNet: "+Constants.LYRICAPI + utils.getAudioName(service.getName()));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            OkHttpUtils
                    .get()
                    .url(Constants.LYRICAPI + utils.getAudioName(service.getName()))
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            Log.d(TAG, "onError: ");
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            Log.d(TAG, "onResponse: " + response);
                            parseJSON(response);
                        }
                    });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    LyricBean bean;

    /**
     * 序列化数据
     *
     * @param response
     */
    private void parseJSON(String response) {
        bean = JSON.parseObject(response, LyricBean.class);
        Log.d(TAG, "parseJSON: " + bean.getCount());
        // 判断是否有歌词资源
        if (bean.getCount() > 0) {
            // 开始下载歌词
            String url = bean.getResult().get(0).getLrc();
            Log.d(TAG, "parseJSON: " + url);
            loadLyricFormNet(url);
        } else {
            handler.sendEmptyMessage(FAILED);
        }
    }

    /**
     * 下载歌词到本地
     *
     * @param url
     */
    private void loadLyricFormNet(String url) {
        try {
            OkHttpUtils//
                    .get()//
                    .url(url)//
                    .build()//
                    .execute(new FileCallBack(Constants.STROAGEPATH, utils.getAudioName(service.getName()) + ".lrc")//
                    {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            Log.d(TAG, "onError: ---------------" + e.toString());
                        }

                        @Override
                        public void onResponse(File response, int id) {
                            Log.d(TAG, "onResponse: ");
                            // readFile(response);
                            handler.sendEmptyMessage(LYRICLOADSUCCESS);
                        }
                    });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
