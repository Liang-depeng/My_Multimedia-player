package ldp.example.com.mymultimediaplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;


import ldp.example.com.mymultimediaplayer.activity.MainActivity;


public class WelcomeActivity extends Activity {

    private static final String TAG = WelcomeActivity.class.getSimpleName();
    private Handler mHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.welcome_layout);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //2.5s后执行启动下一activity
                StartMainActivity();
                Log.e(TAG,"当前线程名称=" + Thread.currentThread().getName());
            }
        },2500);
    }

    private void StartMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * @param event 触屏
     * @return    触屏事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e(TAG,"触屏事件=" + event.getAction());
        StartMainActivity();
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
