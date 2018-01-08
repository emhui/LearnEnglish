package com.ycxy.ymh.learnenglish;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ycxy.ymh.activity.AudioActivity;
import com.ycxy.ymh.adapter.AudioAdapter;
import com.ycxy.ymh.bean.Audio;
import com.ycxy.ymh.service.AudioPlayService;
import com.ycxy.ymh.view.MyDecoration;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int SUCCESSQUERY = 0;
    private static final int SHOWAUDIONAME = 1;
    private static final String TAG = "MainActivity";
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
            switch (msg.what) {
                case SUCCESSQUERY:
                    AudioAdapter adapter = new AudioAdapter(MainActivity.this, audioArrayList);
                    recyclerView.setAdapter(adapter);
                    break;
                case SHOWAUDIONAME:
                    updataUI();
                    break;
            }
        }
    };

    private void updataUI() {
        if (iService != null) {
            try {
                updataBtnPlay();
                // 记住切割名字
                if (iService.getName().equals("")) {
                    tv_audio_msg.setText("当前没有音乐在播放");
                } else {
                    tv_audio_msg.setText("正在播放 " + iService.getName().split("\\.")[0]);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            tv_audio_msg.setText("当前没有音乐在播放");
        }
        handler.sendEmptyMessageDelayed(SHOWAUDIONAME, 100);
    }

    private void updataBtnPlay() throws RemoteException {
        if (iService.isPlaying()) {
            btn_audio_play.setBackgroundResource(R.mipmap.pause);
        } else {
            btn_audio_play.setBackgroundResource(R.mipmap.play);
        }
    }

    private IAudioPlayService iService;
    private int position = 0;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            iService = IAudioPlayService.Stub.asInterface(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            try {
                if (iService != null) {
                    iService.stop();
                    iService = null;
                }
            } catch (Exception e) {

            }
        }
    };

    private void openAudio() {
        if (iService != null) {
            try {
                iService.openAudio(position);
                Log.d(TAG, "openAudio: " + position);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

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
        isPermission();
        getDataFromLocal();
        startService();
        handler.sendEmptyMessageDelayed(SHOWAUDIONAME, 100);
    }

    private void startService() {
        Intent intent = new Intent(this, AudioPlayService.class);
        startService(intent);
        bindService(intent, conn, BIND_AUTO_CREATE);
    }

    private void initView() {
        ll_audio_msg = findViewById(R.id.ll_audio_msg);
        recyclerView = findViewById(R.id.recyclerView);
        btn_audio_play = findViewById(R.id.btn_audio_play);
        btn_audio_next = findViewById(R.id.btn_audio_next);
        tv_audio_msg = findViewById(R.id.tv_audio_msg);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new MyDecoration(this, LinearLayoutManager.HORIZONTAL));
        recyclerView.addOnItemTouchListener(new MyOnItemTouchListener(recyclerView) {

            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                // 开始播放音乐
                position = vh.getAdapterPosition();
                Log.d(TAG, "onItemClick: " + position);
                openAudio();
            }
        });

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
                        Log.d(TAG, "run:data " + data);
                        String artist = cursor.getString(4);//艺术家
                        mediaItem.setArtist(artist);
                        Log.d(TAG, "run: artist" + artist);
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
                Intent intent = new Intent(MainActivity.this, AudioActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_audio_play:
                try {
                    if (iService.isNull()) {
                        openAudio();
                    }

                    if (iService.isPlaying()) {
                        btn_audio_play.setBackgroundResource(R.mipmap.play);
                        iService.pause();
                    } else {
                        btn_audio_play.setBackgroundResource(R.mipmap.pause);
                        iService.start();
                    }
                    isPlaying = !isPlaying;
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.btn_audio_next:
                try {
                    iService.next();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;

        }
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
                    onItemClick(VH);
                }
            }

            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return false;
            }
        }
    }

    /**
     * 申请权限访问
     */
    public void isPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
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
}
