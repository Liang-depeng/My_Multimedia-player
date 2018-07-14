package ldp.example.com.mymultimediaplayer.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import java.util.ArrayList;
import ldp.example.com.mymultimediaplayer.Pager.InternetMusicPager;
import ldp.example.com.mymultimediaplayer.Pager.InternetVideoPager;
import ldp.example.com.mymultimediaplayer.Pager.MusicPager;
import ldp.example.com.mymultimediaplayer.Pager.VideoPager;
import ldp.example.com.mymultimediaplayer.R;
import ldp.example.com.mymultimediaplayer.base.BasePager;

public class MainActivity extends FragmentActivity {

    private FrameLayout flame_content;
    private RadioGroup rg_tab;
    private static int position;

    /**
     * 页面集合
     */
    private static ArrayList<BasePager> basePagers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        flame_content = (FrameLayout) findViewById(R.id.flame_content);
        rg_tab = (RadioGroup) findViewById(R.id.rg_tab);


        /**
         * 添加页面
         */
        basePagers = new ArrayList<>();
        basePagers.add(new MusicPager(this));//0
        basePagers.add(new InternetMusicPager(this));//1
        basePagers.add(new VideoPager(this));//2
        basePagers.add(new InternetVideoPager(this));//3

        rg_tab.setOnCheckedChangeListener(new MyOnCheckedChangeListener());


        /**
         * 默认选择本地音乐
         */
        rg_tab.check(R.id.rb_local_music);
    }


    class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId){
                default:
                    position = 0;
                    break;
                case R.id.rb_internet_music:
                    position = 1;
                    break;
                case R.id.rb_local_video:
                    position = 2;
                    break;
                case R.id.rb_internet_video:
                    position = 3;
                    break;
            }
            setFragment();
        }
    }

    private void setFragment() {
        //得到Fragmentmanager
        FragmentManager manager = getSupportFragmentManager();
        //开启事务
        FragmentTransaction ft = manager.beginTransaction();
        //替换fragment
        ft.replace(R.id.flame_content,new MyFragment());
        //提交事务
        ft.commit();
    }

    /**
     * 根据位置得到页面
     * @return
     */
    private static BasePager getBasePager() {
        BasePager basePager = basePagers.get(position);
        if (basePager!=null) {
            /**
             * 联网请求或绑定数据
             */
            basePager.initData();
        }
        return basePager;
    }

    /**
     * 在Mainactivity中的替换fragment步骤时，第二个参数，不能写new Fragment(){}这样的匿名内部类，不然会报错：
     * Fragment null must be a public static class to be properly recreated from in
     * 这个错误的原因是v4包升级成25后，Fragment必须要作为一个public 的static类，才能在此使用。
     */
    public static class MyFragment extends Fragment {
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            BasePager basePager = getBasePager();
            if (basePager!=null){
                return basePager.rootview;
            }
            return null;
        }
    }
}
