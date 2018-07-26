package ldp.example.com.mymultimediaplayer.pager;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import ldp.example.com.mymultimediaplayer.base.BasePager;
import ldp.example.com.mymultimediaplayer.utils.LogUtil;

/**
 * created by ldp at 2018/7/14
 */
public class InternetMusicPager extends BasePager{
    private TextView mTextView;
    /**
     * 构造方法
     *
     * @param context 上下文
     */
    public InternetMusicPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        LogUtil.e("网络音乐页面初始化");
        mTextView = new TextView(context);
        mTextView.setTextSize(30);
        mTextView.setGravity(Gravity.CENTER);
        mTextView.setTextColor(Color.BLUE);
        return mTextView;
    }

    @Override
    public void initData() {
        super.initData();
        mTextView.setText("网络音乐");
        LogUtil.e("网络音乐页面data初始化");
    }
}
