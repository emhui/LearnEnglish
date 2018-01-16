package com.ycxy.ymh.service;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.ycxy.ymh.activity.AudioActivity;
import com.ycxy.ymh.activity.OnlineAudioActivity;
import com.ycxy.ymh.bean.Audio;
import com.ycxy.ymh.bean2.DataBean;
import com.ycxy.ymh.learnenglish.IAudioPlayService;
import com.ycxy.ymh.learnenglish.R;
import com.ycxy.ymh.utils.CacheUtils;
import com.ycxy.ymh.utils.Constants;
import com.ycxy.ymh.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
    /**
     * 第三方音频的地址
     */
    private String otherPath = null;

    private AudioManager mAudioManager;


    @Override
    public void onCreate() {
        super.onCreate();
        initData();
    }

    private void initData() {
        mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        mFocusChangeListener = new MyAudioFocusChangeListener();
        playmode = new Utils().getPlaymode(this, "playmode");
        position = new Utils().getPosfStor(this);

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
        public void openOtherAudio(String path) throws RemoteException {
            service.openOtherAudio(path);
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
            if (service != null)
                return service.getCurrentPosition();
            return 0;
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
     * @param
     */
    public void openOtherAudio(String path) {
        this.otherPath = path;
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
                mediaPlayer.setDataSource(path);
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
     * 根据位置打开对应的音频文件,并且播放
     *
     * @param position
     */
    public void openAudio(int position) {
        this.position = position;
        otherPath = null;
        Log.d(TAG, "openAudio: " + position);
        if (audioArrayList != null && audioArrayList.size() > 0) {

            audio = audioArrayList.get(position);

            if (mediaPlayer != null) {
                mediaPlayer.reset();
            }

            try {
                initPlayer();

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

    private void initPlayer() throws IOException {
        mediaPlayer = new MediaPlayer();
        //设置监听：播放出错，播放完成，准备好
        mediaPlayer.setOnPreparedListener(new MyOnPreparedListener());
        mediaPlayer.setOnCompletionListener(new MyOnCompletionListener());
        mediaPlayer.setOnErrorListener(new MyOnErrorListener());
        mediaPlayer.setDataSource(audio.getData());
        mediaPlayer.prepareAsync();
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
            switch (what) {
                case -38:
                    Log.d(TAG, "MEDIA_ERROR_IO++++++++++++");
                    break;
                case -1004:
                    Log.d(TAG, "MEDIA_ERROR_IO");
                    break;
                case -1007:
                    Log.d(TAG, "MEDIA_ERROR_MALFORMED");
                    break;
                case 200:
                    Log.d(TAG, "MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK");
                    break;
                case 100:
                    Log.d(TAG, "MEDIA_ERROR_SERVER_DIED");
                    break;
                case -110:
                    Log.d(TAG, "MEDIA_ERROR_TIMED_OUT");
                    break;
                case 1:
                    Log.d(TAG, "MEDIA_ERROR_UNKNOWN");
                    break;
                case -1010:
                    Log.d(TAG, "MEDIA_ERROR_UNSUPPORTED");
                    break;
            }
            switch (extra) {
                case 800:
                    Log.d(TAG, "MEDIA_INFO_BAD_INTERLEAVING");
                    break;
                case 702:
                    Log.d(TAG, "MEDIA_INFO_BUFFERING_END");
                    break;
                case 701:
                    Log.d(TAG, "MEDIA_INFO_METADATA_UPDATE");
                    break;
                case 802:
                    Log.d(TAG, "MEDIA_INFO_METADATA_UPDATE");
                    break;
                case 801:
                    Log.d(TAG, "MEDIA_INFO_NOT_SEEKABLE");
                    break;
                case 1:
                    Log.d(TAG, "MEDIA_INFO_UNKNOWN");
                    break;
                case 3:
                    Log.d(TAG, "MEDIA_INFO_VIDEO_RENDERING_START");
                    break;
                case 700:
                    Log.d(TAG, "MEDIA_INFO_VIDEO_TRACK_LAGGING");
                    break;
            }

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
            // 发送消息
            EventBus.getDefault().post(audio);
            start();
        }
    }

    // 启动
    public void start() {
        int result = mAudioManager.requestAudioFocus(mFocusChangeListener, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            EventBus.getDefault().post(new Constants());
            // 每次播放都将地址保存到本地，防止被杀死
            new Utils().savePos2Stor(this, position);
            getName();
            mediaPlayer.start();
            setNotify();
        }
    }

    private MyAudioFocusChangeListener mFocusChangeListener;

    class MyAudioFocusChangeListener implements AudioManager.OnAudioFocusChangeListener {

        private int mPreviousState;

        private boolean mShouldStart = true;

        @Override
        public void onAudioFocusChange(int focusChange) {
            handlerAudioFocus(focusChange);
            mPreviousState = focusChange;
        }

        private void handlerAudioFocus(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    handlerAudioFocusGain();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    if (mediaPlayer != null) {
/*                        mediaPlayer.release();
                        mediaPlayer = null;*/
                        mediaPlayer.pause();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    mediaPlayer.pause();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
//                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mOriginalVol / 2,
//                            AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                    // mediaPlayer.setVolume(0.5f, 0.5f);
                    break;
            }
        }

        private void handlerAudioFocusGain() {
            switch (mPreviousState) {
                case AudioManager.AUDIOFOCUS_LOSS:
/*                    try {
                        initPlayer();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/
                    mShouldStart = false;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    if (mediaPlayer != null) {
                        if (mShouldStart) {
                            // mediaPlayer.start();
                        } else {
                            mShouldStart = true;
                        }
                    }
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    //mediaPlayer.setVolume(1, 1);
                    break;
                default:
            }
        }
    }

    /**
     * 播暂停音乐
     */
    private void pause() {
        EventBus.getDefault().post(new Utils());
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
        if (mediaPlayer != null)
            return mediaPlayer.getCurrentPosition();
        return 0;
    }

    /**
     * 得到当前音频的总时长
     *
     * @return
     */
    private int getDuration() {
        if (mediaPlayer != null)
            return mediaPlayer.getDuration();
        return 0;
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
        String name = "";
        if (otherPath == null){
            name = new Utils().getAudioName(audio.getName());
            CacheUtils.saveToLocal(AudioPlayService.this, OnlineAudioActivity.key_audioname,name);
        }
        else {
            if (!otherPath.startsWith("http")){
                name = getRealPathName();

                CacheUtils.saveToLocal(AudioPlayService.this, OnlineAudioActivity.key_audioname,name);
            }
        }

        return name;
    }

    @NonNull
    private String getRealPathName() {
        String name;// /storage/emulated/0/lyric/庐州月.lrc
        name = otherPath.split("\\.")[0].trim(); // /storage/emulated/0/lyric/庐州月
        String[] ns = name.split("/");
        int size = ns.length;
        name = ns[size - 1];
        if (name.contains("_")) {
            name = name.split("_")[0].trim();
        }

        if (name.contains("-")){
            name = name.split("-")[1].trim();
        }
        return name;
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
        int mode = getPlayMode();
        if (mode == AudioPlayService.REPEAT_ALL) {
            position++;
            if (position > audioArrayList.size() - 1) {
                position = 0;
            }
        } else if (mode == AudioPlayService.REPEAT_SINGLE) {
            // openAudio(position);
        } else if (mode == AudioPlayService.REPEAT_RANDOM) {
            position = new Random().nextInt(audioArrayList.size() - 1);
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
        int mode = getPlayMode();

        if (mode == AudioPlayService.REPEAT_ALL) {
            position--;
            if (position < 0) {
                position = audioArrayList.size() - 1;
            }
        } else if (mode == AudioPlayService.REPEAT_SINGLE) {
            // openAudio(position);
        } else if (mode == AudioPlayService.REPEAT_RANDOM) {
            position = new Random().nextInt(audioArrayList.size() - 1);
        }

        openAudio(position);
    }

    /**
     * 设置播放模式
     *
     * @param playmode
     */
    private void setPlayMode(int playmode) {
        this.playmode = playmode;
        // 保存缓存到
        new Utils().putPlaymode(this, "playmode", playmode);

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
        return playmode;
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

    NotificationManager mNotificationManager = null;

    public void setNotify() {

        // Andriod O 可能有问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        } else {
            mNotify();
        }
    }

    private void mNotify() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.music)
                        .setContentTitle("LE Music")
                        .setContentText(getName());
        Intent resultIntent = new Intent(this, AudioActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(AudioActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());
    }

    public void cancelNotify() {
        if (mNotificationManager != null) {
            mNotificationManager.cancel(1);
            mNotificationManager = null;
        }
    }

    @Override
    public void onDestroy() {
        cancelNotify();
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.ASYNC, sticky = false, priority = 0)
    public void scanDataBase(DataBean dataBean){
        getDataFromLocal();
    }}
