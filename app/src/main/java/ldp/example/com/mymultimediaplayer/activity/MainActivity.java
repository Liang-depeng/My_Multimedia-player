package ldp.example.com.mymultimediaplayer.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import ldp.example.com.mymultimediaplayer.R;

public class MainActivity extends Activity {

    private FrameLayout flame_content;
    private RadioGroup rg_tab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        flame_content = (FrameLayout) findViewById(R.id.flame_content);
        rg_tab = (RadioGroup) findViewById(R.id.rg_tab);

        rg_tab.check(R.id.rb_local_music);
    }
}
