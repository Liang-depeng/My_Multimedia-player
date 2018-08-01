package ldp.example.com.mymultimediaplayer.service;

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
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;

import ldp.example.com.mymultimediaplayer.IMyMusicPlayerAidlInterface;
import ldp.example.com.mymultimediaplayer.R;
import ldp.example.com.mymultimediaplayer.activity.LocalMusicPlayerActivity;
import ldp.example.com.mymultimediaplayer.domain.MusicItem;
import ldp.example.com.mymultimediaplayer.utils.Cache;

/**
 * created by ldp at 2018/7/27
 */
public class MymusicPlayerService extends Service {

    public static final String MUSIC_START = "ldp.com.example.mymultimediaplayer.MUSIC_START";
    private ArrayList<MusicItem> musicItems;
    private int position;
    private MusicItem musicItem;
    private MediaPlayer mMediaPlayer;

    public static final  int PLAY_NORMAL = 1;
    public  static final  int PLAY_SINFLE = 2;
    public  static final  int PLAY_ALL = 3;

    private int playmode = PLAY_NORMAL;
    @Override
    public void onCreate() {
        super.onCreate();
        playmode = Cache.getInt(this,"play_mode");
        getdatafromlocal();
    }

    private void getdatafromlocal() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                musicItems = new ArrayList<>();

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
                        MediaStore.Audio.Media.ALBUM_ID,

                        MediaStore.Audio.Media.TITLE
                };

                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        MusicItem musicItem = new MusicItem();

                        musicItems.add(musicItem);

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

                        String title = cursor.getString(7);
                        musicItem.setTitle(title);
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

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
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

        @Override
        public void seekTo(int position) throws RemoteException {
            mMediaPlayer.seekTo(position);
        }

        @Override
        public int getAudioSessionId() throws RemoteException {
            return mMediaPlayer.getAudioSessionId();
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
        if (musicItems!=null&&musicItems.size()>0){
            musicItem = musicItems.get(position);

            if (mMediaPlayer!=null){
//                mMediaPlayer.release();
                mMediaPlayer.reset();
            }

            try {
                mMediaPlayer = new MediaPlayer();

                mMediaPlayer.setOnPreparedListener(new MyOnPreparedListener2());
                mMediaPlayer.setOnCompletionListener(new MyOnCompletionListener2());
                mMediaPlayer.setOnErrorListener(new MyOnErrorListener2());

                mMediaPlayer.setDataSource(musicItem.getData_music());
                mMediaPlayer.prepareAsync();

                if (playmode==PLAY_SINFLE){
                    mMediaPlayer.setLooping(true);
                }else {
                    mMediaPlayer.setLooping(false);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            Toast.makeText(MymusicPlayerService.this,"     没有数据    ",Toast.LENGTH_LONG).show();
        }
    }

    private NotificationManager mManager;
    /**
     * 播放
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void start() {
        //
        mMediaPlayer.start();
        //当播放歌曲的时候，在状态栏显示的时候，点击进入播放界面
        mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //状态栏通知
        Intent intent = new Intent(this, LocalMusicPlayerActivity.class);
        intent.putExtra("Notification",true);//标识来自状态栏
        PendingIntent pendingIntent = PendingIntent.getActivity(this,1,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_zhuangtailan)
                .setContentTitle("ldp_music")
                .setContentText(" "+ getMusicName())
                .setContentIntent(pendingIntent)
                .build();
        mManager.notify(1,notification);
    }

    /**
     * 暂停
     */
    private void pause() {
        mMediaPlayer.pause();
        mManager.cancel(1);
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
        return (int) mMediaPlayer.getCurrentPosition();
    }

    /**
     * 时长
     */
    private int getDuration() {
        return (int) mMediaPlayer.getDuration();
    }

    private String getMusicPlayer() {
        return musicItem.getSingerName();
    }

    private String getMusicName() {
        return musicItem.getTitle();
    }

    private String getMusicPath() {
        return musicItem.getData_music();
    }

    private void next() {
        //根据当前播放模式，设置下一个位置
        setNextMusic();
        //根据当前播放模式和下标播放
        openNextMusic();
    }

    private void setNextMusic() {

        int playmode = getPlayMode();
        if (playmode==MymusicPlayerService.PLAY_NORMAL){
            position++;
        }else if (playmode==MymusicPlayerService.PLAY_SINFLE){
            position++;
            if (position>=musicItems.size()){
                position=0;
            }
        }else if (playmode==MymusicPlayerService.PLAY_ALL){
            position++;
            if (position>=musicItems.size()){
                position=0;
            }
        }else{
            position++;
        }
    }

    private void openNextMusic() {
        int playmode = getPlayMode();
        if (playmode==MymusicPlayerService.PLAY_NORMAL){
            if (position<musicItems.size()){
                openMusic(position);
            }else {
                position = musicItems.size()-1;
            }
        }else if (playmode==MymusicPlayerService.PLAY_SINFLE){
            openMusic(position);
        }else if (playmode==MymusicPlayerService.PLAY_ALL){
            openMusic(position);
        }else{
            if (position<musicItems.size()){
                openMusic(position);
            }else {
                position = musicItems.size()-1;
            }
        }
    }

    private void pre() {
        //根据当前播放模式，设置下一个位置
        setPreMusic();
        //根据当前播放模式和下标播放
        openPreMusic();
    }

    private void openPreMusic() {
        int playmode = getPlayMode();
        if (playmode==MymusicPlayerService.PLAY_NORMAL){
            if (position>=0){
                openMusic(position);
            }else {
                position = 0;
            }
        }else if (playmode==MymusicPlayerService.PLAY_SINFLE){
            openMusic(position);
        }else if (playmode==MymusicPlayerService.PLAY_ALL){
            openMusic(position);
        }else{
            if (position>=0){
                openMusic(position);
            }else {
                position = 0;
            }
        }
    }

    private void setPreMusic() {
        int playmode = getPlayMode();
        if (playmode==MymusicPlayerService.PLAY_NORMAL){
            position--;
        }else if (playmode==MymusicPlayerService.PLAY_SINFLE){
            position--;
            if (position<0){
                position=musicItems.size()-1;
            }
        }else if (playmode==MymusicPlayerService.PLAY_ALL){
            position--;
            if (position<0){
                position=musicItems.size()-1;
            }
        }else{
            position--;
        }
    }

    /**
     * 播放模式
     */
    private void setPlayMode(int playMode) {
        this.playmode = playMode;
        Cache.putInt(this,"play_mode",playMode);

        if (playmode==PLAY_SINFLE){
            mMediaPlayer.setLooping(true);
        }else {
            mMediaPlayer.setLooping(false);
        }
    }

    private int getPlayMode() {

        return playmode;
    }

    public boolean isPlaying(){
        return mMediaPlayer.isPlaying();
    }


    private class MyOnPreparedListener2 implements MediaPlayer.OnPreparedListener {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onPrepared(MediaPlayer mp) {
            //通知activity获取信息（广播）
            //notifyChange(MUSIC_START);
            EventBus.getDefault().post(musicItem);
            start();
        }
    }

//    private void notifyChange(String action) {
//        Intent intent = new Intent(action);
//        sendBroadcast(intent);
//
//    }

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
