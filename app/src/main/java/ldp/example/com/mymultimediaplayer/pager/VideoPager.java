package ldp.example.com.mymultimediaplayer.pager;


import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import ldp.example.com.mymultimediaplayer.R;
import ldp.example.com.mymultimediaplayer.activity.SystemVideoPlayer;
import ldp.example.com.mymultimediaplayer.adapter.VideoPagerAdapter;
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
    private ArrayList<MediaItem> mMediaItems;//数据集合
    private VideoPagerAdapter mVideoPagerAdapter;


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
            if (mMediaItems != null && mMediaItems.size() > 0) {
                //有数据   + 适配器
                mVideoPagerAdapter = new VideoPagerAdapter(context, mMediaItems);
                local_video_list.setAdapter(mVideoPagerAdapter);
                //隐藏文本和progressbar
                local_no_video.setVisibility(View.GONE);
            } else {
                //无数据
                //文本显示
                local_no_video.setVisibility(View.VISIBLE);
            }
            //progressbar隐藏
            local_video_progressbar.setVisibility(View.GONE);
        }
    };

    @Override
    public View initView() {

        View view = View.inflate(context, R.layout.video_pager, null);
        local_video_list = (ListView) view.findViewById(R.id.local_video_list);
        local_no_video = (TextView) view.findViewById(R.id.local_no_video);
        local_video_progressbar = (ProgressBar) view.findViewById(R.id.local_video_progressbar);

        /**
         * 设置点击事件
         */

        local_video_list.setOnItemClickListener(new MyOnItemClickListener());

        LogUtil.e("本地视频页面初始化");
        return view;
    }

    @Override
    public void initData() {

        super.initData();
        LogUtil.e("本地视频页面data初始化");
        //加载数据
        isGrantExternalRW((Activity)context);
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

                mMediaItems = new ArrayList<>();

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
                        //艺术家名称
                        MediaStore.Video.Media.ARTIST
                };

                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        MediaItem mediaItem = new MediaItem();

                        mMediaItems.add(mediaItem);

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

                     //   mediaItem.setFirstFramePic(getFirstFramePic(data));
                    }
                    cursor.close();
                }

                //发消息 handler
                handler.sendEmptyMessage(0);
            }
        }.start();
    }


    /**
     * 视频列表点击事件监听
     */
    public class MyOnItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MediaItem mediaItem = mMediaItems.get(position);

            //调用自己写的播放器
//            Intent intent = new Intent(context, SystemVideoPlayer.class);
//            intent.setDataAndType(Uri.parse(mediaItem.getData()), "video/*");


            Intent intent = new Intent(context, SystemVideoPlayer.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("local_video_list",mMediaItems);
            intent.putExtras(bundle);
            intent.putExtra("position",position);
            /**
             * content上下文一定要写，否则会出现空指针异常
             */
            context.startActivity(intent);

            //隐式意图
/*            Intent intent = new Intent();
            intent.setDataAndType(Uri.parse(mediaItem.getData()),"video/*");
            startActivity(intent);*/


        }
    }
    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);
            return false;
        }
        return true;
    }

//    public Bitmap getFirstFramePic(String path){
//        //创建MediaMetadataRetriever对象
//        MediaMetadataRetriever mmr=new MediaMetadataRetriever();
////        //设置资源位置
////        String path="/storage/sdcard1"+"/Movies"+"/XiaomiPhone.mp4";
//        //绑定资源
//        mmr.setDataSource(path);
//        //获取第一帧图像的bitmap对象
//        Bitmap bitmap=mmr.getFrameAtTime();
//        //加载到ImageView控件上
////        img.setImageBitmap(bitmap);
//        mmr.release();
//        return bitmap;
//    }
}
