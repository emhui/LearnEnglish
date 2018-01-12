package com.ycxy.ymh.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
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
import com.ycxy.ymh.bean.ResultBean;
import com.ycxy.ymh.learnenglish.IAudioPlayService;
import com.ycxy.ymh.learnenglish.MainActivity;
import com.ycxy.ymh.learnenglish.R;
import com.ycxy.ymh.service.AudioPlayService;
import com.ycxy.ymh.utils.Constants;
import com.ycxy.ymh.utils.HeadSetUtil;
import com.ycxy.ymh.utils.Utils;
import com.ycxy.ymh.view.LyricView;
import com.ycxy.ymh.view.MyRelativeLayout;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;


import okhttp3.Call;

public class AudioActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int SHOWAUDIONAME = 0;
    private static final int UPDATAUI = 2;
    private static final String TAG = "AudioActivity";
    private static final int FAILED = 3;
    private static final int LYRICLOADSUCCESS = 4;
    private ImageView iv_cd;
    private ImageView iv_handler;
    private ImageView btn_play;
    private ImageView btn_next;
    private ImageView btn_pre;
    private ImageView btn_mode;
    private Button btn_menu;
    private SeekBar seekbar;
    private TextView tv_show_Time;
    private TextView tv_show_name;
    private TextView tv_null;
    private MyRelativeLayout rr_cd;
    private boolean isPlaying = true;
    private int position = 0;
    private Animation operatingAnim;
    private RotateAnimation rotate;
    private RotateAnimation rotateBack;
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
                    lyricView.reset();
                    break;
                // 歌词下载成功
                case LYRICLOADSUCCESS:
                    String audioName = null;
                    try {
                        audioName = utils.getAudioName(service.getName());
                        File file = new File(utils.nameToPath(audioName));
                        if (file.exists()) {
                            lyricView.setLyricFile(file);
                        } else {
                            lyricView.reset();
                        }

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };
    private boolean isPlayCD = false;
    private Audio audio = null;
    private Uri uri = null;
    private ObjectAnimator objectAnimator;
    private Float currentValue = 0f;


    private void updataUI() {
        try {
            updataBtnPlay();
            lyricView.setCurrentTimeMillis(service.getCurrentPosition());
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
            getData();
            HeadSetUtil.getInstance().setOnHeadSetListener(headSetListener);
            HeadSetUtil.getInstance().open(AudioActivity.this);
            try {
                tv_show_name.setText(new Utils().getAudioName(service.getName()));
                if (service.isPlaying()) {
                    startPlayCD();
                    showPlaymode();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    HeadSetUtil.OnHeadSetListener headSetListener = new HeadSetUtil.OnHeadSetListener() {
        @Override
        public void onDoubleClick() {
            next();
        }

        @Override
        public void onClick() {
            play_method();
        }

        @Override
        public void onThreeClick() {
            pre();
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
        // iv_cd.setBackgroundResource(R.drawable.btn_shuffle_selector);
        EventBus.getDefault().register(this);
        startService();
        position = new Utils().getPosfStor(this);
        handler.sendEmptyMessageDelayed(UPDATAUI, 100);
    }

    private void getData() {
        uri = getIntent().getData();
        try {
            String songPath = getRealPathFromURI(uri);
            Log.d(TAG, "getData: " + songPath);
            service.openOtherAudio(songPath);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            ;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
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

        animation();

        btn_play.setOnClickListener(this);
        btn_pre.setOnClickListener(this);
        btn_mode.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        btn_menu.setOnClickListener(this);
        // rr_cd.setOnClickListener(this);
        lyricView.setOnClickListener(this);

        seekbar.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());
        handler.sendEmptyMessageDelayed(SHOWAUDIONAME, 100);
        // 原来lyric
        lyricView.setOnPlayerClickListener(new LyricView.OnPlayerClickListener() {
            @Override
            public void onPlayerClicked(long l, String s) {
                try {
                    service.seekTo((int) l);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        lyricView.setOnPlayerSingleClickListener(new LyricView.OnPlayerSingleClickListener() {
            @Override
            public void onPlayerSingleClicked() {
                showCD();
            }
        });

        rr_cd.setOnSwipeListener(new MyRelativeLayout.OnSwipeListener() {
            @Override
            public void setOnSwipeListener(double distanceX) {
                if (distanceX > 0) {
                    pre();
                }

                if (distanceX < 0) {
                    next();
                }
            }
        });

        rr_cd.setOnClickListener(new MyRelativeLayout.OnClickListener() {
            @Override
            public void setOnClickListener() {
                try {
                    showLyric();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void animation() {

        objectAnimator = ObjectAnimator.ofFloat(iv_cd, "rotation", 0f, 360f);//添加旋转动画，旋转中心默认为控件中点
        objectAnimator.setDuration(3000);//设置动画时间
        objectAnimator.setInterpolator(new LinearInterpolator());//动画时间线性渐变
        objectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        objectAnimator.setRepeatMode(ObjectAnimator.RESTART);

        operatingAnim = AnimationUtils.loadAnimation(this, R.anim.tip);
        // operatingAnim.setStartOffset(1000);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);

        rotate = new RotateAnimation(0f, 28f, Animation.RELATIVE_TO_SELF,
                0.8f, Animation.RELATIVE_TO_SELF, 0.1f);
        rotate.setDuration(1000);
        rotate.setFillAfter(true);

        rotateBack = new RotateAnimation(28f, 0f, Animation.RELATIVE_TO_SELF,
                0.8f, Animation.RELATIVE_TO_SELF, 0.1f);
        rotateBack.setDuration(1000);
        rotateBack.setFillAfter(true);
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
                setPlaymode();
                break;
            case R.id.btn_audio_pre:
                pre();
                break;
            case R.id.btn_audio_next:
                next();
                break;
            case R.id.btn_audio_menu:
                switchCDorLyric();

                break;
        }
    }

    private void showPlaymode() {
        try {
            int mode = service.getPlayMode();
            if (mode == AudioPlayService.REPEAT_ALL) {
                btn_mode.setBackgroundResource(R.drawable.btn_all_repeat_selector);
            } else if (mode == AudioPlayService.REPEAT_SINGLE) {
                btn_mode.setBackgroundResource(R.drawable.btn_one_repeat_selector);
            } else if (mode == AudioPlayService.REPEAT_RANDOM) {
                btn_mode.setBackgroundResource(R.drawable.btn_shuffle_selector);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPlaymode() {
        try {
            int playmode = service.getPlayMode();
            if (playmode == AudioPlayService.REPEAT_ALL) {
                playmode = AudioPlayService.REPEAT_SINGLE;
            } else if (playmode == AudioPlayService.REPEAT_SINGLE) {
                playmode = AudioPlayService.REPEAT_RANDOM;
            } else if (playmode == AudioPlayService.REPEAT_RANDOM) {
                playmode = AudioPlayService.REPEAT_ALL;
            }
            //保持
            service.setPlayMode(playmode);

            //设置图片
            showPlaymode();

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void pre() {
        try {
            service.pre();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void next() {
        try {
            service.next();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 切换CD和歌词界面
     */
    private void switchCDorLyric() {
        try {
            if (!isPlayCD) {
                showLyric();
            } else {
                showCD();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showCD() {
        lyricView.setVisibility(View.GONE);
        rr_cd.setVisibility(View.VISIBLE);
        btn_menu.setBackgroundResource(R.drawable.btn_audio_show_lyric_selector);
        isPlayCD = !isPlayCD;
    }

    private void showLyric() throws RemoteException {
        getLyric();
        lyricView.setVisibility(View.VISIBLE);
        rr_cd.setVisibility(View.GONE);
        btn_menu.setBackgroundResource(R.drawable.btn_audio_show_lyric_stop_selector);
        isPlayCD = !isPlayCD;
    }

    /**
     * 获取歌词
     *
     * @throws RemoteException
     */
    private void getLyric() throws RemoteException {
        // 获取真正名字T he Piano Guys-Because of You.mp3
        String audioName = utils.getAudioName(service.getName());
        // 判断本地是否已存在该歌曲
        if (utils.isLyricExit(audioName)) {
            // 成功找到消息，更新UI
            handler.sendEmptyMessage(LYRICLOADSUCCESS);
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

    /**
     * 每次成功播放音乐调用这个方法
     * 开始播放CD，查找歌词
     *
     * @param audio
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updataCDandLyric(Audio audio) {
        try {
            int playmode = service.getPlayMode();

            if (playmode == AudioPlayService.REPEAT_ALL) {
                btn_mode.setBackgroundResource(R.drawable.btn_all_repeat_selector);
            } else if (playmode == AudioPlayService.REPEAT_SINGLE) {
                btn_mode.setBackgroundResource(R.drawable.btn_one_repeat_selector);
            } else if (playmode == AudioPlayService.REPEAT_ALL) {
                btn_mode.setBackgroundResource(R.drawable.btn_shuffle_selector);
            }

            tv_show_name.setText(new Utils().getAudioName(service.getName()));
            getLyric();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void startCDMsg(Constants constants) {
        startPlayCD();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void stopCDMsg(Utils utils) {
        stopPlayCD();
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
        if (operatingAnim != null && rotateBack != null) {
            iv_cd.clearAnimation();
            iv_handler.startAnimation(rotateBack);
        }
    }


    public void startPlayCD() {
        if (objectAnimator != null && rotate != null) {
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

            setBtnPlay();
        } else {
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
        // startPlayCD();
    }

    /**
     * 从网络加载歌词
     */
    private void loadLyricFormNet() {
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
                            parseJSON(response);
                        }
                    });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    LyricBean bean;
    int lyricNum = 0;
    int lyricIndex = 0;
    List<ResultBean> lyricUrlList;

    /**
     * 序列化数据
     *
     * @param response
     */
    private void parseJSON(String response) {
        lyricNum = 0;
        lyricIndex = 0;
        bean = JSON.parseObject(response, LyricBean.class);
        lyricNum = bean.getCount();
        // 判断是否有歌词资源
        if (bean.getCount() > 0) {
            // 获取歌词的url
            lyricUrlList = bean.getResult();
            String url = lyricUrlList.get(lyricIndex).getLrc();
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
                            if (isRightLyric(response)) {
                                handler.sendEmptyMessage(LYRICLOADSUCCESS);
                            } else {
                                if (lyricIndex++ <= lyricNum - 1) {
                                    loadLyricFormNet(lyricUrlList.get(lyricIndex).getLrc());
                                }
                            }
                        }
                    });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: -----------------+++++++++++++++++++-============");
        EventBus.getDefault().unregister(this);
        HeadSetUtil.getInstance().close(this);
    }

    /**
     * 检验下载的歌词文件是否正确
     *
     * @param response
     * @return
     */
    private boolean isRightLyric(File response) {
        FileReader reader = null;
        BufferedReader bufferedReader = null;
        String txt = "";
        boolean isRightLyric = false;
        try {
            reader = new FileReader(response);
            bufferedReader = new BufferedReader(reader);
            String str = "";
            while ((str = bufferedReader.readLine()) != null) {
                // 含有 [] 则为正确歌词
                if (str.contains("[") || str.contains("]")) {
                    isRightLyric = true;
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return isRightLyric;
    }
}
