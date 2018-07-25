package ldp.example.com.mymultimediaplayer.domain;

/**
 * created by ldp at 2018/7/25
 */
public class MusicItem {

    private String name;

    private long duration;

    private long musicsize;

    private String data_music;

    private String singerName;

    @Override
    public String toString() {
        return "MusicItem{" +
                "name='" + name + '\'' +
                ", duration=" + duration +
                ", musicsize=" + musicsize +
                ", data_music='" + data_music + '\'' +
                ", singerName='" + singerName + '\'' +
                '}';
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
}
