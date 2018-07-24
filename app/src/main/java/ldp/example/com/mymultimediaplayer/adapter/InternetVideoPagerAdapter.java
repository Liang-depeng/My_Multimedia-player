package ldp.example.com.mymultimediaplayer.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.xutils.x;

import java.util.ArrayList;

import ldp.example.com.mymultimediaplayer.R;
import ldp.example.com.mymultimediaplayer.domain.Internet_MediaItem;

/**
 * created by ldp at 2018/7/24
 *
 * InternetVideoPagerAdapter
 */
public class InternetVideoPagerAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Internet_MediaItem> internet_mediaItems;

    public InternetVideoPagerAdapter(Context context, ArrayList<Internet_MediaItem> list) {
        this.context = context;
        this.internet_mediaItems = list;
    }

    @Override
    public int getCount() {
        return internet_mediaItems == null ? 0 : internet_mediaItems.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_internet_videopager, null);
            viewHolder = new ViewHolder();
            viewHolder.internet_video_pic = (ImageView) convertView.findViewById(R.id.internet_video_pic);
            viewHolder.internet_video_title = (TextView) convertView.findViewById(R.id.internet_video_title);
            viewHolder.internet_video_report = (TextView) convertView.findViewById(R.id.internet_video_report);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }


        viewHolder.internet_video_title.setText(internet_mediaItems.get(position).getName());
        viewHolder.internet_video_report.setText(internet_mediaItems.get(position).getVideoTitle());
        x.image().bind(viewHolder.internet_video_pic,internet_mediaItems.get(position).getImagineUrl());
        return convertView;
    }

    public static class ViewHolder {
        private ImageView internet_video_pic;
        private TextView internet_video_title;
        private TextView internet_video_report;
    }
}
