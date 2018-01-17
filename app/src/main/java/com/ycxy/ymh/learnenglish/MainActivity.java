package com.ycxy.ymh.learnenglish;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.ycxy.ymh.activity.AudioActivity;
import com.ycxy.ymh.activity.FileActivity;
import com.ycxy.ymh.activity.MVListActivity;
import com.ycxy.ymh.activity.OnlineAudioActivity;
import com.ycxy.ymh.adapter.AudioAdapter;
import com.ycxy.ymh.bean.Audio;
import com.ycxy.ymh.bean.MessageEvent;
import com.ycxy.ymh.bean2.DataBean;
import com.ycxy.ymh.receiver.MusicBoradcastReceiver;
import com.ycxy.ymh.service.AudioPlayService;
import com.ycxy.ymh.service.LongRunningService;
import com.ycxy.ymh.utils.CacheUtils;
import com.ycxy.ymh.utils.Utils;
import com.ycxy.ymh.view.MyDecoration;
import com.ycxy.ymh.view.MyTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static final int SUCCESSQUERY = 0;
    private static final int SHOWAUDIONAME = 1;
    private static final String TAG = "MainActivity";
    private ArrayList<Audio> audioArrayList;
    private LinearLayout ll_audio_msg;
    private RecyclerView recyclerView;
    private Button btn_audio_play;
    private Button btn_audio_next;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MyTextView tv_audio_msg;

    private boolean isPlaying = false;
    private AudioAdapter adapter;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SUCCESSQUERY:
                    Log.d(TAG, "handleMessage: " + audioArrayList.size());
                    adapter = new AudioAdapter(MainActivity.this, audioArrayList);
                    recyclerView.setAdapter(adapter);
                    break;
                case SHOWAUDIONAME:
                    updataUI();
                    break;
            }
        }
    };

    private android.content.IntentFilter filter;
    private android.support.v7.widget.Toolbar toolbar;
    private String audioName;

    private void updataUI() {
        if (iService != null) {
            try {
                updataBtnPlay();
                audioName = CacheUtils.getFromLoacl(this,OnlineAudioActivity.key_audioname);
                tv_audio_msg.setText(audioName);
            } catch (Exception e) {
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
            startBroadcast();
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
                CacheUtils.saveToLocal(this,OnlineAudioActivity.key_audioname,
                        new Utils().getAudioName(audioArrayList.get(position).getName()));
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

        toolbar = findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);
    }

    private void initData() {
        // isPermission();
        getDataFromLocal();
        EventBus.getDefault().register(this);
        startService();
        position = new Utils().getPosfStor(this);
        handler.sendEmptyMessageDelayed(SHOWAUDIONAME, 100);

    }

    private void startBroadcast() {
        // 注册广播
        filter = new IntentFilter();
        filter.addAction("android.intent.action.HEADSET_PLUG");
        filter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
        receiver = new MusicBoradcastReceiver();
        registerReceiver(receiver, filter);

        // 耳机插入的监听事件
        receiver.setOnHEADSET_PLUGINListener(new MusicBoradcastReceiver.OnHEADSET_PLUGINListener() {
            @Override
            public void setOnHEADSET_PLUGINListener() {
                start();
            }
        });
        // 耳机或蓝牙耳机断开的监听
        receiver.setOnHEADSET_PLUGOUTListener(new MusicBoradcastReceiver.OnHEADSET_PLUGOUTListener() {
            @Override
            public void setOnHEADSET_PLUGOUTListener() {
                pause();
            }
        });
    }

    Intent intentService;

    private void startService() {
        intentService = new Intent(this, AudioPlayService.class);
        startService(intentService);
        bindService(intentService, conn, BIND_AUTO_CREATE);
    }

    private void initView() {
        ll_audio_msg = findViewById(R.id.ll_audio_msg);
        recyclerView = findViewById(R.id.recyclerView);
        btn_audio_play = findViewById(R.id.btn_audio_play);
        btn_audio_next = findViewById(R.id.btn_audio_next);
        tv_audio_msg = findViewById(R.id.tv_audio_msg);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        LinearLayoutManager manager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new MyDecoration(this, LinearLayoutManager.HORIZONTAL));
        recyclerView.addOnItemTouchListener(new MyOnItemTouchListener(recyclerView) {

            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                // 开始播放音乐
                position = vh.getAdapterPosition();
                openAudio();
            }

            @Override
            public void onItemLongPress(RecyclerView.ViewHolder vh) {
                position = vh.getAdapterPosition();
                showInfo();
            }
        });

        // ll_audio_msg.setOnClickListener(this);
        btn_audio_play.setOnClickListener(this);
        btn_audio_next.setOnClickListener(this);

        tv_audio_msg.setOnClickedListener(new MyTextView.OnClickedListener() {
            @Override
            public void setOnClickedListener() {
                startPlayView();
            }
        });

        tv_audio_msg.setOnSwipeListener(new MyTextView.OnSwipeListener() {
            @Override
            public void setOnSwipeNextListener() {
                next();
            }

            @Override
            public void setOnSwipePreListener() {
                pre();
            }
        });
    }

    public static String key_splash = "splash";
    public static boolean isLiving = false;

    @Override
    public void onBackPressed() {
        isLiving = true;
        // CacheUtils.saveToLocal(this,key_splash,"splash");
        super.onBackPressed();
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
                        MediaStore.Audio.Media.TITLE,//视频文件在sdcard的名称
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
            case R.id.btn_audio_play:
                try {
                    if (iService.isNull()) {
                        openAudio();
                    }
                    if (iService.isPlaying()) {
                        pause();
                    } else {
                        start();
                    }
                    isPlaying = !isPlaying;
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.btn_audio_next:
                next();
                break;

        }
    }

    private void pre() {
        try {
            iService.pre();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void next() {
        try {
            iService.next();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void startPlayView() {
        Intent intent = new Intent(MainActivity.this, AudioActivity.class);
        startActivity(intent);
    }

    private void start() {
        try {
            if (!iService.isNull()) {
                btn_audio_play.setBackgroundResource(R.mipmap.pause);
                iService.start();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void pause() {
        try {
            if (!iService.isNull()) {
                btn_audio_play.setBackgroundResource(R.mipmap.play);
                iService.pause();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();


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

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    public MusicBoradcastReceiver receiver;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.timer:
                timer();
                break;
            case R.id.skin:
                startActivity(new Intent(MainActivity.this, OnlineAudioActivity.class));
                break;
            case R.id.mv:
                startActivity(new Intent(MainActivity.this, MVListActivity.class));
                break;
            case R.id.exit:
                exit();
                break;
            case R.id.file:
                startActivity(new Intent(MainActivity.this, FileActivity.class));
                break;
        }
        return true;
    }

//    String[] infos = new String[]{"播放", "设置铃声", "查看歌曲信息", "删除"};
    String[] infos = new String[]{"播放","查看地址"};

    private void showInfo() {
        String name = new Utils().getAudioName(audioArrayList.get(position).getName());
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("歌曲：" + name)
                .setItems(infos, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Toast.makeText(MainActivity.this, "定时播放已取消", Toast.LENGTH_SHORT).show();
                        switch (which) {
                            case 0:
                                openAudio();
                                break;
                            case 1:
                                // setRingtone();
                                // moreInfo();
                                Toast.makeText(MainActivity.this,
                                        audioArrayList.get(position).getData(),
                                        Toast.LENGTH_LONG).show();
                                break;
                            case 2:
                                break;
                            case 3:
                                delete();
                                break;
                        }
                    }
                }).create();
        dialog.show();
    }

    private void moreInfo() {
        PopupWindow popupWindow = new PopupWindow(this);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(LayoutInflater.from(this).inflate(R.layout.item_pop, null));
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);
    }

    private void delete() {
        Audio audio = audioArrayList.get(position);
        final File file = new File(audio.getData());
        String name = audio.getName();
        if (file.exists()) {
            AlertDialog dialog = new AlertDialog.Builder(this).setTitle("确定删除 " + name + "?")
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            file.delete();
                            //audioArrayList.remove(position);
                            adapter.remove(position);
                            new Utils().updataMediaData(MainActivity.this);
                            Toast.makeText(MainActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    })
                    .create();
            dialog.show();
        }
    }

    private void setRingtone() {
        Audio audio = audioArrayList.get(position);
        Uri uri = MediaStore.Audio.Media.getContentUriForPath(audio.getData());//获取系统音频文件的Uri
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, audio.getData());
        values.put(MediaStore.MediaColumns.TITLE, audio.getName());
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*");
        Uri newUri = this.getContentResolver().insert(uri, values);//将文件插入系统媒体库，并获取新的Uri
        RingtoneManager.setActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALL, newUri);//设置铃声
        Toast.makeText(this, "铃声设置成功~", Toast.LENGTH_SHORT).show();
    }


    String[] items = new String[]{"不开启", "10分钟后", "20分钟后", "30分钟后", "45分钟后", "60分钟后"};
    int[] itemtimers = new int[]{0, 1, 20, 30, 45, 60};
    private int timer;
    private Intent intent;

    private void timer() {
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("定时停止播放")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            if (longTimeIBinder != null) {
                                longTimeIBinder.cancel();
                                stopService(intent);
                            }
                            Toast.makeText(MainActivity.this, "定时播放已取消", Toast.LENGTH_SHORT).show();
                        } else {
                            timer = itemtimers[which];
                            if (intent == null) {
                                intent = new Intent(MainActivity.this, LongRunningService.class);
                            }
                            if (longTimeIBinder == null) {
                                startService(intent);
                                bindService(intent, longConn, BIND_AUTO_CREATE);
                            }
                            if (longTimeIBinder != null) {
                                longTimeIBinder.setTime(timer);
                            }
                            Toast.makeText(MainActivity.this, "设置成功，将于" + timer + "分钟后关闭", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).create();
        dialog.show();
    }

    private void exit() {
        pause();
        isLiving = false;
        unbindService(conn);
        stopService(intentService);
        this.finish();
    }

    public LongRunningService.LongTimeIBinder longTimeIBinder;

    public ServiceConnection longConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            longTimeIBinder = (LongRunningService.LongTimeIBinder) service;
            longTimeIBinder.setTime(timer);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = false, priority = 0)
    public void onTiemrTO(MessageEvent messageEvent) {
        pause();
        stopService(intent);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC, sticky = false, priority = 0)
    public void scanDataBase(DataBean dataBean) {
        getDataFromLocal();
        Log.d(TAG, "scanDataBase: ==============");
    }
}
