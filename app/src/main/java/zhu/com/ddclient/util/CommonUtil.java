package zhu.com.ddclient.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by zss on 2016/9/14.
 */
public class CommonUtil {
    public static  String getValueFromLocal(Context context ,String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences("my",Context.MODE_WORLD_READABLE);
        return sharedPreferences.getString(key,"");
    }
    public static void saveValueToLocal(Context context,String key,String value){
        SharedPreferences sharedPreferences = context.getSharedPreferences("my",Context.MODE_WORLD_READABLE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,value);
        editor.commit();
    }
}
