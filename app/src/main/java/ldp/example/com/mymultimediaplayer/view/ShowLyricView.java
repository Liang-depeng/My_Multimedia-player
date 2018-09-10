package ldp.example.com.mymultimediaplayer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import java.util.ArrayList;

import ldp.example.com.mymultimediaplayer.domain.Lyric;
import ldp.example.com.mymultimediaplayer.utils.DensityUtils;

/**
 * created by ldp at 2018/7/31
 */
public class ShowLyricView extends android.support.v7.widget.AppCompatTextView {


    private ArrayList<Lyric> lyrics;
    private Paint paint;//高亮
    private Paint paint2;//其他歌词
    private int width, height;
    private int index;//歌词列表中的索引
    private float text_height;
    private float currentPosition;//当前播放进度
    private float mSleepTime;
    private float mTimePoint;

    public void setLyrics(ArrayList<Lyric> lyrics) {
        this.lyrics = lyrics;
    }

    public ShowLyricView(Context context) {
        this(context,null);
    }

    public ShowLyricView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ShowLyricView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {

        text_height = DensityUtils.dip2px(context,21);

        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(DensityUtils.dip2px(context,15));
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);//居中

        paint2 = new Paint();
        paint2.setColor(Color.GRAY);
        paint2.setTextSize(DensityUtils.dip2px(context,15));
        paint2.setAntiAlias(true);
        paint2.setTextAlign(Paint.Align.CENTER);//居中

//        lyrics = new ArrayList<>();
//        for (int i = 0; i < 100; i++) {
//            Lyric lyric = new Lyric();
//            lyric.setTimepoint(1000 * i);
//            lyric.setSleepHeightLightTime(1500 + i);
//            lyric.setContent(i + "121212121212" + i);
//            lyrics.add(lyric);
//        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (lyrics != null && lyrics.size() > 0) {

            //歌词往上推移
            float plush =0;
            if (mSleepTime==0){
                plush=0;
            }else {
                //平移
              //  float delta = ((currentPosition-mTimePoint)/mSleepTime)*text_height;

                plush=text_height+((currentPosition-mTimePoint)/mSleepTime)*text_height;
            }
            canvas.translate(0,-plush);



            //绘制当前歌词
            String current_content = lyrics.get(index).getContent();
            canvas.drawText(current_content, width / 2, height / 2, paint);
            //绘制前面部分
            float Y = height / 2;

            for (int i = index - 1; i >= 0; i--) {
                String pre_content = lyrics.get(i).getContent();
                Y = Y - text_height;
                if (Y < 0) {
                    break;
                }
                canvas.drawText(pre_content, width/2, Y, paint2);
            }
            //绘制后面部分
            Y = height / 2;
            for (int i = index + 1; i < lyrics.size(); i++) {
                String next_content = lyrics.get(i).getContent();
                Y = Y + text_height;
                if (Y > height) {
                    break;
                }
                canvas.drawText(next_content, width/2, Y, paint2);
            }

        } else {
            canvas.drawText("没有歌词", width / 2, height / 2, paint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    /**
     * 根据当前进度找出高亮显示的一句
     * @param currentPosition
     */
    public void setNextLyric(int currentPosition) {
        this.currentPosition = currentPosition;
        if (lyrics==null||lyrics.size()==0){
            return;
        }

        for (int i = 1; i < lyrics.size(); i++) {
            if (currentPosition<lyrics.get(i).getTimepoint()){
                int tempIndex = i-1;
                if (currentPosition>=lyrics.get(tempIndex).getTimepoint()){
                    index = tempIndex;
                    mSleepTime = lyrics.get(index).getSleepHeightLightTime();
                    mTimePoint = lyrics.get(index).getTimepoint();
                }
            }
        }

        //重新绘制
        invalidate();//主线程
    }
}
