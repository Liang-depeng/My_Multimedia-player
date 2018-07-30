package ldp.example.com.mymultimediaplayer.utils;

import android.content.Context;
import android.content.SharedPreferences;

import ldp.example.com.mymultimediaplayer.service.MymusicPlayerService;

/**
 * created by ldp at 2018/7/25
 */
public class Cache {

    public static void putString(Context context,String key,String values){
        SharedPreferences sharedPreferences = context.getSharedPreferences("ldp",Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(key,values).commit();
    }
    public static String getString(Context context,String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences("ldp",Context.MODE_PRIVATE);
        return sharedPreferences.getString(key,"");
    }

    public static void putInt(Context context,String key,int values){
        SharedPreferences sharedPreferences = context.getSharedPreferences("ldp",Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(key,values).commit();
    }
    public static int getInt(Context context,String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences("ldp",Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, MymusicPlayerService.PLAY_NORMAL);
    }
}
