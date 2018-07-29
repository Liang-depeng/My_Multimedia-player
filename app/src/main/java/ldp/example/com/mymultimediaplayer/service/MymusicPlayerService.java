package ldp.example.com.mymultimediaplayer.service;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import io.vov.vitamio.MediaPlayer;
import ldp.example.com.mymultimediaplayer.IMyMusicPlayerAidlInterface;
import ldp.example.com.mymultimediaplayer.domain.MusicItem;

/**
 * created by ldp at 2018/7/27
 */
public class MymusicPlayerService extends Service {

    public static final String MUSIC_START = "ldp.com.example.mymultimediaplayer.MUSIC_START";
    private ArrayList<MusicItem> mMusicItems;
    private int position;
    private MusicItem musicItem;
    private MediaPlayer mMediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        getdatafromlocal();
    }

    private void getdatafromlocal() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                mMusicItems = new ArrayList<>();

                ContentResolver resolver = getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

                String objs[] = {
                        //视频在sd卡名称
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        //视频总时长
                        MediaStore.Audio.Media.DURATION,
                        //视频大小
                        MediaStore.Audio.Media.SIZE,
                        //视频绝对地址
                        MediaStore.Audio.Media.DATA,
                        //艺术家名称
                        MediaStore.Audio.Media.ARTIST,
                        //唱片
                        MediaStore.Audio.Media.ALBUM,
                        //唱片ID
                        MediaStore.Audio.Media.ALBUM_ID
                };

                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        MusicItem musicItem = new MusicItem();

                        mMusicItems.add(musicItem);

                        String name = cursor.getString(0);
                        musicItem.setName(name);

                        long duration = cursor.getLong(1);
                        musicItem.setDuration(duration);

                        long size = cursor.getLong(2);
                        musicItem.setMusicsize(size);

                        String data = cursor.getString(3);
                        musicItem.setData_music(data);

                        String artist = cursor.getString(4);
                        musicItem.setSingerName(artist);

                        String album = cursor.getString(5);
                        musicItem.setAlbum(album);

                        int abbum_id = cursor.getInt(6);
                        musicItem.setAlbum_id(abbum_id);
                    }
                    cursor.close();
                }

            }

        }.start();
    }

    private IMyMusicPlayerAidlInterface.Stub mStub = new IMyMusicPlayerAidlInterface.Stub() {
        MymusicPlayerService service = MymusicPlayerService.this;

        @Override
        public void openMusic(int position) throws RemoteException {
            service.openMusic(position);
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
        public String getMusicPlayer() throws RemoteException {
            return service.getMusicPlayer();
        }

        @Override
        public String getMusicName() throws RemoteException {
            return service.getMusicName();
        }

        @Override
        public String getMusicPath() throws RemoteException {
            return service.getMusicPath();
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
        public void setPlayMode(int playMode) throws RemoteException {
            service.setPlayMode(playMode);
        }

        @Override
        public int getPlayMode() throws RemoteException {
            return service.getPlayMode();
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return service.isPlaying();
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mStub;
    }

    /**
     * 根据对应位置打开对应的文件
     *
     * @param position
     */
    private void openMusic(int position) {
        this.position = position;
        if (mMusicItems!=null&&mMusicItems.size()>0){
            musicItem = mMusicItems.get(position);

            if (mMediaPlayer!=null){
                mMediaPlayer.release();
                mMediaPlayer.reset();
            }

            try {
                mMediaPlayer = new MediaPlayer(this);

                mMediaPlayer.setOnPreparedListener(new MyOnPreparedListener2());
                mMediaPlayer.setOnCompletionListener(new MyOnCompletionListener2());
                mMediaPlayer.setOnErrorListener(new MyOnErrorListener2());

                mMediaPlayer.setDataSource(musicItem.getData_music());
                mMediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            Toast.makeText(MymusicPlayerService.this,"",Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 播放
     */
    private void start() {
        mMediaPlayer.start();
    }

    /**
     * 暂停
     */
    private void pause() {
        mMediaPlayer.pause();
    }

    /**
     * 停止
     */
    private void stop() {
    }

    /**
     * 播放进度
     */
    private int getCurrentPosition() {
        return 0;
    }

    /**
     * 时长
     */
    private int getDuration() {
        return 0;
    }

    private String getMusicPlayer() {
        return musicItem.getSingerName();
    }

    private String getMusicName() {
        return musicItem.getName();
    }

    private String getMusicPath() {
        return "";
    }

    private void next() {

    }

    private void pre() {

    }

    /**
     * 播放模式
     */
    private void setPlayMode(int playMode) {

    }

    private int getPlayMode() {

        return 0;
    }

    public boolean isPlaying(){
        return mMediaPlayer.isPlaying();
    }


    private class MyOnPreparedListener2 implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
            //通知activity获取信息（广播）
            notifyChange(MUSIC_START);
            start();
        }
    }

    private void notifyChange(String action) {
        Intent intent = new Intent(action);
        sendBroadcast(intent);

    }

    private class MyOnCompletionListener2 implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            next();
        }
    }

    private class MyOnErrorListener2 implements MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            next();
            return true;
        }
    }
}
