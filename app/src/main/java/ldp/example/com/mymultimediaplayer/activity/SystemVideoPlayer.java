package ldp.example.com.mymultimediaplayer.activity;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import ldp.example.com.mymultimediaplayer.R;

/**
 * created by ldp at 2018/7/18
 */
public class SystemVideoPlayer extends Activity{

    private VideoView mVideoView;
    private Uri mUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_systemvideoplayer);
        mVideoView = (VideoView) findViewById(R.id.video_view);


        //准备好的监听
        mVideoView.setOnPreparedListener(new MyOnPreparedListener());
        //播放失败监听
        mVideoView.setOnErrorListener(new MyOnErrorListener());
        //播放完成监听
        mVideoView.setOnCompletionListener(new MyOnCompletionListener());
        //得到播放地址
        mUri = getIntent().getData();
        if (mUri!=null) {
            mVideoView.setVideoURI(mUri);
        }

        /**
         * 设置控制面板
         * 安卓系统自带控制面板
         */
        mVideoView.setMediaController(new MediaController(this));
    }

    private class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {
        //当底层解码准备好的时候 开始播放
        @Override
        public void onPrepared(MediaPlayer mp) {
            mVideoView.start();
        }
    }

    private class MyOnErrorListener implements MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Toast.makeText(SystemVideoPlayer.this,"播放失败",Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            Toast.makeText(SystemVideoPlayer.this,"播放完成",Toast.LENGTH_LONG).show();
        }
    }
}
