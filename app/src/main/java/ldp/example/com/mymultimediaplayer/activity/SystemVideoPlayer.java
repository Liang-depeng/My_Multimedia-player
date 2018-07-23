package ldp.example.com.mymultimediaplayer.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.speech.tts.Voice;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.style.UpdateAppearance;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ldp.example.com.mymultimediaplayer.R;
import ldp.example.com.mymultimediaplayer.domain.MediaItem;
import ldp.example.com.mymultimediaplayer.utils.TimeUtils;
import ldp.example.com.mymultimediaplayer.view.Myvideoview;

/**
 * created by ldp at 2018/7/18
 */
public class SystemVideoPlayer extends Activity implements View.OnClickListener {

    private static final int PROGRESS = 1;
    private static final int FULL_SCREEN = 1;
    private static final int DEFALUT_SCREEN = 0;
    private boolean SHOW;
    private static final int HIDE_MESSAGE = 2;
    private boolean IS_FULLSCREEN = false;
    private Myvideoview mVideoView;
    private Uri mUri;
    private LinearLayout videoTop;
    private TextView videoName;
    private ImageView ivBattery;
    private TextView ivSystemTime;
    private Button btnVoice;
    private SeekBar seekbarVoice;
    private Button btnSwitchPlayer;
    private LinearLayout videoBottom;
    private TextView tvCurrentTime;
    private SeekBar seekbarVideo;
    private TextView tvDuration;
    private Button btnExit;
    private Button btnVideoPre;
    private Button btnVideoStartPause;
    private Button btnVideoNext;
    private Button btnSwitchVideoScreen;
    private TimeUtils mTimeUtils;
    private MyReceiver mMyReceiver; //监听电量变化广播
    private RelativeLayout mVideo_bofangqi;
    private ArrayList<MediaItem> mMediaItems;//c传入的视频列表
    private int position;

    private GestureDetector mDetector;//手势识别器
    private RelativeLayout mMedia_controller;
    private int screenwidth;
    private int screenheight;
    private int videowidth;
    private int videoheight;
    private AudioManager mAudioManager;//音量
    private int currentVoice;
    private int voiceMax;//0-15
    private boolean IS_VOICE = false;


    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_systemvideoplayer);
        findViews();
        //隐藏状态栏
        mVideo_bofangqi.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        initData();


        //准备好的监听
        mVideoView.setOnPreparedListener(new MyOnPreparedListener());
        //播放失败监听
        mVideoView.setOnErrorListener(new MyOnErrorListener());
        //播放完成监听
        mVideoView.setOnCompletionListener(new MyOnCompletionListener());
        //视频拖动
        seekbarVideo.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());
        //音量拖动
        seekbarVoice.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener2());

        //得到播放地址
        getData();
        setData();
        //        /**
        //        * 设置控制面板
        //        * 安卓系统自带控制面板
        //        */
        // mVideoView.setMediaController(new MediaController(this));
    }

    private void setData() {

        if (mMediaItems != null && mMediaItems.size() > 0) {
            MediaItem mediaItem = mMediaItems.get(position);
            videoName.setText(mediaItem.getName());
            mVideoView.setVideoPath(mediaItem.getData());

        } else if (mUri != null) {
            mVideoView.setVideoURI(mUri);
        } else {
            Toast.makeText(this, "无数据传输", Toast.LENGTH_LONG).show();
        }
        setButton();
    }

    private void getData() {
        //得到播放地址
        mUri = getIntent().getData();

        mMediaItems = (ArrayList<MediaItem>) getIntent().getSerializableExtra("local_video_list");
        position = getIntent().getIntExtra("position", 0);

    }

    @Override
    protected void onDestroy() {
        /**
         * 释放资源的时候，先释放子类，再释放父类
         * 取消注册电量监听
         */
        if (mMyReceiver != null) {
            unregisterReceiver(mMyReceiver);
            mMyReceiver = null;
        }

        super.onDestroy();
    }

    private void initData() {
        mTimeUtils = new TimeUtils();
        mMyReceiver = new MyReceiver();

        IntentFilter intentfiler = new IntentFilter();
        intentfiler.addAction(Intent.ACTION_BATTERY_CHANGED);
        //注册电量广播
        registerReceiver(mMyReceiver, intentfiler);
        /**
         * 手势识别器
         */
        mDetector = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);//长按
                startandpause();
            }
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                SwitchScreen();
                return super.onDoubleTap(e);//双击
                //
            }
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if(SHOW){
                    hideMediacontrollerLayout();
                    //移除隐藏的消息
                    mHandler.removeMessages(HIDE_MESSAGE);
                }else {
                    showMediacontrollerLayout();
                    mHandler.sendEmptyMessageDelayed(HIDE_MESSAGE,4000);
                }
                return super.onSingleTapConfirmed(e);//单击

            }
        });

       // getWindowManager().getDefaultDisplay().getWidth()
        /**
         * 得到屏幕的宽高
         */
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenwidth = displayMetrics.widthPixels;
        screenheight = displayMetrics.heightPixels;

        mAudioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
        currentVoice = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        voiceMax = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        seekbarVoice.setMax(voiceMax);//最大音量
        seekbarVoice.setProgress(currentVoice);//当前音量
    }

    private void SwitchScreen() {
        if (IS_FULLSCREEN){
            setVideoSize(DEFALUT_SCREEN);
        }else {
            setVideoSize(FULL_SCREEN);
        }
    }

    private void setVideoSize(int screen) {
        switch(screen){
            case DEFALUT_SCREEN:
                //设置画面大小 -默认视频宽高比例
                int mVideoWidth = videowidth;
                int mVideoHeight = videoheight;
                int width = screenwidth;
                int height = screenheight;
                // for compatibility, we adjust size based on aspect ratio
                if ( mVideoWidth * height  < width * mVideoHeight ) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * mVideoWidth / mVideoHeight;
                } else if ( mVideoWidth * height  > width * mVideoHeight ) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * mVideoHeight / mVideoWidth;
                }
                mVideoView.videoSize(width,height);

                //按钮状态 -全屏
                btnSwitchVideoScreen.setBackgroundResource(R.drawable.btn_switch_video_selector);
                IS_FULLSCREEN = false;
                break;
            case FULL_SCREEN:
                //设置画面大小  -全頻
                mVideoView.videoSize(screenwidth,screenheight);
                btnSwitchVideoScreen.setBackgroundResource(R.drawable.btn_switch_defult_video_selector);
                //按钮状态  -默认
                IS_FULLSCREEN = true;
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //传递事件给手势识别器
        mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
    public void showMediacontrollerLayout(){
        mMedia_controller.setVisibility(View.VISIBLE);
        SHOW = true;
    }
    public void hideMediacontrollerLayout(){
        mMedia_controller.setVisibility(View.GONE);
        SHOW = false;
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0);
            setBattery(level);
        }
    }

    /**
     * 改变电量图片
     *
     * @param level 电量
     */
    private void setBattery(int level) {
        if (level <= 0) {
            ivBattery.setImageResource(R.drawable.ic_battery_0);
        } else if (level > 0 && level <= 20) {
            ivBattery.setImageResource(R.drawable.ic_battery_10);
        } else if (level > 20 && level <= 40) {
            ivBattery.setImageResource(R.drawable.ic_battery_20);
        } else if (level > 40 && level <= 60) {
            ivBattery.setImageResource(R.drawable.ic_battery_40);
        } else if (level > 60 && level <= 80) {
            ivBattery.setImageResource(R.drawable.ic_battery_60);
        } else if (level > 80 && level < 100) {
            ivBattery.setImageResource(R.drawable.ic_battery_80);
        } else if (level == 100) {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        }
    }

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-07-19 13:48:09 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        mVideoView = (Myvideoview) findViewById(R.id.video_view);
        videoTop = (LinearLayout) findViewById(R.id.video_top);
        videoName = (TextView) findViewById(R.id.video_name);
        ivBattery = (ImageView) findViewById(R.id.iv_battery);
        ivSystemTime = (TextView) findViewById(R.id.iv_system_time);
        btnVoice = (Button) findViewById(R.id.btn_voice);
        seekbarVoice = (SeekBar) findViewById(R.id.seekbar_voice);
        btnSwitchPlayer = (Button) findViewById(R.id.btn_switch_player);
        videoBottom = (LinearLayout) findViewById(R.id.video_bottom);
        tvCurrentTime = (TextView) findViewById(R.id.tv_current_time);
        seekbarVideo = (SeekBar) findViewById(R.id.seekbar_video);
        tvDuration = (TextView) findViewById(R.id.tv_duration);
        btnExit = (Button) findViewById(R.id.btn_exit);
        btnVideoPre = (Button) findViewById(R.id.btn_video_pre);
        btnVideoStartPause = (Button) findViewById(R.id.btn_video_start_pause);
        btnVideoNext = (Button) findViewById(R.id.btn_video_next);
        btnSwitchVideoScreen = (Button) findViewById(R.id.btn_switch_video_screen);
        mVideo_bofangqi = (RelativeLayout) findViewById(R.id.video_bofangqi);
        mMedia_controller = (RelativeLayout) findViewById(R.id.media_controller_layout);

        btnVoice.setOnClickListener(this);
        btnSwitchPlayer.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        btnVideoPre.setOnClickListener(this);
        btnVideoStartPause.setOnClickListener(this);
        btnVideoNext.setOnClickListener(this);
        btnSwitchVideoScreen.setOnClickListener(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2018-07-19 13:48:09 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {

        mHandler.removeMessages(HIDE_MESSAGE);
        mHandler.sendEmptyMessageDelayed(HIDE_MESSAGE,4000);

        if (v == btnVoice) {
            IS_VOICE = !IS_VOICE;
            updatevoice(currentVoice,IS_VOICE);
            // Handle clicks for btnVoice
        } else if (v == btnSwitchPlayer) {
            // Handle clicks for btnSwitchPlayer
        } else if (v == btnExit) {
            finish();
            // Handle clicks for btnExit
        } else if (v == btnVideoPre) {
            playpre();
            // Handle clicks for btnVideoPre
        } else if (v == btnVideoStartPause) {
            startandpause();
            // Handle clicks for btnVideoStartPause
        } else if (v == btnVideoNext) {
            playnext();
            // Handle clicks for btnVideoNext
        } else if (v == btnSwitchVideoScreen) {
            SwitchScreen();
            // Handle clicks for btnSwitchVideoScreen
        }



    }

    private void startandpause() {
        if (mVideoView.isPlaying()) {
            /**
             *如果播放器正在播放，暂停播放，更换背景按钮
             */
            mVideoView.pause();
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_start_selector);
        } else {
            /**
             *如果播放器暂停播放，继续播放，更换背景按钮
             */
            mVideoView.start();
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_pause_selector);
        }
    }

    private void playpre() {
        if (mMediaItems != null && mMediaItems.size() > 0) {
            position--;
            if (position >= 0) {
                MediaItem mediaItem = mMediaItems.get(position);
                videoName.setText(mediaItem.getName());
                mVideoView.setVideoPath(mediaItem.getData());

                //设置按钮状态
                setButton();
            }
        } else if (mUri != null) {
            //设置状态按钮，此时只有一个视频
            setButton();
        }
    }

    /**
     * 播放下一个
     */
    private void playnext() {
        if (mMediaItems != null && mMediaItems.size() > 0) {
            position++;
            if (position < mMediaItems.size()) {
                MediaItem mediaItem = mMediaItems.get(position);
                videoName.setText(mediaItem.getName());
                mVideoView.setVideoPath(mediaItem.getData());

                //设置按钮状态
                setButton();
            }
        } else if (mUri != null) {
            //设置状态按钮，此时只有一个视频
            setButton();
        }
    }

    private void setButton() {
        if (mMediaItems.size() > 0 && mMediaItems != null) {
            if (mMediaItems.size() == 1) {
                btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                btnVideoPre.setEnabled(false);
                btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                btnVideoNext.setEnabled(false);
            } else if (mMediaItems.size() == 2) {
                if (position == 0) {
                    btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                    btnVideoPre.setEnabled(false);
                    btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
                    btnVideoNext.setEnabled(true);
                } else if (position == mMediaItems.size() - 1) {
                    btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                    btnVideoNext.setEnabled(false);
                    btnVideoPre.setBackgroundResource(R.drawable.btn_pre_selector);
                    btnVideoPre.setEnabled(true);
                }
            } else {
                if (position == 0) {
                    btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                    btnVideoPre.setEnabled(false);
                } else if (position == mMediaItems.size() - 1) {
                    btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                    btnVideoNext.setEnabled(false);
                } else {
                    btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
                    btnVideoNext.setEnabled(true);
                    btnVideoPre.setBackgroundResource(R.drawable.btn_pre_selector);
                    btnVideoPre.setEnabled(true);
                }

            }
        } else if (mUri != null) {
            btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
            btnVideoPre.setEnabled(false);
            btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
            btnVideoNext.setEnabled(false);
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PROGRESS:
                    //得到当前视频播放进程
                    int currentPosition = mVideoView.getCurrentPosition();
                    //当前进度
                    seekbarVideo.setProgress(currentPosition);
                    //更新时间播放进度
                    tvCurrentTime.setText(mTimeUtils.stringForTime(currentPosition));
                    //设置系统间
                    ivSystemTime.setText(getSystemtime());
                    // 每秒更新一次
                    mHandler.removeMessages(PROGRESS);
                    mHandler.sendEmptyMessageDelayed(PROGRESS, 1000);
                    break;
                case HIDE_MESSAGE:
                    hideMediacontrollerLayout();
                    break;
            }
        }
    };

    /**
     * 得到系统给时间
     *
     * @return
     */
    private String getSystemtime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }


    private class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {
        //当底层解码准备好的时候 开始播放
        @Override
        public void onPrepared(MediaPlayer mp) {
            videowidth = mp.getVideoWidth();
            videoheight = mp.getVideoHeight();
            mVideoView.start();
            //得到视频总时长
            int duration = mVideoView.getDuration();
            seekbarVideo.setMax(duration);
            mHandler.sendEmptyMessage(PROGRESS);
            //视频总时长
            tvDuration.setText("/" + mTimeUtils.stringForTime(duration));
//            hideMediacontrollerLayout();//解决切换视频切换一个就会隐藏控制面板的小bug

            setVideoSize(DEFALUT_SCREEN);

            //mVideoView.videoSize(mp.getVideoWidth(),mp.getVideoHeight());//获取视频默认长宽
        }
    }

    private class MyOnErrorListener implements MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Toast.makeText(SystemVideoPlayer.this, "播放失败", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            playnext();
            //Toast.makeText(SystemVideoPlayer.this, "播放完成", Toast.LENGTH_LONG).show();
        }
    }

    private class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        /**
         * 手指滑动，引起seekbar进度变化，回调
         *
         * @param seekBar
         * @param progress
         * @param fromUser 用户引起 为 true 不是用户引起 false
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                //实现视频拖动
                mVideoView.seekTo(progress);
            }
        }

        /**
         * 手指触碰的时候回调
         *
         * @param seekBar
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mHandler.removeMessages(HIDE_MESSAGE);
        }

        /**
         * 手指离开的时候回调
         *
         * @param seekBar
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mHandler.sendEmptyMessageDelayed(HIDE_MESSAGE,4000);
        }
    }

    private class MyOnSeekBarChangeListener2 implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser){
                updatevoice(progress,false);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    private void updatevoice(int progress,boolean IS_VOICE) {

//        if (progress>0){
//            IS_VOICE=false;
//        }else {
//            IS_VOICE=true;
//        }
        if (IS_VOICE){
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,0,0);
            seekbarVoice.setProgress(0);
        }else {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);
            seekbarVoice.setProgress(progress);
            currentVoice = progress;
        }


    }

    /**
     * 绑定手机音量键
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_VOLUME_DOWN){
            currentVoice--;
            updatevoice(currentVoice,false);
            mHandler.removeMessages(HIDE_MESSAGE);
            mHandler.sendEmptyMessageDelayed(HIDE_MESSAGE,4000);
            return true;
        }else if (keyCode==KeyEvent.KEYCODE_VOLUME_UP){
            currentVoice++;
            updatevoice(currentVoice,false);
            mHandler.removeMessages(HIDE_MESSAGE);
            mHandler.sendEmptyMessageDelayed(HIDE_MESSAGE,4000);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
