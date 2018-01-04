// IAudioPlayService.aidl
package com.ycxy.ymh.learnenglish;

// Declare any non-default types here with import statements

interface IAudioPlayService {
    /**
     * 根据位置打开对应的音频文件,并且播放
     *
     * @param position
     */
     void openAudio(int position);

    // 启动
     void start();
    /**
     * 播暂停音乐
     */
     void pause();

    /**
     * 停止
     */
     void stop() ;

    /**
     * 得到当前的播放进度
     *
     * @return
     */
     int getCurrentPosition();

    /**
     * 得到当前音频的总时长
     *
     * @return
     */
     int getDuration() ;
    /**
     * 得到艺术家
     *
     * @return
     */
     String getArtist();

    /**
     * 得到歌曲名字
     *
     * @return
     */
     String getName();

    /**
     * 得到歌曲播放的路径
     *
     * @return
     */
     String getAudioPath();

    /**
     * 播放下一个音频
     */
     void next() ;

    /**
     * 播放上一个音频
     */
     void pre() ;

    /**
     * 设置播放模式
     *
     * @param playmode
     */
     void setPlayMode(int playmode);
    /**
     * 得到播放模式
     *
     * @return
     */
     int getPlayMode();


    /**
     * 是否在播放音频
     * @return
     */
     boolean isPlaying();

    /**
     * 判断mediaplay是否为空
     * @return
     */
    boolean isNull();

    void seekTo(int mesc);
}
