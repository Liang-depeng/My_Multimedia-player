package ldp.example.com.mymultimediaplayer.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
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

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import ldp.example.com.mymultimediaplayer.IMyMusicPlayerAidlInterface;
import ldp.example.com.mymultimediaplayer.R;
import ldp.example.com.mymultimediaplayer.service.MymusicPlayerService;

/**
 * created by ldp at 2018/7/26
 */
public class LocalMusicPlayerActivity extends Activity implements View.OnClickListener {

    private static final int PROGRESS = 1;
    @ViewInject(R.id.music_pic1)
    private ImageView music_pic_list01;

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
    private MyReceiver receiver;



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
                    service.openMusic(position);
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
        initView();
        getData();
        bindMusicService();

    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case PROGRESS:

                    break;
            }
        }
    };

    private void initData() {
        MyReceiver receiver = new MyReceiver();
        IntentFilter intentFilter =new IntentFilter();
        intentFilter.addAction(MymusicPlayerService.MUSIC_START);
        registerReceiver(receiver, intentFilter);
    }

    private class MyReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            showData();
        }
    }

    private void showData() {
        try {
            mSinger.setText(service.getMusicPlayer());
            mSong_name.setText(service.getMusicName());

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
        musicPic1 = (ImageView)findViewById( R.id.music_pic1 );
        seekbarMusicplayer = (SeekBar)findViewById( R.id.seekbar_musicplayer );
        musicPlayingTime = (TextView)findViewById( R.id.music_playing_time );
        musicDuration = (TextView)findViewById( R.id.music_duration );
        musicSwitch1 = (ImageView)findViewById( R.id.music_switch_1 );
        musicPlayerPre = (ImageView)findViewById( R.id.music_player_pre );
        musicPlayerPause = (ImageView)findViewById( R.id.music_player_pause );
        musicPlayerNext = (ImageView)findViewById( R.id.music_player_next );

        musicSwitch1.setOnClickListener(this);
        musicPlayerPre.setOnClickListener(this);
        musicPlayerPause.setOnClickListener(this);
        musicPlayerNext.setOnClickListener(this);
    }

    public void onClick(View v){
        if (v==musicSwitch1){

        }else if (v==musicPlayerPre){

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

        }
    }

    private void bindMusicService() {
        Intent intent = new Intent(this, MymusicPlayerService.class);
        intent.setAction("ldp.com.mymultimediaplayer.OPEN_MUSIC");
        bindService(intent, con, Context.BIND_AUTO_CREATE);
        startService(intent);//不至于实例化多个服务
    }

    private void getData() {
        position = getIntent().getIntExtra("position", 0);

    }

    private void initView() {
        // music_pic_list01 = (ImageView) findViewById(R.id.music_pic1);
        music_pic_list01.setBackgroundResource(R.drawable.music_pic_list);
        AnimationDrawable animationDrawable = (AnimationDrawable) music_pic_list01.getBackground();
        animationDrawable.start();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    protected void onDestroy() {
        if (receiver!=null){
            unregisterReceiver(receiver);
            receiver=null;
        }
        super.onDestroy();
    }
}
