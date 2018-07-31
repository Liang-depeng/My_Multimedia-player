package ldp.example.com.mymultimediaplayer.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import ldp.example.com.mymultimediaplayer.domain.Lyric;

/**
 * created by ldp at 2018/7/31
 */
public class Lyricutils {

    private ArrayList<Lyric> lyrics;

    /**
     * 读取歌词文件
     *
     * @param file
     */
    public void readLyricFile(File file) {
        if (file == null || !file.exists()) {
            //歌词文件不存在
            lyrics = null;
        } else {
            //文件存在
            lyrics = new ArrayList<>();
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "GBK"));
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

        }
    }



    /**
     * 解析一句歌词
     * @param line
     * @return
     */
    private String parseLyric(String line) {
        //  [  第一次出现的位置
        int pos1 = line.indexOf("[");   //  0 / -1  没有返回 -1
        int pos2 = line.indexOf("]");   //        没有返回 -1

        if (pos1 == 0 && pos2 != -1) {  // 一句歌词
        }
        return null;
    }
}
