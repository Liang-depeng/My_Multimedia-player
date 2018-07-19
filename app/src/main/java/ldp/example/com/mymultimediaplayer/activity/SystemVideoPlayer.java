package ldp.example.com.mymultimediaplayer.activity;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import ldp.example.com.mymultimediaplayer.R;
import ldp.example.com.mymultimediaplayer.utils.TimeUtils;

/**
 * created by ldp at 2018/7/18
 */
public class SystemVideoPlayer extends Activity implements View.OnClickListener {

    private static final int PROGRESS = 1;

    private VideoView mVideoView;
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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_systemvideoplayer);
        mTimeUtils = new TimeUtils();
        findViews();

        //准备好的监听
        mVideoView.setOnPreparedListener(new MyOnPreparedListener());

        //播放失败监听
        mVideoView.setOnErrorListener(new MyOnErrorListener());

        //播放完成监听
        mVideoView.setOnCompletionListener(new MyOnCompletionListener());

        //视频拖动
        seekbarVideo.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());

        //得到播放地址
        mUri = getIntent().getData();
        if (mUri != null) {
            mVideoView.setVideoURI(mUri);
        }

        //        /**
        //        * 设置控制面板
        //        * 安卓系统自带控制面板
        //        */
        // mVideoView.setMediaController(new MediaController(this));
    }

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-07-19 13:48:09 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        mVideoView = (VideoView) findViewById(R.id.video_view);
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
        if (v == btnVoice) {
            // Handle clicks for btnVoice
        } else if (v == btnSwitchPlayer) {
            // Handle clicks for btnSwitchPlayer
        } else if (v == btnExit) {
            // Handle clicks for btnExit
        } else if (v == btnVideoPre) {
            // Handle clicks for btnVideoPre
        } else if (v == btnVideoStartPause) {
            if (mVideoView.isPlaying()){
                /**
                 *如果播放器正在播放，暂停播放，更换背景按钮
                 */
                mVideoView.pause();
                btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_start_selector);
            }else{
                /**
                 *如果播放器暂停播放，继续播放，更换背景按钮
                 */
                mVideoView.start();
                btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_pause_selector);
            }
            // Handle clicks for btnVideoStartPause
        } else if (v == btnVideoNext) {
            // Handle clicks for btnVideoNext
        } else if (v == btnSwitchVideoScreen) {
            // Handle clicks for btnSwitchVideoScreen
        }
    }

    private Handler mHandler = new Handler(){
        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case PROGRESS:
                    //得到当前视频播放进程
                    int currentPosition = mVideoView.getCurrentPosition();
                    //当前进度
                    seekbarVideo.setProgress(currentPosition);

                    tvCurrentTime.setText(mTimeUtils.stringForTime(currentPosition));
                    // 每秒更新一次
                    mHandler.removeMessages(PROGRESS);
                    mHandler.sendEmptyMessageDelayed(PROGRESS,1000);
                    break;
            }
        }
    };


    private class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {
        //当底层解码准备好的时候 开始播放
        @Override
        public void onPrepared(MediaPlayer mp) {
            mVideoView.start();
            //得到视频总时长
            int duration = mVideoView.getDuration();
            seekbarVideo.setMax(duration);
            mHandler.sendEmptyMessage(PROGRESS);
            //视频总时长
            tvDuration.setText(mTimeUtils.stringForTime(duration));
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
            Toast.makeText(SystemVideoPlayer.this, "播放完成", Toast.LENGTH_LONG).show();
        }
    }

    private class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        /**
         * 手指滑动，引起seekbar进度变化，回调
         * @param seekBar
         * @param progress
         * @param fromUser 用户引起 为 true 不是用户引起 false
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser){
                //实现视频拖动
                mVideoView.seekTo(progress);
            }
        }

        /**
         * 手指触碰的时候回调
         * @param seekBar
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        /**
         * 手指离开的时候回调
         * @param seekBar
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
}
