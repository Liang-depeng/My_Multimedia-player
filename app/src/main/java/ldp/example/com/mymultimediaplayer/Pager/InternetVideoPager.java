package ldp.example.com.mymultimediaplayer.Pager;

import android.content.Context;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

import ldp.example.com.mymultimediaplayer.R;
import ldp.example.com.mymultimediaplayer.adapter.InternetVideoPagerAdapter;
import ldp.example.com.mymultimediaplayer.base.BasePager;
import ldp.example.com.mymultimediaplayer.domain.Internet_MediaItem;
import ldp.example.com.mymultimediaplayer.utils.Constants;
import ldp.example.com.mymultimediaplayer.utils.LogUtil;

/**
 * created by ldp at 2018/7/14
 */
public class InternetVideoPager extends BasePager {

    @ViewInject(R.id.internet_video_lists)
    private ListView mListView;

    @ViewInject(R.id.internet_no_video)
    private TextView mTextView;

    @ViewInject(R.id.internet_video_progressbar)
    private ProgressBar mProgressBar;

    private ArrayList<Internet_MediaItem> mInternet_mediaItems;
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
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("网络视频页面data初始化");

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
        mInternet_mediaItems = parseJson(json);

        //适配器
        if (mInternet_mediaItems!=null&&mInternet_mediaItems.size()>0){
            mInternetVideoPagerAdapter = new InternetVideoPagerAdapter(context, mInternet_mediaItems);
            mListView.setAdapter(mInternetVideoPagerAdapter);

            //隐藏文本
            mTextView.setVisibility(View.GONE);
        }else {

            mTextView.setVisibility(View.INVISIBLE);
        }

        mProgressBar.setVisibility(View.GONE);
    }

    private ArrayList<Internet_MediaItem> parseJson(String json) {

        ArrayList<Internet_MediaItem> internet_mediaItems = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(json);
            //opt 没有key 也可行
            JSONArray jsonArray = jsonObject.optJSONArray("trailers");
            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObjectitem = (JSONObject) jsonArray.get(i);

                    if (jsonObjectitem != null) {

                        Internet_MediaItem internet_mediaItem = new Internet_MediaItem();

                        String movieName = jsonObjectitem.optString("movieName");
                        internet_mediaItem.setName(movieName);

                        String videoTitle = jsonObjectitem.optString("videoTitle");
                        internet_mediaItem.setVideoTitle(videoTitle);

                        String imagineUrl = jsonObjectitem.optString("coverImg");
                        internet_mediaItem.setImagineUrl(imagineUrl);

                        String heigthUrl = jsonObjectitem.optString("hightUrl");
                        internet_mediaItem.setHeigthUrl(heigthUrl);

                        internet_mediaItems.add(internet_mediaItem);
                    }

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return internet_mediaItems;
    }
}
