package ldp.example.com.mymultimediaplayer.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import ldp.example.com.mymultimediaplayer.IMyMusicPlayerAidlInterface;
import ldp.example.com.mymultimediaplayer.R;
import ldp.example.com.mymultimediaplayer.service.MymusicPlayerService;

/**
 * created by ldp at 2018/7/26
 */
public class LocalMusicPlayerActivity extends Activity {

    @ViewInject(R.id.music_pic1)
    private ImageView music_pic_list01;

    private int position;
    private IMyMusicPlayerAidlInterface service;
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
        initView();
        getData();
        bindMusicService();

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
}
