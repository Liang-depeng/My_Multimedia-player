package ldp.example.com.mymultimediaplayer.adapter;

import android.content.Context;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import ldp.example.com.mymultimediaplayer.Pager.VideoPager;
import ldp.example.com.mymultimediaplayer.R;
import ldp.example.com.mymultimediaplayer.domain.MediaItem;
import ldp.example.com.mymultimediaplayer.utils.TimeUtils;

/**
 * created by ldp at 2018/7/17
 * <p>
 * 本地视频页面适配器
 */
public class VideoPagerAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<MediaItem> videoPagerData_list;
    private Context mContext;
    private TimeUtils mTimeUtils;

    public VideoPagerAdapter(Context context, ArrayList<MediaItem> list) {
        this.mContext = context;
        this.videoPagerData_list = list;
        inflater = LayoutInflater.from(mContext);
        mTimeUtils = new TimeUtils();
    }

    @Override
    public int getCount() {
        return videoPagerData_list == null ? 0 : videoPagerData_list.size();
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
            convertView = View.inflate(mContext, R.layout.item_videopager, null);
            viewHolder = new ViewHolder();
            viewHolder.ic_video_pic1 = (ImageView) convertView.findViewById(R.id.ic_video_pic);
            viewHolder.local_video_name1 = (TextView) convertView.findViewById(R.id.local_video_name);
            viewHolder.local_video_time1 = convertView.findViewById(R.id.local_video_time);
            viewHolder.local_video_size1 = convertView.findViewById(R.id.local_video_size);

            convertView.setTag(viewHolder);


        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        /**
         * 得到数据
         */
        viewHolder.local_video_name1.setText(videoPagerData_list.get(position).getName());
        viewHolder.local_video_time1.setText(mTimeUtils.stringForTime((int) videoPagerData_list.get(position).getDuration()));
        viewHolder.local_video_size1.setText(android.text.format.Formatter.
                formatFileSize(mContext, videoPagerData_list.get(position).getSize()));
        return convertView;
    }

    public static class ViewHolder {
        private ImageView ic_video_pic1;
        private TextView local_video_name1;
        private TextView local_video_time1;
        private TextView local_video_size1;
    }
}
