package com.cn.filelist;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private Button btn_back;
    private RecyclerView recyclerView;
    private TextView tv_cur_dict;
    private String currPath;
    private String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isPermission();
        initView();
    }

    private void initView() {
        btn_back = findViewById(R.id.btn_back);
        recyclerView = findViewById(R.id.rv_file_list);
        tv_cur_dict = findViewById(R.id.tv_cur_dict);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FileAdpater adpater = new FileAdpater(this);
        recyclerView.setAdapter(adpater);

        adpater.scanFiles(rootPath);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileAdpater adpater1 = (FileAdpater) recyclerView.getAdapter();

                if (adpater1.curPath.equals(rootPath)) {
                    Toast.makeText(MainActivity.this,
                            "已到达根目录",
                            Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    File file = new File(adpater1.curPath);
                    tv_cur_dict.setText(file.getParent());
                    adpater1.scanFiles(file.getParent());
                }
            }
        });

        recyclerView.addOnItemTouchListener(new MyOnItemTouchListener(recyclerView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                FileAdpater fileNext = (FileAdpater) recyclerView.getAdapter();
                File file = fileNext.fileList.get(vh.getAdapterPosition());

                if (file.isDirectory()) {
                    currPath = file.getPath();
                    tv_cur_dict.setText(file.getPath());
                    fileNext.scanFiles(file.getPath());
                } else {
                    Toast.makeText(MainActivity.this,
                            "this is a file, can't open it",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onItemLongPress(RecyclerView.ViewHolder vh) {

            }
        });
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

    /**
     * 申请权限访问
     */
    public void isPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
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
