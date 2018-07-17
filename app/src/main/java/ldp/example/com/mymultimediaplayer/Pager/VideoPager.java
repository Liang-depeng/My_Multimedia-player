package ldp.example.com.mymultimediaplayer.Pager;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ldp.example.com.mymultimediaplayer.R;
import ldp.example.com.mymultimediaplayer.base.BasePager;
import ldp.example.com.mymultimediaplayer.domain.MediaItem;
import ldp.example.com.mymultimediaplayer.utils.LogUtil;

/**
 * created by ldp at 2018/7/14
 */
public class VideoPager extends BasePager {
    private ListView local_video_list;
    private TextView local_no_video;
    private ProgressBar local_video_progressbar;
    private ArrayList<MediaItem> mediaItems_list;//数据集合
    private int ispermission = 0;


    /**
     * 构造方法
     *
     * @param context 上下文
     */
    public VideoPager(Context context) {
        super(context);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mediaItems_list != null && mediaItems_list.size() > 0) {
                //有数据   + 适配器
                //隐藏文本和progressbar
            } else {
                //无数据
                //文本隐藏
            }

            //progressbar隐藏
        }
    };

    @Override
    public View initView() {

        View view = View.inflate(context, R.layout.video_pager, null);
        local_video_list = (ListView) view.findViewById(R.id.local_video_list);
        local_no_video = (TextView) view.findViewById(R.id.local_no_video);
        local_video_progressbar = (ProgressBar) view.findViewById(R.id.local_video_progressbar);

        LogUtil.e("本地视频页面初始化");
        return view;
    }

    @Override
    public void initData() {

        super.initData();
        LogUtil.e("本地视频页面data初始化");
        //加载数据
            getDataFromlocal();

    }



    /**
     * 从本地sd卡得到数据
     * <p>
     * 从内容提供者获取视频信息（6.0需要添加动态权限）
     */
    private void getDataFromlocal() {

        new Thread() {
            @Override
            public void run() {
                super.run();
                mediaItems_list = new ArrayList<>();

                ContentResolver resolver = context.getContentResolver();
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;


                String[] objs = {
                        //视频在sd卡名称
                        MediaStore.Video.Media.DISPLAY_NAME,
                        //视频总时长
                        MediaStore.Video.Media.DURATION,
                        //视频大小
                        MediaStore.Video.Media.SIZE,
                        //视频绝对地址
                        MediaStore.Video.Media.DATA,
                        //
                        MediaStore.Video.Media.ARTIST
                };

                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        MediaItem mediaItem = new MediaItem();

                        mediaItems_list.add(mediaItem);

                        String name = cursor.getString(0);
                        mediaItem.setName(name);

                        long duration = cursor.getLong(1);
                        mediaItem.setDuration(duration);

                        long size = cursor.getLong(2);
                        mediaItem.setSize(size);

                        String data = cursor.getString(3);
                        mediaItem.setData(data);

                        String artist = cursor.getString(4);
                        mediaItem.setArtist(artist);

                    }
                    cursor.close();
                }

                //发消息 handler
                handler.sendEmptyMessage(0);
            }
        }.start();
    }


}
