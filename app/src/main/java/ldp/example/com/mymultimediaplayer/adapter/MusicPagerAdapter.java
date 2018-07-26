package ldp.example.com.mymultimediaplayer.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ldp.example.com.mymultimediaplayer.R;
import ldp.example.com.mymultimediaplayer.domain.MusicItem;

/**
 * created by ldp at 2018/7/25
 */
public class MusicPagerAdapter extends BaseAdapter {

    private ArrayList<MusicItem> mMusicItems;
    private Context mContext;


    public MusicPagerAdapter(Context context, ArrayList<MusicItem> musicItems) {

        this.mContext = context;
        this.mMusicItems = musicItems;

    }

    @Override
    public int getCount() {
        return mMusicItems == null ? 0 : mMusicItems.size();
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
        ViewHolder viewHolder = new ViewHolder();
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_music_pager, null);

            viewHolder.ic_video_pic = (ImageView) convertView.findViewById(R.id.ic_music_pic);
            viewHolder.local_music_name = (TextView) convertView.findViewById(R.id.local_music_name);
            viewHolder.local_video_singer = (TextView) convertView.findViewById(R.id.local_video_singer);
            viewHolder.local_music_size=(TextView)convertView.findViewById(R.id.local_music_size);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.local_music_name.setText(mMusicItems.get(position).getName());
        viewHolder.local_video_singer.setText(mMusicItems.get(position).getSingerName());
        viewHolder.ic_video_pic.setImageBitmap(mMusicItems.get(position).getMusic_pic());
        viewHolder.local_music_size.setText(android.text.format.Formatter.formatFileSize(mContext,mMusicItems.get(position).getMusicsize()));
        return convertView;
    }


    public static class ViewHolder {
        private ImageView ic_video_pic;
        private TextView local_music_name;
        private TextView local_video_singer;
        private TextView local_music_size;

    }
}
