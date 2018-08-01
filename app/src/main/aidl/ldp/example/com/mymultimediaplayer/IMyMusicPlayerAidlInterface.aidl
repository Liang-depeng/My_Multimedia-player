// IMyMusicPlayerAidlInterface.aidl
package ldp.example.com.mymultimediaplayer;

// Declare any non-default types here with import statements

interface IMyMusicPlayerAidlInterface {
//    /**
//     * Demonstrates some basic types that you can use as parameters
//     * and return values in AIDL.
//     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);
            /**
                 * 根据对应位置打开对应的文件
                 * @param position
                 */
                 void openMusic(int position);

                /**
                 * 播放
                 */
                 void start();

                /**
                 * 暂停
                 */
                 void pause();

                /**
                 * 停止
                 */
                void stop();

                /**
                 * 播放进度
                 */
                 int getCurrentPosition();

                /**
                 * 时长
                 */
                int getDuration();

                String getMusicPlayer();

                String getMusicName();

                String getMusicPath();

                void next();

                void pre();

                /**
                 * 播放模式
                 */
                void setPlayMode(int playMode);

                int getPlayMode ();

                boolean isPlaying();

                void seekTo(int position);

                 int getAudioSessionId();
}


