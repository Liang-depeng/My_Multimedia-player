package ldp.example.com.mymultimediaplayer.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import ldp.example.com.mymultimediaplayer.R;

/**
 * created by ldp at 2018/7/16
 *
 * 自定义标题栏
 */
public class Titlebar extends LinearLayout implements View.OnClickListener {
    private View search;
    private Context content;

    /**
     * 在代码中实例化该类使用该方法
     * @param context
     */
    public Titlebar(Context context) {
        this(context,null);
    }

    /**
     * 在布局文件中使用该类的时候，android系统调用这个构造方法实例化该类
     * @param context
     * @param attrs
     */
    public Titlebar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    /**
     * 当需要设置样式的时候
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public Titlebar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.content = context;
    }

    /**
     * 当布局文件加载完成回调
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //得到孩子实例
        search = getChildAt(1);

        //设置点击事件
        search.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.search: //搜索
                Toast.makeText(content,"正在努力开发中...",Toast.LENGTH_LONG).show();
                break;
        }
    }
}
