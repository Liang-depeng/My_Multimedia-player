package ldp.example.com.mymultimediaplayer.activity;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import ldp.example.com.mymultimediaplayer.R;

/**
 * created by ldp at 2018/7/26
 */
public class LocalMusicPlayerActivity extends Activity{

    @ViewInject(R.id.music_pic1)
    private ImageView music_pic_list01;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localmusic_layout);

        x.view().inject(LocalMusicPlayerActivity.this);

       // music_pic_list01 = (ImageView) findViewById(R.id.music_pic1);
        music_pic_list01.setBackgroundResource(R.drawable.music_pic_list);
        AnimationDrawable animationDrawable = (AnimationDrawable)music_pic_list01.getBackground();
        animationDrawable.start();
    }
}
