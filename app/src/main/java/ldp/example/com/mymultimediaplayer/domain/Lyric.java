package ldp.example.com.mymultimediaplayer.domain;

/**
 * created by ldp at 2018/7/31
 */
public class Lyric {

    private String content;//内容

    private long timepoint;//时间戳

    private long sleepHeightLightTime;//高亮

    @Override
    public String toString() {
        return "Lyric{" +
                "content='" + content + '\'' +
                ", timepoint=" + timepoint +
                ", sleepHeightLightTime=" + sleepHeightLightTime +
                '}';
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimepoint() {
        return timepoint;
    }

    public void setTimepoint(long timepoint) {
        this.timepoint = timepoint;
    }

    public long getSleepHeightLightTime() {
        return sleepHeightLightTime;
    }

    public void setSleepHeightLightTime(long sleepHeightLightTime) {
        this.sleepHeightLightTime = sleepHeightLightTime;
    }
}
