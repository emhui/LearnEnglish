package com.ycxy.ymh.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.ycxy.ymh.adapter.MusicListAdapter;
import com.ycxy.ymh.bean.MusicList;
import com.ycxy.ymh.bean2.DataBean;
import com.ycxy.ymh.bean2.MusicInfo;
import com.ycxy.ymh.bean3.Lyric;
import com.ycxy.ymh.learnenglish.IAudioPlayService;
import com.ycxy.ymh.learnenglish.R;
import com.ycxy.ymh.service.AudioPlayService;
import com.ycxy.ymh.utils.CacheUtils;
import com.ycxy.ymh.utils.HeadSetUtil;
import com.ycxy.ymh.utils.JsonUtils;
import com.ycxy.ymh.utils.NetUtils;
import com.ycxy.ymh.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;

public class OnlineAudioActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {
    private static final String TAG = "OnlineAudioActivity";

    public static final int GETLIST = 0;
    public static final int GETLISTERRO = 1;
    public static final int GETMUSICINFO = 2;
    public static final int GETMUSICLYRIC = 3;
    public static final int DOWNLOADSUCCESS = 4;

    public static final String key_music = "music";
    public static final String key_list = "list";
    public static String key_lyric = "lyric";
    public static String key_audioname = "audioname";
    private Button btn_search;
    private EditText et_name;
    private RecyclerView rv_music;
    private ProgressBar pb;
    private MediaPlayer mediaPlayer;
    public static final String url_getID = "https://api.imjad.cn/cloudmusic/?type=search&s=";
    public static final String url_getMusic = "https://api.imjad.cn/cloudmusic/?type=song&id=";
    public static final String url_getLyric = "https://api.imjad.cn/cloudmusic/?type=lyric&id=";
    public static final String url_getMusic_trail = "&br=128000";
    private String download_url;
    private String download_music_url;

    private MusicListAdapter adapter;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GETLIST:
                    getList();
                    break;
                case GETLISTERRO:
                    Toast.makeText(OnlineAudioActivity.this, "查询失败", Toast.LENGTH_SHORT).show();
                case GETMUSICINFO:
                    getInfo();
                    Toast.makeText(OnlineAudioActivity.this, "歌词信息获取成功", Toast.LENGTH_SHORT).show();
                    break;
                case GETMUSICLYRIC:
                    downLoadLyric();
                    Toast.makeText(OnlineAudioActivity.this, "歌词获取成功", Toast.LENGTH_SHORT).show();
                    break;
                case DOWNLOADSUCCESS:
                    updataMediaData();
                    Toast.makeText(OnlineAudioActivity.this, "歌区下载成功", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };



    MusicInfo musicInfo;
    private Lyric lyric;
    private int id;
    private String name;

    private void getInfo() {
        String response = CacheUtils.getFromLoacl(OnlineAudioActivity.this, key_music);
        musicInfo = JsonUtils.parseMusic(response);
        Toast.makeText(OnlineAudioActivity.this, musicInfo.getData().get(0).getUrl(), Toast.LENGTH_SHORT).show();
        download_music_url = musicInfo.getData().get(0).getUrl();
        /*play(download_music_url);*/
        try {
            service.openOtherAudio(download_music_url);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    MusicList musicList;

    private void getList() {
        String response = CacheUtils.getFromLoacl(OnlineAudioActivity.this, key_list);
        musicList = JsonUtils.parseMusicList(response);
        adapter = new MusicListAdapter(OnlineAudioActivity.this, musicList.getResult().getSongs(), handler);
        rv_music.setAdapter(adapter);
        Toast.makeText(OnlineAudioActivity.this, "查询成功,有" + musicList.getResult().getSongCount(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online);
        isPermission();

        initView();
        initData();
    }

    private void initData() {
        new Utils().hasFile();
        bindService(new Intent(this, AudioPlayService.class),
                conn,BIND_AUTO_CREATE);
        /*
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnPreparedListener(this);*/
    }

    public void play(String url) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        btn_search = findViewById(R.id.btn_search);
        et_name = findViewById(R.id.et_name);
        rv_music = findViewById(R.id.rv_music);
        pb = findViewById(R.id.pb);
        rv_music.setLayoutManager(new LinearLayoutManager(this));

        pb.setMax(100);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = et_name.getText().toString().trim();
                // CacheUtils.saveToLocal(OnlineAudioActivity.this, OnlineAudioActivity.key_audioname,name);
                String url_getID = OnlineAudioActivity.this.url_getID + name;
                NetUtils.getNetMusic(url_getID, handler, OnlineAudioActivity.this, key_list, GETLIST);
            }
        });

        rv_music.addOnItemTouchListener(new MyOnItemTouchListener(rv_music) {

            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                // 开始播放音乐
                int position = vh.getAdapterPosition();
                name = musicList.getResult().getSongs().get(position).getName();
                id = musicList.getResult().getSongs().get(position).getId();
                download_url = OnlineAudioActivity.url_getMusic + id + OnlineAudioActivity.url_getMusic_trail;
                Log.d(TAG, "onItemClick: " + name);
                CacheUtils.saveToLocal(OnlineAudioActivity.this,OnlineAudioActivity.key_audioname,name);
                NetUtils.getNetMusic(download_url, handler, OnlineAudioActivity.this, OnlineAudioActivity.key_music, OnlineAudioActivity.GETMUSICINFO);
            }

            @Override
            public void onItemLongPress(RecyclerView.ViewHolder vh) {
                int position = vh.getAdapterPosition();
                name = musicList.getResult().getSongs().get(position).getName();
                id = musicList.getResult().getSongs().get(position).getId();
                download_url = OnlineAudioActivity.url_getMusic + id + OnlineAudioActivity.url_getMusic_trail;
                showInfo();
            }
        });
    }


    /**
     * 申请权限访问
     */
    public void isPermission() {
        if (ContextCompat.checkSelfPermission(OnlineAudioActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(OnlineAudioActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(OnlineAudioActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
        }
    }

    /**
     * 权限访问结果回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    finish();
                }
                break;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaPlayer.start();
    }


    /**
     * 实现触摸点击Item的监听
     */
    private abstract class MyOnItemTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetectorCompat mGestureDetectorCompat;
        private RecyclerView mRecyclerView;

        public MyOnItemTouchListener(RecyclerView recyclerView) {
            this.mRecyclerView = recyclerView;
            mGestureDetectorCompat = new GestureDetectorCompat(mRecyclerView.getContext()
                    , new MyGestureListener());
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            mGestureDetectorCompat.onTouchEvent(e);
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            mGestureDetectorCompat.onTouchEvent(e);
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }

        public abstract void onItemClick(RecyclerView.ViewHolder vh);

        public abstract void onItemLongPress(RecyclerView.ViewHolder vh);

        /**
         * 根据手势
         */
        private class MyGestureListener implements GestureDetector.OnGestureListener {

            @Override
            public boolean onDown(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                View childe = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
                if (childe != null) {
                    RecyclerView.ViewHolder VH = mRecyclerView.getChildViewHolder(childe);
                    onItemClick(VH);
                }
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View childe = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
                if (childe != null) {
                    RecyclerView.ViewHolder VH = mRecyclerView.getChildViewHolder(childe);
                    onItemLongPress(VH);
                }
            }

            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return false;
            }
        }
    }

    String[] infos = new String[]{"音乐下载", "歌词下载"};

    private void showInfo() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setItems(infos, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Toast.makeText(MainActivity.this, "定时播放已取消", Toast.LENGTH_SHORT).show();
                        switch (which) {
                            case 0:
                                downLoadMusic();
                                break;
                            case 1:
                                String download_lyric_url = url_getLyric + id;
                                Log.d(TAG, "onClick: " + download_lyric_url);
                                NetUtils.getNetMusic(download_lyric_url, handler, OnlineAudioActivity.this, OnlineAudioActivity.key_lyric, OnlineAudioActivity.GETMUSICLYRIC);
                                break;
                        }
                    }
                }).create();
        dialog.show();
    }

    private void downLoadMusic() {
        Log.d(TAG, "downLoadMusic: " + download_music_url);
        NetUtils.downLoadMusic(download_music_url, name, handler, pb);
    }

    // 保存数据
    private void downLoadLyric() {
        String response = CacheUtils.getFromLoacl(this, key_lyric);
        lyric = JsonUtils.parseLyric(response);
        String lyricContext = lyric.getLrc().getLyric();
        new Utils().saveLyric(et_name.getText().toString().trim(), lyricContext);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    // 更新数据库
    private void updataMediaData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { // 判断SDK版本是不是4.4或者高于4.4
            String[] paths = new String[]{Environment.getExternalStorageDirectory().toString()};
            MediaScannerConnection.scanFile(this, paths, null, null);
        } else {
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED));
        }

        EventBus.getDefault().post(new DataBean());
    }


    private IAudioPlayService service;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            service = IAudioPlayService.Stub.asInterface(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

    };
}
