package ldp.example.com.mymultimediaplayer.domain;

import android.graphics.Bitmap;

/**
 * created by ldp at 2018/7/25
 */
public class MusicItem {

    private String title;

    private String name;

    private long duration;

    private long musicsize;

    private String data_music;

    private String singerName;

    private String album;

    private int album_id;

    private Bitmap music_pic;


    @Override
    public String toString() {
        return "MusicItem{" +
                "name='" + name + '\'' +
                ", duration=" + duration +
                ", musicsize=" + musicsize +
                ", data_music='" + data_music + '\'' +
                ", singerName='" + singerName + '\'' +
                ", album='" + album + '\'' +
                ", album_id=" + album_id +
                '}';
    }

    public Bitmap getMusic_pic() {
        return music_pic;
    }

    public void setMusic_pic(Bitmap music_pic) {
        this.music_pic = music_pic;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(int album_id) {
        this.album_id = album_id;
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

    public long getMusicsize() {
        return musicsize;
    }

    public void setMusicsize(long musicsize) {
        this.musicsize = musicsize;
    }

    public String getData_music() {
        return data_music;
    }

    public void setData_music(String data_music) {
        this.data_music = data_music;
    }

    public String getSingerName() {
        return singerName;
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
