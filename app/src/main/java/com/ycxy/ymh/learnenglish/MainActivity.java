package com.ycxy.ymh.learnenglish;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ycxy.ymh.bean.Audio;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int SUCCESSQUERY = 0;
    private ArrayList<Audio> audioArrayList;
    private LinearLayout ll_audio_msg;
    private RecyclerView recyclerView;
    private Button btn_audio_play;
    private Button btn_audio_next;
    private TextView tv_audio_msg;

    private boolean isPlaying = false;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        clsTilte();
        initView();
        initData();
    }

    private void clsTilte() {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    private void initData() {
        getDataFromLocal();
    }

    private void initView() {
        ll_audio_msg = findViewById(R.id.ll_audio_msg);
        recyclerView = findViewById(R.id.recyclerView);
        btn_audio_play = findViewById(R.id.btn_audio_play);
        btn_audio_next = findViewById(R.id.btn_audio_next);
        tv_audio_msg = findViewById(R.id.tv_audio_msg);

        ll_audio_msg.setOnClickListener(this);
        btn_audio_play.setOnClickListener(this);
        btn_audio_next.setOnClickListener(this);
    }


    /**
     * 从本地的sdcard得到数据
     * //1.遍历sdcard,后缀名
     * //2.从内容提供者里面获取视频
     * //3.如果是6.0的系统，动态获取读取sdcard的权限
     */
    private void getDataFromLocal() {

        new Thread() {
            @Override
            public void run() {
                super.run();

                isGrantExternalRW((Activity) MainActivity.this);
                audioArrayList = new ArrayList<>();
                ContentResolver resolver = MainActivity.this.getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] objs = {
                        MediaStore.Audio.Media.DISPLAY_NAME,//视频文件在sdcard的名称
                        MediaStore.Audio.Media.DURATION,//视频总时长
                        MediaStore.Audio.Media.SIZE,//视频的文件大小
                        MediaStore.Audio.Media.DATA,//视频的绝对地址
                        MediaStore.Audio.Media.ARTIST,//歌曲的演唱者
                };
                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {

                        Audio mediaItem = new Audio();

                        audioArrayList.add(mediaItem);//写在上面

                        String name = cursor.getString(0);//视频的名称
                        mediaItem.setName(name);

                        long duration = cursor.getLong(1);//视频的时长
                        mediaItem.setDuration(duration);

                        long size = cursor.getLong(2);//视频的文件大小
                        mediaItem.setSize(size);

                        String data = cursor.getString(3);//视频的播放地址
                        mediaItem.setData(data);

                        String artist = cursor.getString(4);//艺术家
                        mediaItem.setArtist(artist);
                    }
                    cursor.close();
                }
                //Handler发消息
                handler.sendEmptyMessage(SUCCESSQUERY);


            }
        }.start();

    }

    /**
     * 解决安卓6.0以上版本不能读取外部存储权限的问题
     *
     * @param activity
     * @return
     */
    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);

            return false;
        }

        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_audio_msg:
                Toast.makeText(this, "跳转", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_audio_play:
                if (isPlaying) {
                    btn_audio_play.setBackgroundResource(R.mipmap.pause);
                } else {
                    btn_audio_play.setBackgroundResource(R.mipmap.play);
                }
                isPlaying = !isPlaying;
                break;
            case R.id.btn_audio_next:
                Toast.makeText(this, "下一曲", Toast.LENGTH_SHORT).show();
                break;

        }
    }
}
