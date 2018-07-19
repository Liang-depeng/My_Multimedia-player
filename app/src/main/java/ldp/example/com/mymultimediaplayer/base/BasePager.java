package ldp.example.com.mymultimediaplayer.base;

import android.content.Context;
import android.view.View;

import ldp.example.com.mymultimediaplayer.utils.BaseActivity;


/**
 * created by ldp at 2018/7/11
 */
public abstract class BasePager{

    public  Context context;
    public View rootview;
    public boolean isInitData;
    /**
     * 构造方法
     * @param context 上下文
     */
    public BasePager(Context context){
        this.context = context;
        rootview = initView();
    }

    /**
     * 强制子类实现
     * @return
     */
    public abstract View initView();

    /**
     * 子页面初始化数据，联网请求数据或者绑定数据
     */
    public void initData(){

    }

}
