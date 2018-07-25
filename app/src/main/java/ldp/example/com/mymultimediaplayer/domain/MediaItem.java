package ldp.example.com.mymultimediaplayer.domain;

import java.io.Serializable;

/**
 * created by ldp at 2018/7/16
 */
public class MediaItem implements Serializable{//序列化
    private String name;

    private long duration;

    private long size;

    private String data;

    private  String artist;

    private String mImagineUrl;

    private String mHeigthUrl;

    private String videoTitle;


    @Override
    public String toString() {
        return "MediaItem{" +
                "name='" + name + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                ", data='" + data + '\'' +
                ", artist='" + artist + '\'' +
                ", mImagineUrl='" + mImagineUrl + '\'' +
                ", mHeigthUrl='" + mHeigthUrl + '\'' +
                ", videoTitle='" + videoTitle + '\'' +
                '}';
    }

    public String getImagineUrl() {
        return mImagineUrl;
    }

    public void setImagineUrl(String imagineUrl) {
        mImagineUrl = imagineUrl;
    }

    public String getHeigthUrl() {
        return mHeigthUrl;
    }

    public void setHeigthUrl(String heigthUrl) {
        mHeigthUrl = heigthUrl;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}
