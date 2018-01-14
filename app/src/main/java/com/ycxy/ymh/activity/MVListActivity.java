package com.ycxy.ymh.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ycxy.ymh.adapter.MVListAdapter;
import com.ycxy.ymh.bean6.DataBean;
import com.ycxy.ymh.bean5.MVList;
import com.ycxy.ymh.bean5.MvsBean;
import com.ycxy.ymh.bean6.MVInfo;
import com.ycxy.ymh.learnenglish.R;
import com.ycxy.ymh.utils.CacheUtils;
import com.ycxy.ymh.utils.JsonUtils;
import com.ycxy.ymh.utils.NetUtils;

import java.util.List;

public class MVListActivity extends AppCompatActivity {

    private static final String TAG = "MVListActivity";

    public static final String url_mv_getList = "https://api.imjad.cn/cloudmusic/?type=search&search_type=1004&s=";
    public static final String url_mv_getUrl = "https://api.imjad.cn/cloudmusic/?type=mv&id=";
    public static final String url_mv_getUrl_trail = "&br=128000";

    public static final String key_mv_list = "mv_list";
    public static final String key_mv_info = "mv_info";

    public static final String intent_video_url = "video_url";
    public static final String intent_name_url = "name_url";
    public static final String intent_image_url = "image_url";

    public static final int GETMVLIST = 0;
    public static final int GETMVINFO = 1;
    public static final int GETMVERRO = 2;

    private Button btn_search;
    private EditText et_name;
    private RecyclerView recyclerView;
    private MVListAdapter adapter;

    private String mv_name;

    private MVList mvList;
    private List<MvsBean> mvsBeanList;
    private MvsBean mvsBean;

    private MVInfo mvInfo;
    private DataBean mv;

    private int pos;
    // private List<DataBean> dataBeans;

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GETMVLIST:
                    getList();
                    break;
                case GETMVINFO:
                    getInfo();
                    break;
                case GETMVERRO:
                    Toast.makeText(MVListActivity.this,
                            "视频播放错误", Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_mv);

        initView();
    }

    private void initView() {
        btn_search = findViewById(R.id.btn_search_mv);
        et_name = findViewById(R.id.et_name_mv);
        recyclerView = findViewById(R.id.rv_music_mv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mv_name = et_name.getText().toString().trim();
                String url = url_mv_getList + mv_name;
                NetUtils.getNetMusic(url, handler, MVListActivity.this, key_mv_list, GETMVLIST);
            }
        });

        recyclerView.addOnItemTouchListener(new MyOnItemTouchListener(recyclerView) {

            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                pos = vh.getAdapterPosition();
                mvsBean = mvsBeanList.get(pos);
                int id = mvsBean.getId();
                String url = MVListActivity.url_mv_getUrl + id + MVListActivity.url_mv_getUrl_trail;
                NetUtils.getNetMusic(url, handler, MVListActivity.this, MVListActivity.key_mv_info, MVListActivity.GETMVINFO);
            }

            @Override
            public void onItemLongPress(RecyclerView.ViewHolder vh) {

            }
        });
    }

    private void getList() {
        try {
            String response = CacheUtils.getFromLoacl(this, key_mv_list);
            mvList = JsonUtils.parseMvlist(response);
            mvsBeanList = mvList.getResult().getMvs();
            adapter = new MVListAdapter(this, mvsBeanList, handler);
            recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            handler.sendEmptyMessage(GETMVERRO);
        }
    }

    private void getInfo() {
        try {
            String response = CacheUtils.getFromLoacl(this, key_mv_info);
            mvInfo = JsonUtils.parseMvInfo(response);
            mv = mvInfo.getData();
            Intent intent = new Intent(MVListActivity.this, VideoActivity.class);
            intent.putExtra(intent_video_url, mv.getBrs().get_$720());
            intent.putExtra(intent_name_url,mvsBean.getName());
            intent.putExtra(intent_image_url,mv.getCover());
            startActivity(intent);
        } catch (Exception e) {
            Log.d(TAG, "getInfo: " + e.toString());
            handler.sendEmptyMessage(GETMVERRO);
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
                    , new MVListActivity.MyOnItemTouchListener.MyGestureListener());
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
