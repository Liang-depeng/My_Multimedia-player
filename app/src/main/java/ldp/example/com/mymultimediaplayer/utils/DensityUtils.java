package ldp.example.com.mymultimediaplayer.utils;

import android.content.Context;

/**
 * created by ldp at 2018/8/1
 */
public class DensityUtils {

    /**
     * 根据手机分辨率从dip单位转换为像素（px）
     * @param context
     * @param dpValue
     * @return
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机分辨率 从像素转换为dp
     * @param context
     * @param pxValue
     * @return
     */

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
