package ldp.example.com.mymultimediaplayer.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ldp.example.com.mymultimediaplayer.domain.Lyric;

/**
 * created by ldp at 2018/7/31
 */
public class Lyricutils {

    private ArrayList<Lyric> lyrics;

    private boolean isLyric =false;

    public ArrayList<Lyric> getLyrics() {
        return lyrics;
    }


    public boolean isLyric() {
        return isLyric;
    }

    /**
     * 读取歌词文件
     *
     * @param file
     */



    public void readLyricFile(File file) {
        if (file == null || !file.exists()) {
            //歌词文件不存在
            lyrics = null;
            isLyric = false;
        } else {
            //文件存在
            lyrics = new ArrayList<>();
            isLyric = true;
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    line = parseLyric(line);
                }
                reader.close();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Collections.sort(lyrics, new Comparator<Lyric>() {
                @Override
                public int compare(Lyric o1, Lyric o2) {
                    if (o1.getTimepoint()<o2.getTimepoint()){
                        return -1;
                    }else if (o1.getTimepoint()>o2.getTimepoint()){
                        return 1;
                    }else {
                        return 0;
                    }

                }
            });

            //每句歌词高亮时间
            for (int i=0;i<lyrics.size();i++){
                Lyric oneLyric = lyrics.get(i);
                if (i+1<lyrics.size()){
                    Lyric twoLyric = lyrics.get(i+1);
                    oneLyric.setSleepHeightLightTime(twoLyric.getTimepoint()-oneLyric.getTimepoint());
                }
            }

        }
    }



    /**
     * 解析一句歌词
     * @param line
     * @return
     */
    private String parseLyric(String line) {
        //  [  第一次出现的位置
        int pos1 = line.indexOf("[");    //   0 / -1  没有返回 -1
        int pos2 = line.indexOf("]");    //        没有返回 -1

        if (pos1 == 0 && pos2 != -1) {   //  一句歌词
            long[] times = new long[getcountTag(line)];

            String stringTime = line.substring(pos1+1,pos2);
            times[0] = StringTimeToLongTime(stringTime);

            String content = line;

            int i =1;
            while (pos1==0&&pos2!=-1){
             content = content.substring(pos2+1);
             pos1 = content.indexOf("[");
             pos2 = content.indexOf("]");

             if (pos2!=-1){
                 stringTime = content.substring(pos1+1,pos2);
                 times[i] =StringTimeToLongTime(stringTime);
                 if (times[i] == -1){
                     return "";
                 }
                 i++;
             }

            }
            Lyric lyric = new Lyric();
            for (int j = 0; j <times.length ; j++) {
                if (times[j]!=0){
                    lyric.setContent(content);
                    lyric.setTimepoint(times[j]);

                    lyrics.add(lyric);
                    lyric = new Lyric();

                }
            }
            return content;

        }
        return "";
    }

    /**
     *
     * @param stringTime 02:04.12
     * @return
     */
    private long StringTimeToLongTime(String stringTime) {
        long result = -1;
        try{
            //切割
            String[] s1 = stringTime.split(":");
            String[] s2 = s1[1].split("\\.");

            //分
            long minute = Long.parseLong(s1[0]);

            long second = Long.parseLong(s2[0]);

            long mil = Long.parseLong(s2[1]);

            result = minute * 60 * 1000 + second * 1000 + mil * 10;
        }catch (Exception e){
            e.printStackTrace();
            result = -1;
        }

        return result;
    }

    private int getcountTag(String line) {
        int result = -1;
        String[] left = line.split("\\[");
        String[] right = line.split("\\]");
        if (left.length==0&&right.length==0){
            result= 1;
        }else if(left.length>right.length){
            result=left.length;
        }else{
            result=right.length;
        }
        return result;
    }
//
//    /**
//     * 判断文件编码
//     * @param file 文件
//     * @return 编码：GBK,UTF-8,UTF-16LE
//     */
//    public String getCharset(File file) {
//        String charset = "GBK";
//        byte[] first3Bytes = new byte[3];
//        try {
//            boolean checked = false;
//            BufferedInputStream bis = new BufferedInputStream(
//                    new FileInputStream(file));
//            bis.mark(0);
//            int read = bis.read(first3Bytes, 0, 3);
//            if (read == -1)
//                return charset;
//            if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
//                charset = "UTF-16LE";
//                checked = true;
//            } else if (first3Bytes[0] == (byte) 0xFE
//                    && first3Bytes[1] == (byte) 0xFF) {
//                charset = "UTF-16BE";
//                checked = true;
//            } else if (first3Bytes[0] == (byte) 0xEF
//                    && first3Bytes[1] == (byte) 0xBB
//                    && first3Bytes[2] == (byte) 0xBF) {
//                charset = "UTF-8";
//                checked = true;
//            }
//            bis.reset();
//            if (!checked) {
//                int loc = 0;
//                while ((read = bis.read()) != -1) {
//                    loc++;
//                    if (read >= 0xF0)
//                        break;
//                    if (0x80 <= read && read <= 0xBF)
//                        break;
//                    if (0xC0 <= read && read <= 0xDF) {
//                        read = bis.read();
//                        if (0x80 <= read && read <= 0xBF)
//                            continue;
//                        else
//                            break;
//                    } else if (0xE0 <= read && read <= 0xEF) {
//                        read = bis.read();
//                        if (0x80 <= read && read <= 0xBF) {
//                            read = bis.read();
//                            if (0x80 <= read && read <= 0xBF) {
//                                charset = "UTF-8";
//                                break;
//                            } else
//                                break;
//                        } else
//                            break;
//                    }
//                }
//            }
//            bis.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return charset;
//    }
}
