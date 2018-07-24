package ldp.example.com.mymultimediaplayer;

import android.app.Application;

import org.xutils.x;

/**
 * created by ldp at 2018/7/24
 */
public class MyAppLication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG); // 是否输出debug日志, 开启debug会影响性能.
    }
}
