package com.ycxy.ymh.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ycxy.ymh.adapter.FileAdpater;
import com.ycxy.ymh.learnenglish.IAudioPlayService;
import com.ycxy.ymh.learnenglish.R;
import com.ycxy.ymh.service.AudioPlayService;
import com.ycxy.ymh.utils.CacheUtils;

import java.io.File;

public class FileActivity extends AppCompatActivity {

    private Button btn_back;
    private RecyclerView recyclerView;
    private TextView tv_cur_dict;
    private String currPath;
    private String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static String key_file_path = "file_path";
    public static String key_file_lyric_path = "lyric_path";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
        // isPermission();
        initView();
    }

    private void initView() {
        btn_back = findViewById(R.id.btn_back);
        recyclerView = findViewById(R.id.rv_file_list);
        tv_cur_dict = findViewById(R.id.tv_cur_dict);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FileAdpater adpater = new FileAdpater(this);
        recyclerView.setAdapter(adpater);

        String parentPath = CacheUtils.getFromLoacl(this, key_file_path);
        if (parentPath.equals("")) {
            parentPath = rootPath;
        }

        adpater.scanFiles(parentPath);
        tv_cur_dict.setText(parentPath);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                //back();
            }
        });

        recyclerView.addOnItemTouchListener(new MyOnItemTouchListener(recyclerView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                FileAdpater fileNext = (FileAdpater) recyclerView.getAdapter();
                File file = fileNext.fileList.get(vh.getAdapterPosition());
                currPath = file.getPath();
                if (file.isDirectory()) {
                    tv_cur_dict.setText(file.getPath());
                    fileNext.scanFiles(file.getPath());
                } else {
                    openFile();
                }
            }

            @Override
            public void onItemLongPress(RecyclerView.ViewHolder vh) {

            }
        });
    }

    private void back() {
        FileAdpater adpater1 = (FileAdpater) recyclerView.getAdapter();

        if (adpater1.curPath.equals(rootPath)) {
            finish();
            return;
        } else {
            File file = new File(adpater1.curPath);
            tv_cur_dict.setText(file.getParent());
            adpater1.scanFiles(file.getParent());
        }
    }

    private Intent intent;

    private void openFile() {
        if (isAudio()) {
            if (service != null) {
                start();
            } else {
                intent = new Intent(FileActivity.this, AudioPlayService.class);
                bindService(intent, conn, BIND_AUTO_CREATE);
            }
        } else if(isLyric()){
            CacheUtils.saveToLocal(this,key_file_lyric_path,currPath);
        } else {
            Toast.makeText(FileActivity.this,
                    "文件选择错误，不能打开",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void start() {
        try {
            service.openOtherAudio(currPath);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private boolean isAudio() {
        if (currPath.contains(".mp3") ||
                currPath.contains(".flac") ||
                currPath.contains(".wav") ||
                currPath.contains(".aac")) {
            return true;
        }
        return false;
    }

    public boolean isLyric(){
        if (currPath.endsWith(".lrc") || currPath.endsWith(".txt")) {
            return true;
        }
        return false;
    }
    @Override
    public void onBackPressed() {
        back();
        // super.onBackPressed();
    }

    private IAudioPlayService service;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            service = IAudioPlayService.Stub.asInterface(iBinder);
            start();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

    };

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
}
