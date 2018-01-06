package com.ycxy.ymh.service;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.ycxy.ymh.activity.AudioActivity;
import com.ycxy.ymh.bean.Audio;
import com.ycxy.ymh.learnenglish.IAudioPlayService;
import com.ycxy.ymh.learnenglish.MainActivity;
import com.ycxy.ymh.learnenglish.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class AudioPlayService extends Service {

    private static final String TAG = "AudioPlayService";
    private ArrayList<Audio> audioArrayList;
    private int position = 0;
    /**
     * 播放音乐
     */
    private MediaPlayer mediaPlayer;
    /**
     * 当前播放的音频文件对象
     */
    private Audio audio;
    /**
     * 顺序播放
     */
    public static final int REPEAT_ALL = 1;
    /**
     * 单曲循环
     */
    public static final int REPEAT_SINGLE = 2;
    /**
     * 全部循环
     */
    public static final int REPEAT_RANDOM = 3;

    /**
     * 播放模式
     */
    private int playmode = REPEAT_ALL;

    @Override
    public void onCreate() {
        super.onCreate();
        initData();
    }

    private void initData() {
        getDataFromLocal();
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return stub;
    }

    private IAudioPlayService.Stub stub = new IAudioPlayService.Stub() {
        AudioPlayService service = AudioPlayService.this;

        @Override
        public void openAudio(int position) throws RemoteException {
            service.openAudio(position);
        }

        @Override
        public void start() throws RemoteException {
            service.start();
        }

        @Override
        public void pause() throws RemoteException {
            service.pause();
        }

        @Override
        public void stop() throws RemoteException {
            service.stop();
        }

        @Override
        public int getCurrentPosition() throws RemoteException {
            return service.getCurrentPosition();
        }

        @Override
        public int getDuration() throws RemoteException {
            return service.getDuration();
        }

        @Override
        public String getArtist() throws RemoteException {
            return service.getArtist();
        }

        @Override
        public String getName() throws RemoteException {
            if (mediaPlayer == null)
                return "";
            return service.getName();
        }

        @Override
        public String getAudioPath() throws RemoteException {
            return service.getAudioPath();
        }

        @Override
        public void next() throws RemoteException {
            service.next();
        }

        @Override
        public void pre() throws RemoteException {
            service.pre();
        }

        @Override
        public void setPlayMode(int playmode) throws RemoteException {
            service.setPlayMode(playmode);
        }

        @Override
        public int getPlayMode() throws RemoteException {
            return service.getPlayMode();
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return service.isPlaying();
        }

        @Override
        public boolean isNull() throws RemoteException {
            return service.isNull();
        }

        @Override
        public void seekTo(int mesc) throws RemoteException {
            service.seekTo(mesc);
        }
    };

    /**
     * 根据位置打开对应的音频文件,并且播放
     *
     * @param position
     */
    public void openAudio(int position) {
        this.position = position;
        Log.d(TAG, "openAudio: " + position);
        if (audioArrayList != null && audioArrayList.size() > 0) {

            audio = audioArrayList.get(position);

            if (mediaPlayer != null) {
                mediaPlayer.reset();
            }

            try {
                mediaPlayer = new MediaPlayer();
                //设置监听：播放出错，播放完成，准备好
                mediaPlayer.setOnPreparedListener(new MyOnPreparedListener());
                mediaPlayer.setOnCompletionListener(new MyOnCompletionListener());
                mediaPlayer.setOnErrorListener(new MyOnErrorListener());
                mediaPlayer.setDataSource(audio.getData());
                mediaPlayer.prepareAsync();

                if (playmode == AudioPlayService.REPEAT_SINGLE) {
                    //单曲循环播放-不会触发播放完成的回调
                    mediaPlayer.setLooping(true);
                } else {
                    //不循环播放
                    mediaPlayer.setLooping(false);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }


        } else {
            // Toast.makeText(MusicPlayerService.this, "还没有数据", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 由于不知道的错误会导致调用，所以暂时不适用该方法
     * 详细可以看错误码 http://www.cnblogs.com/getherBlog/p/3939033.html
     */
    class MyOnErrorListener implements MediaPlayer.OnErrorListener {

        /**
         * @param mp
         * @param what
         * @param extra
         * @return false 则执行OnCompletionListener方法， 设置为true不处理
         */
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            return true;
        }
    }

    /**
     * 播放完成后自动切换下一曲
     */
    class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            next();
        }
    }

    /**
     * 准备好开始播放
     */
    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mp) {
            start();
        }
    }

    // 启动
    public void start() {
        mediaPlayer.start();
        setNotify();
    }

    /**
     * 播暂停音乐
     */
    private void pause() {
        mediaPlayer.pause();
    }

    /**
     * 停止
     */
    private void stop() {
        mediaPlayer.stop();
        cancelNotify();
    }

    /**
     * 得到当前的播放进度
     *
     * @return
     */
    private int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    /**
     * 得到当前音频的总时长
     *
     * @return
     */
    private int getDuration() {
        return mediaPlayer.getDuration();
    }

    /**
     * 得到艺术家
     *
     * @return
     */
    private String getArtist() {
        return audio.getArtist();
    }

    /**
     * 得到歌曲名字
     *
     * @return
     */
    private String getName() {
        return audio.getName();
    }

    /**
     * 得到歌曲播放的路径
     *
     * @return
     */
    private String getAudioPath() {
        return audio.getData();
    }

    /**
     * 播放下一个音频
     */
    private void next() {
        playNextPosition();
    }

    /**
     * 设置下一曲的位置
     */
    private void playNextPosition() {
/*
        int playmode = getPlayMode();
        // 三种播放模式 全部循环， 单曲循环， 随机播放
        if (playmode == AudioPlayService.REPEAT_ALL) {
            position = (position + 1) >= (audioArrayList.size() - 1) ? 0 : (position + 1);
        } else if (playmode == AudioPlayService.REPEAT_SINGLE) {

        } else {
            position = new Random().nextInt(audioArrayList.size() - 1);
        }
*/

        position++;

        if (position >= audioArrayList.size() - 1) {
            position = 0;
        }

        openAudio(position);
    }

    /**
     * 播放上一个音频
     */
    private void pre() {
        playPrePosition();
    }

    /**
     * 找到上一个音频的位置，并且播放
     */
    private void playPrePosition() {
        int playmode = getPlayMode();
        // 三种播放模式 全部循环， 单曲循环， 随机播放
/*        if (playmode == AudioPlayService.REPEAT_ALL) {
            position = (position - 1) <= 0 ? (audioArrayList.size() - 1) : (position - 1);
        } else if (playmode == AudioPlayService.REPEAT_SINGLE) {

        } else {
            position = new Random().nextInt(audioArrayList.size() - 1);
        }*/

        position--;

        if (position <= 0) {
            position = audioArrayList.size() - 1;
        }

        if (position < audioArrayList.size()) {
            openAudio(position);
        } else {
            position = audioArrayList.size() - 1;
        }
    }

    /**
     * 设置播放模式
     *
     * @param playmode
     */
    private void setPlayMode(int playmode) {
        this.playmode = playmode;
        //CacheUtils.putPlaymode(this,"playmode",playmode);

        if (playmode == AudioPlayService.REPEAT_SINGLE) {
            //单曲循环播放-不会触发播放完成的回调
            mediaPlayer.setLooping(true);
        } else {
            //不循环播放
            mediaPlayer.setLooping(false);
        }
    }

    /**
     * 得到播放模式
     *
     * @return
     */
    private int getPlayMode() {
        return AudioPlayService.REPEAT_ALL;
    }


    /**
     * 是否在播放音频
     *
     * @return
     */
    private boolean isPlaying() {
        if (mediaPlayer == null)
            return false;
        return mediaPlayer.isPlaying();
    }

    /**
     * 判断mediaplay是否为空
     *
     * @return
     */
    private boolean isNull() {
        if (mediaPlayer == null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 播放到什么位置
     *
     * @param msec
     */
    public void seekTo(int msec) {
        mediaPlayer.seekTo(msec);
    }

    private void getDataFromLocal() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                audioArrayList = new ArrayList<>();
                ContentResolver resolver = AudioPlayService.this.getContentResolver();
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

            }
        }.start();
    }

    NotificationManager manager = null;

    public void setNotify() {
        // Andriod O 可能有问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        } else {
            manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            // 获取意图
            Intent intent = new Intent(this, AudioActivity.class);
            intent.putExtra("Notification", true);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,
                    1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            // Notification和版本有关系，每一代的 Notification都会进行更改，因此推荐使用V4包下的Notification
            Notification notification = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.cd)
                    .setContentText("正在播放" + getName())
                    .setContentTitle("LearnEnglish")
                    .setContentIntent(pendingIntent)
                    .build();

            manager.notify(1, notification);
        }
    }

    public void cancelNotify() {
        if (manager != null) {
            manager.cancel(1);
            manager = null;
        }

    }
}
