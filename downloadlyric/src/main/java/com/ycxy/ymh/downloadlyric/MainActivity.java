package com.ycxy.ymh.downloadlyric;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.ycxy.ymh.bean.LyricBean;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import okhttp3.Call;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int ERROR = 0;
    private static final int SUCCESS = 1;
    private static final int FAILED = 2;
    private Button btn_get;
    private TextView tv_show;
    private EditText ed_name;
    private String url = "http://geci.me/api/lyric/";
    private String STROAGEPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "lyric";
    LyricBean bean;
    private String songName = "";
    private ProgressBar pb;
    private RelativeLayout rl;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ERROR:
                    break;
                case SUCCESS:
                    tv_show.setText((String) msg.obj);
                    Toast.makeText(MainActivity.this, "挖掘成功~", Toast.LENGTH_SHORT).
                            show();
                    break;
                case FAILED:
                    tv_show.setText("抱歉，该歌曲歌词资源未找到");
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isPermission();
        hasFile();
        initView();
    }

    private void initView() {
        btn_get = findViewById(R.id.btn_get);
        tv_show = findViewById(R.id.tv_show);
        ed_name = findViewById(R.id.ed_name);
        pb = findViewById(R.id.pb);
        rl = findViewById(R.id.rl);

        btn_get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                songName = ed_name.getText().toString().trim();
                if (isLyricExit()) {
                    File file = new File(STROAGEPATH + File.separator + songName + ".lrc");
                    readFile(file);
                } else {
                    get();
                    rl.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    /**
     * 判断歌词是否存在
     * @return
     */
    private boolean isLyricExit() {
        // File file = new File(STROAGEPATH + File.separator + songName + ".lrc");
        File file = new File(STROAGEPATH);
        File[] files = file.listFiles();

        for (File f : files) {
            if (f.getName().equals(songName + ".lrc")) {
                return true;
            }
        }
        return false;
    }

    private void get() {
        OkHttpUtils
                .get()
                .url(url + songName)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, "onError: ");
                        rl.setVisibility(View.GONE);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, "onResponse: " + response);
                        parseJSON(response);
                        rl.setVisibility(View.GONE);
                    }
                });
    }

    private void parseJSON(String response) {
        bean = JSON.parseObject(response, LyricBean.class);
        Log.d(TAG, "parseJSON: " + bean.getCount());
        // 判断是否有歌词资源
        if (bean.getCount() > 0) {
            // 开始下载歌词
            String url = bean.getResult().get(0).getLrc();
            Log.d(TAG, "parseJSON: " + url);
            downloadLyric(url);
        } else {
            handler.sendEmptyMessage(FAILED);
        }
    }

    private void downloadLyric(String url) {
        OkHttpUtils//
                .get()//
                .url(url)//
                .build()//
                .execute(new FileCallBack(STROAGEPATH, songName + ".lrc")//
                {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, "onError: ---------------" + e.toString());
                    }

                    @Override
                    public void onResponse(File response, int id) {
                        Log.d(TAG, "onResponse: ");
                        readFile(response);
                    }
                });
    }

    private void readFile(File response) {
        FileReader reader = null;
        BufferedReader bufferedReader = null;
        String txt = "";
        try {
            reader = new FileReader(response);
            bufferedReader = new BufferedReader(reader);
            String str = "";
            while ((str = bufferedReader.readLine()) != null) {
                txt = txt + "\n" + str;
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
        Message msg = Message.obtain();
        msg.obj = txt;
        msg.what = SUCCESS;
        handler.sendMessage(msg);
    }

    /**
     * 申请权限访问
     */
    public void isPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
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

    /**
     * 建立一个存储音乐歌词的文件
     */
    public void hasFile() {
        File file = new File(STROAGEPATH);
        if (!file.exists()) {
            file.mkdir();
        }
    }
}
