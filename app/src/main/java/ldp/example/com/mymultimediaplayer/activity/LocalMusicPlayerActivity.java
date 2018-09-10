package ldp.example.com.mymultimediaplayer.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.x;

import java.io.File;

import ldp.example.com.mymultimediaplayer.IMyMusicPlayerAidlInterface;
import ldp.example.com.mymultimediaplayer.R;
import ldp.example.com.mymultimediaplayer.domain.MusicItem;
import ldp.example.com.mymultimediaplayer.service.MymusicPlayerService;
import ldp.example.com.mymultimediaplayer.utils.Lyricutils;
import ldp.example.com.mymultimediaplayer.utils.TimeUtils;
import ldp.example.com.mymultimediaplayer.view.BaseVisualizerView;
import ldp.example.com.mymultimediaplayer.view.ShowLyricView;

/**
 * created by ldp at 2018/7/26
 */
public class LocalMusicPlayerActivity extends Activity implements View.OnClickListener {

    private static final int PROGRESS = 1;
    private static final int SHOW_LYRIC = 2;
   // @ViewInject(R.id.music_pic1)
  //  private ImageView music_pic_list01;

    private int position;
    private IMyMusicPlayerAidlInterface service;
    private RelativeLayout musicHead;
    private ImageView musicPic1;
    private SeekBar seekbarMusicplayer;
    private TextView musicPlayingTime;
    private TextView musicDuration;
    private ImageView musicSwitch1;
    private ImageView musicPlayerPre;
    private ImageView musicPlayerPause;
    private ImageView musicPlayerNext;
    private TextView mSinger;
    private TextView mSong_name;
//    private MyReceiver receiver;
    private TimeUtils mUtils;
    private boolean notification;

    private ShowLyricView show_lyric;
    private BaseVisualizerView baseVisualizerView;



    private ServiceConnection con = new ServiceConnection() {

        /**
         * 连接成功调用
         * @param name
         * @param iBinder
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            service = IMyMusicPlayerAidlInterface.Stub.asInterface(iBinder);

            if (service!=null){
                try {
                    if (!notification){
                        service.openMusic(position);
                    }else {
                        System.out.println("onServiceConnected" + Thread.currentThread().getName());
                        showData();
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 断开连接时调用
         * @param name
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {
            try {
                if (service!=null){
                    service.stop();
                    service=null;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localmusic_layout);
        x.view().inject(LocalMusicPlayerActivity.this);
        initData();
        findViews();
      //  initView();
        getData();
        bindMusicService();

    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case PROGRESS:
                    try {
                        //得到当前进度
                        int currentPosition = service.getCurrentPosition();
                        //设置seekbar.progress
                        seekbarMusicplayer.setProgress(currentPosition);
                        //时间进度
                        musicPlayingTime.setText(mUtils.stringForTime(currentPosition)+"/");
                        musicDuration.setText(mUtils.stringForTime(service.getDuration()));
                        //每秒跟新一次
                        mHandler.removeMessages(PROGRESS);
                        mHandler.sendEmptyMessageDelayed(PROGRESS,1000);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case SHOW_LYRIC:
                    //得到当前进度
                    try {
                        int currentPosition = service.getCurrentPosition();

                        //传入进度至show_lyric_view
                        show_lyric.setNextLyric(currentPosition);
                        //实时发消息
                        mHandler.removeMessages(SHOW_LYRIC);
                        mHandler.sendEmptyMessage(SHOW_LYRIC);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }


                    break;
            }
        }
    };

    private void initData() {
        mUtils = new TimeUtils();
//        //注册广播
//        MyReceiver receiver = new MyReceiver();
//        IntentFilter intentFilter =new IntentFilter();
//        intentFilter.addAction(MymusicPlayerService.MUSIC_START);
//        registerReceiver(receiver, intentFilter);
        //注册
        EventBus.getDefault().register(this);//this,当前类
    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ShowData(null);
        }
    }

    //订阅方法
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = false,priority = 0)
    public void ShowData(MusicItem musicItem) {

        //showLyric();
        showgeci();
        showData();
        checkshowPlayMode();
        setupVisualizerFxAndUi();

    }

    private Visualizer mVisualizer;
    /**
     * 生成一个VisualizerView对象，使音频频谱的波段能够反映到 VisualizerView上
     */
    private void setupVisualizerFxAndUi()
    {
        try {
            int audioSessionid = service.getAudioSessionId();
            System.out.println("audioSessionid=="+audioSessionid);
            mVisualizer = new Visualizer(audioSessionid);
            // 参数内必须是2的位数
            mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
            // 设置允许波形表示，并且捕获它
            baseVisualizerView.setVisualizer(mVisualizer);
            mVisualizer.setEnabled(true);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private void showgeci(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                Lyricutils lyricutils = new Lyricutils();
                //传歌词文件
                try {
                    String path = service.getMusicPath();

                    path = path.substring(0,path.lastIndexOf("."));
                    File file = new File(path+".lrc");
                    if (!file.exists()){
                        file = new File(path+ ".txt");
                    }
                    lyricutils.readLyricFile(file);
                    show_lyric.setLyrics(lyricutils.getLyrics());

                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                if (lyricutils.isLyric()){
                    mHandler.sendEmptyMessage(SHOW_LYRIC);
                }
            }
        }.start();
    }

//    private void showLyric(){
//        Lyricutils lyricutils = new Lyricutils();
//        //传歌词文件
//        try {
//            String path = service.getMusicPath();
//
//            path = path.substring(0,path.lastIndexOf("."));
//            File file = new File(path+".lrc");
//            if (!file.exists()){
//                file = new File(path+ ".txt");
//            }
//            lyricutils.readLyricFile(file);
//            show_lyric.setLyrics(lyricutils.getLyrics());
//
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//
//        if (lyricutils.isLyric()){
//            mHandler.sendEmptyMessage(SHOW_LYRIC);
//        }
//
//
//    }

    private void showData() {
        try {
            mSinger.setText(service.getMusicPlayer());
            mSong_name.setText(service.getMusicName());
            //
            seekbarMusicplayer.setMax(service.getDuration());

            mHandler.sendEmptyMessage(PROGRESS);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-07-29 09:13:18 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        mSinger = (TextView) findViewById(R.id.singer);
        mSong_name = (TextView) findViewById(R.id.song_name);
        musicHead = (RelativeLayout)findViewById( R.id.music_head );
        //musicPic1 = (ImageView)findViewById( R.id.music_pic1 );
        seekbarMusicplayer = (SeekBar)findViewById( R.id.seekbar_musicplayer );
        musicPlayingTime = (TextView)findViewById( R.id.music_playing_time );
        musicDuration = (TextView)findViewById( R.id.music_duration );
        musicSwitch1 = (ImageView)findViewById( R.id.music_switch_1 );
        musicPlayerPre = (ImageView)findViewById( R.id.music_player_pre );
        musicPlayerPause = (ImageView)findViewById( R.id.music_player_pause );
        musicPlayerNext = (ImageView)findViewById( R.id.music_player_next );
        show_lyric = (ShowLyricView) findViewById(R.id.show_lyric);
        baseVisualizerView = (BaseVisualizerView) findViewById(R.id.baseVisualizerView);

        musicSwitch1.setOnClickListener(this);
        musicPlayerPre.setOnClickListener(this);
        musicPlayerPause.setOnClickListener(this);
        musicPlayerNext.setOnClickListener(this);

        seekbarMusicplayer.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener2());
    }

    public void onClick(View v){
        if (v==musicSwitch1){
            setPlayMode();
        }else if (v==musicPlayerPre){
            if (service!=null){
                try {
                    service.pre();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }else if (v==musicPlayerPause){
            if (service!=null){
                try {
                    if (service.isPlaying()){
                        service.pause();
                        musicPlayerPause.setImageResource(R.drawable.ic_musicplayer_pauseing);
                    }else{
                        service.start();
                        musicPlayerPause.setImageResource(R.drawable.ic_musicplayer_starting);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }else if(v==musicPlayerNext){
            if (service!=null){
                try {
                    service.next();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setPlayMode() {
        try {
            int playmode = service.getPlayMode();
            if (playmode==MymusicPlayerService.PLAY_NORMAL){
                playmode=MymusicPlayerService.PLAY_SINFLE;
            }else if (playmode==MymusicPlayerService.PLAY_SINFLE){
                playmode=MymusicPlayerService.PLAY_ALL;
            }else if (playmode==MymusicPlayerService.PLAY_ALL){
                playmode=MymusicPlayerService.PLAY_NORMAL;
            }else{
                playmode=MymusicPlayerService.PLAY_NORMAL;
            }

            service.setPlayMode(playmode);

            showPlayMode();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void showPlayMode() {
        try {
            int playmode = service.getPlayMode();
            if (playmode==MymusicPlayerService.PLAY_NORMAL){
                musicSwitch1.setImageResource(R.drawable.ic_musicplayer_switch1);
                Toast.makeText(LocalMusicPlayerActivity.this,"顺序播放",Toast.LENGTH_LONG).show();
            }else if (playmode==MymusicPlayerService.PLAY_SINFLE){
                musicSwitch1.setImageResource(R.drawable.ic_musicplayer_switch3);
                Toast.makeText(LocalMusicPlayerActivity.this,"单曲循环",Toast.LENGTH_LONG).show();
            }else if (playmode==MymusicPlayerService.PLAY_ALL){
                musicSwitch1.setImageResource(R.drawable.ic_musicplayer_switch2);
                Toast.makeText(LocalMusicPlayerActivity.this,"列表循环",Toast.LENGTH_LONG).show();
            }else{
                musicSwitch1.setImageResource(R.drawable.ic_musicplayer_switch1);
                Toast.makeText(LocalMusicPlayerActivity.this,"顺序播放",Toast.LENGTH_LONG).show();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private void checkshowPlayMode() {
        try {
            int playmode = service.getPlayMode();
            if (playmode==MymusicPlayerService.PLAY_NORMAL){
                musicSwitch1.setImageResource(R.drawable.ic_musicplayer_switch1);
            }else if (playmode==MymusicPlayerService.PLAY_SINFLE){
                musicSwitch1.setImageResource(R.drawable.ic_musicplayer_switch3);
            }else if (playmode==MymusicPlayerService.PLAY_ALL){
                musicSwitch1.setImageResource(R.drawable.ic_musicplayer_switch2);
            }else{
                musicSwitch1.setImageResource(R.drawable.ic_musicplayer_switch1);
            }

            //校验播放暂停按钮
            if (service.isPlaying()){
                musicPlayerPause.setImageResource(R.drawable.ic_musicplayer_pauseing);
            }else {
                musicPlayerPause.setImageResource(R.drawable.ic_musicplayer_starting);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private void bindMusicService() {
        Intent intent = new Intent(this, MymusicPlayerService.class);
        intent.setAction("ldp.com.mymultimediaplayer.OPEN_MUSIC");
        bindService(intent, con, Context.BIND_AUTO_CREATE);
        startService(intent);//  不至于实例化多个服务
    }

    private void getData() {
        notification=getIntent().getBooleanExtra("Notification",false);
        if (!notification) {
            position = getIntent().getIntExtra("position", 0);
        }
    }

    private void initView() {
        // music_pic_list01 = (ImageView) findViewById(R.id.music_pic1);
      //  music_pic_list01.setBackgroundResource(R.drawable.music_pic_list);
       // AnimationDrawable animationDrawable = (AnimationDrawable) music_pic_list01.getBackground();
        //animationDrawable.start();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
//        if (receiver!=null){
//            unregisterReceiver(receiver);
//            receiver=null;
//        }
        //取消注册
        EventBus.getDefault().unregister(this);
        if (con!=null){
            unbindService(con);
            con=null;
        }
        super.onDestroy();
    }

    private class MyOnSeekBarChangeListener2 implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser){
                try {
                    service.seekTo(progress);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVisualizer!=null){
            mVisualizer.release();
        }
    }
}
