package ldp.example.com.mymultimediaplayer.Pager;

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
public class MusicPager extends BasePager{
    private TextView mTextView;
    /**
     * 构造方法
     *
     * @param context 上下文
     */
    public MusicPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        LogUtil.e("本地音乐页面初始化");
        mTextView = new TextView(context);
        mTextView.setTextSize(30);
        mTextView.setGravity(Gravity.CENTER);
        mTextView.setTextColor(Color.BLUE);
        return mTextView;
    }

    @Override
    public void initData() {
        super.initData();
        mTextView.setText("本地音乐");
        LogUtil.e("本地音乐页面data初始化");
    }
}
