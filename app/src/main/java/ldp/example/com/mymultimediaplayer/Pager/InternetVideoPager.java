package ldp.example.com.mymultimediaplayer.Pager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ldp.example.com.mymultimediaplayer.R;
import ldp.example.com.mymultimediaplayer.activity.SystemVideoPlayer;
import ldp.example.com.mymultimediaplayer.adapter.InternetVideoPagerAdapter;
import ldp.example.com.mymultimediaplayer.base.BasePager;
import ldp.example.com.mymultimediaplayer.domain.MediaItem;
import ldp.example.com.mymultimediaplayer.me.maxwin.view.XListView;
import ldp.example.com.mymultimediaplayer.utils.Constants;
import ldp.example.com.mymultimediaplayer.utils.LogUtil;

/**
 * created by ldp at 2018/7/14
 */
public class InternetVideoPager extends BasePager {

    @ViewInject(R.id.internet_video_lists)
    private XListView mListView;

    @ViewInject(R.id.internet_no_video)
    private TextView mTextView;

    @ViewInject(R.id.internet_video_progressbar)
    private ProgressBar mProgressBar;

    private ArrayList<MediaItem> mMediaItems;

    private InternetVideoPagerAdapter mInternetVideoPagerAdapter;

    /**
     * 构造方法
     *
     * @param context 上下文
     */
    public InternetVideoPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.internet_video_pager, null);
        /**
         * 第一个参数是：InternetVideoPager.this
         * 第二个参数是：布局
         */
        x.view().inject(this, view);

        mListView.setOnItemClickListener(new MysetOnItemClickListener());

        //下拉刷新
        mListView.setPullLoadEnable(true);
        mListView.setXListViewListener(new MyXListViewListener());
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("网络视频页面data初始化");
        getDataFromInternet();
    }

    private void getDataFromInternet() {
        RequestParams params = new RequestParams(Constants.INTERNET_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("联网 OJBK " + result);
                progressData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("联网 NO OK " + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("onCancelled " + cex.getMessage());
            }

            @Override
            public void onFinished() {
                LogUtil.e("onFinished");
            }
        });
    }

    private void progressData(String json) {

        mMediaItems = parseJson(json);

        //适配器
        if (mMediaItems != null && mMediaItems.size() > 0) {
            mInternetVideoPagerAdapter = new InternetVideoPagerAdapter(context, mMediaItems);
            mListView.setAdapter(mInternetVideoPagerAdapter);

            onLoad();
            //隐藏文本
            mTextView.setVisibility(View.GONE);
        } else {

            mTextView.setVisibility(View.INVISIBLE);
        }

        mProgressBar.setVisibility(View.GONE);
    }


    private ArrayList<MediaItem> parseJson(String json) {

        ArrayList<MediaItem> internet_mediaItems = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(json);
            //opt 没有key 也可行
            JSONArray jsonArray = jsonObject.optJSONArray("trailers");
            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObjectitem = (JSONObject) jsonArray.get(i);

                    if (jsonObjectitem != null) {

                        MediaItem internet_mediaItem = new MediaItem();

                        String movieName = jsonObjectitem.optString("movieName");
                        internet_mediaItem.setName(movieName);

                        String videoTitle = jsonObjectitem.optString("videoTitle");
                        internet_mediaItem.setVideoTitle(videoTitle);

                        String imagineUrl = jsonObjectitem.optString("coverImg");
                        internet_mediaItem.setImagineUrl(imagineUrl);

                        String heigthUrl = jsonObjectitem.optString("hightUrl");
                        internet_mediaItem.setData(heigthUrl);

                        internet_mediaItems.add(internet_mediaItem);
                    }

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return internet_mediaItems;
    }

    class MysetOnItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


            Intent intent = new Intent(context, SystemVideoPlayer.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("local_video_list",mMediaItems);
            intent.putExtras(bundle);
            intent.putExtra("position",position);
            /**
             * content上下文一定要写，否则会出现空指针异常
             */
            context.startActivity(intent);
        }
    }

    /**
     * 下拉刷新，上拉加载更多...
     */
    private class MyXListViewListener implements XListView.IXListViewListener {
        @Override
        public void onRefresh() {
            getDataFromInternet();

        }

        @Override
        public void onLoadMore() {
            Toast.makeText(context,"暂无更多数据",Toast.LENGTH_LONG).show();
            onLoad();
        }
    }
    private void onLoad(){
        mListView.stopRefresh();
        mListView.stopLoadMore();
        mListView.setRefreshTime("更新时间："+getSystemtime());
    }
    private String getSystemtime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }

}
