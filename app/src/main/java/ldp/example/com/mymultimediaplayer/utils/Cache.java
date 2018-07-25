package ldp.example.com.mymultimediaplayer.utils;

import android.content.Context;
import android.content.SharedPreferences;

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
}
