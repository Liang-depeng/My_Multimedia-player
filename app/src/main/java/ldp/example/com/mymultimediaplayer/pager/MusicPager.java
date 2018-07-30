package ldp.example.com.mymultimediaplayer.pager;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import ldp.example.com.mymultimediaplayer.R;
import ldp.example.com.mymultimediaplayer.activity.LocalMusicPlayerActivity;
import ldp.example.com.mymultimediaplayer.adapter.MusicPagerAdapter;
import ldp.example.com.mymultimediaplayer.base.BasePager;
import ldp.example.com.mymultimediaplayer.domain.MusicItem;
import ldp.example.com.mymultimediaplayer.utils.LogUtil;

/**
 * created by ldp at 2018/7/14
 */
public class MusicPager extends BasePager {
    private TextView mTextView;
    private TextView mLocal_no_music;
    private ListView mLocal_music_list;
    private ProgressBar mLocal_music_progressBar;

    private ArrayList<MusicItem> mMusicItems;
    private MusicPagerAdapter mMusicPagerAdapter;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mMusicItems != null && mMusicItems.size() > 0) {
                //有数据绑定适配器 隐藏文本
                mMusicPagerAdapter = new MusicPagerAdapter(context, mMusicItems);
                mLocal_music_list.setAdapter(mMusicPagerAdapter);

                mLocal_no_music.setVisibility(View.GONE);
            } else {
                //无数据 显示文本
                mLocal_no_music.setVisibility(View.VISIBLE);
            }
            //隐藏  progressBar
            mLocal_music_progressBar.setVisibility(View.GONE);
        }
    };

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

        View view = View.inflate(context, R.layout.music_player, null);
        mLocal_music_list = (ListView) view.findViewById(R.id.local_music_list);
        mLocal_no_music = (TextView) view.findViewById(R.id.local_no_music);
        mLocal_music_progressBar = (ProgressBar) view.findViewById(R.id.local_music_progressbar);

        mLocal_music_list.setOnItemClickListener(new MyMusicOnItemClickListener());
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("本地音乐页面data初始化");
        isGrantExternalRW((Activity) context);
        getMusicDataFromLocal();
    }

    private void getMusicDataFromLocal() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                SystemClock.sleep(1000);
                mMusicItems = new ArrayList<>();

                ContentResolver resolver = context.getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

                String objs[] = {
                        //视频在sd卡名称
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        //视频总时长
                        MediaStore.Audio.Media.DURATION,
                        //视频大小
                        MediaStore.Audio.Media.SIZE,
                        //视频绝对地址
                        MediaStore.Audio.Media.DATA,
                        //艺术家名称
                        MediaStore.Audio.Media.ARTIST,
                        //唱片
                        MediaStore.Audio.Media.ALBUM,
                        //唱片ID
                        MediaStore.Audio.Media.ALBUM_ID
                };

                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        MusicItem musicItem = new MusicItem();

                        mMusicItems.add(musicItem);

                        String name = cursor.getString(0);
                        musicItem.setName(name);

                        long duration = cursor.getLong(1);
                        musicItem.setDuration(duration);

                        long size = cursor.getLong(2);
                        musicItem.setMusicsize(size);

                        String data = cursor.getString(3);
                        musicItem.setData_music(data);

                        String artist = cursor.getString(4);
                        musicItem.setSingerName(artist);

                        String album = cursor.getString(5);
                        musicItem.setAlbum(album);

                        int abbum_id = cursor.getInt(6);
                        musicItem.setAlbum_id(abbum_id);

                        musicItem.setMusic_pic(getAlbumArt(abbum_id));
                    }
                    cursor.close();
                }
                //发消息
                mHandler.sendEmptyMessage(0);
            }

        }.start();
    }

    private class MyMusicOnItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //Toast.makeText(context, "我是第" + position + "条", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(context, LocalMusicPlayerActivity.class);
            intent.putExtra("position", position);
            context.startActivity(intent);
        }
    }

    /**
     * 根据专辑ID获取专辑封面图
     * 来源：https://blog.csdn.net/jasper_success/article/details/78832286
     *
     * @param album_id 专辑ID
     * @return
     */
    private Bitmap getAlbumArt(int album_id) {
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[]{"album_art"};
        Cursor cur = context.getContentResolver().query(Uri.parse(mUriAlbums + "/" + Integer.toString(album_id)), projection, null, null, null);
        String album_art = null;
        if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
            cur.moveToNext();
            album_art = cur.getString(0);
        }
        cur.close();
        Bitmap bm = null;
        if (album_art != null) {
            bm = BitmapFactory.decodeFile(album_art);
        } else {
            bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_test);
        }
        return bm;
    }

    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);
            return false;
        }
        return true;
    }
}
