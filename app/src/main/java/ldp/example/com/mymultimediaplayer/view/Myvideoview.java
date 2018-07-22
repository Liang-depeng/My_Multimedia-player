package ldp.example.com.mymultimediaplayer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.VideoView;

/**
 * created by ldp at 2018/7/21
 * 自定义 videoview
 */
public class Myvideoview extends VideoView{
    /**
     * 在代码中创建的时候使用
     * @param context
     */
    public Myvideoview(Context context) {
        this(context,null);
    }

    /**
     * 当这个类在布局文件的时候，系统通过该构造方法实例化
     * @param context
     * @param attrs
     */
    public Myvideoview(Context context, AttributeSet attrs) {
        super(context, attrs,0);
    }

    /**
     * 设置样式时调用
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public Myvideoview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 视频尺寸
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec,heightMeasureSpec);
    }

    public void videoSize(int widthMeasureSpec,int heightMeasureSpec){
        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = widthMeasureSpec;
        params.height = heightMeasureSpec;
        setLayoutParams(params);
    }
}
