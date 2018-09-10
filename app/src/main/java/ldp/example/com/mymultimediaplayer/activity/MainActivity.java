package ldp.example.com.mymultimediaplayer.activity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;

import ldp.example.com.mymultimediaplayer.pager.InternetMusicPager;
import ldp.example.com.mymultimediaplayer.pager.InternetVideoPager;
import ldp.example.com.mymultimediaplayer.pager.MusicPager;
import ldp.example.com.mymultimediaplayer.pager.VideoPager;
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
    private DrawerLayout mDrawerLayout;
    private ImageView mImageView_mine;
    private NavigationView mNv_list;
    private int ispermission = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cehua_layout);

//        /**
//         * 运行时权限
//         * 进去获取需要的权限
//         *
//         */
//        isGrantExternalRW((Activity) MainActivity.this);


        flame_content = (FrameLayout) findViewById(R.id.flame_content); //主页面布局
        rg_tab = (RadioGroup) findViewById(R.id.rg_tab);//底部
        mDrawerLayout = (DrawerLayout) findViewById(R.id.ce_hua);//侧滑菜单
        mImageView_mine = (ImageView) findViewById(R.id.imageView_mine);// 左上角导航栏
        mNv_list = (NavigationView) findViewById(R.id.nv_list);//侧滑菜单列表
        mImageView_mine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);//侧滑菜单
            }
        });

        mNv_list.setCheckedItem(R.id.nav_0);// 侧滑菜单默认选择第一个

        /**
         * 侧滑菜各个选项监听器
         *
         * 根据需要增删
         */
        mNv_list.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();
                switch (id){
                    default:
                        break;
                    case R.id.nav_0:
                        Toast.makeText(MainActivity.this,"我是第0个选项",Toast.LENGTH_LONG).show();
                        break;
                    case R.id.nav_1:
                        Toast.makeText(MainActivity.this,"我是第1个选项",Toast.LENGTH_LONG).show();
                        break;
                    case R.id.nav_2:
                        Toast.makeText(MainActivity.this,"我是第2个选项",Toast.LENGTH_LONG).show();
                        break;
                    case R.id.nav_3:
                        Toast.makeText(MainActivity.this,"我是第3个选项",Toast.LENGTH_LONG).show();
                        break;
                    case R.id.nav_4:
                        Toast.makeText(MainActivity.this,"我是第4个选项",Toast.LENGTH_LONG).show();
                        break;
                    case R.id.nav_5:
                        Toast.makeText(MainActivity.this,"我是第5个选项",Toast.LENGTH_LONG).show();
                        break;
                }
               // mDrawerLayout.closeDrawers();
                return true;
            }
        });

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


    /**
     * 页面底部4个按钮监听
     */
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


    /**
     * 获取fragment
     */
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
        if (basePager!=null&&!basePager.isInitData) {
            /**
             * 联网请求或绑定数据
             */
            basePager.initData();
            basePager.isInitData = true;
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

    /**
     * 返回键
     * @param keyCode
     * @param event
     * @return
     */
    private boolean isExit = false;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (position!=0){
                position =0;
                rg_tab.check(R.id.rb_local_music);
                return true;
            }else if (!isExit){
                isExit = true;
                Toast.makeText(MainActivity.this,"再按一次推出",Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isExit = false;
                    }
                },2000);
                return  true;
            }

        }

        return super.onKeyDown(keyCode, event);
    }

    //
//    private void requestPermission(){
//        if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                !=PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(MainActivity.this,
//                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,},1);
//        }else{
//            setFragment();
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode){
//            default:
//                break;
//            case 1:
//                if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
//                    setFragment();
//                }else{
//                    Toast.makeText(this,"读取sd卡权限被拒绝",Toast.LENGTH_LONG).show();
//                }
//                break;
//
//        }
//    }

//
//    public static boolean isGrantExternalRW(Activity activity) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
//                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
//            activity.requestPermissions(new String[]{
//                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE
//            }, 1);
//            return false;
//        }
//        return true;
//    }
}
