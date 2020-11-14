package Util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

public class SharedPrefrenceUtils {

    public static void insert(Context context,HashMap<String, String>data){
        //步骤1：创建一个SharedPreferences对象
        SharedPreferences sharedPreferences= context.getSharedPreferences("data", Context.MODE_PRIVATE);
        //步骤2： 实例化SharedPreferences.Editor对象
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //步骤3：将获取过来的值放入文件
        for (Map.Entry<String, String> entry : data.entrySet()) {
           editor.putString(entry.getKey(),entry.getValue());
        }
        //步骤4：提交
        editor.commit();
    }

    public static String get(Context context,String id){
        SharedPreferences sharedPreferences= context.getSharedPreferences("data", Context .MODE_PRIVATE);
        String data=sharedPreferences.getString(id,null);
        return data;
    }

    public static void delete(Context context,String id){
        SharedPreferences sharedPreferences= context.getSharedPreferences("data", Context .MODE_PRIVATE);
        SharedPreferences.Editor editor =sharedPreferences.edit();
        editor.remove(id);
        editor.commit();
    }

    public static void update(Context context,HashMap<String,String>d){
        if(d.size()==0)return;
        delete(context,d.keySet().iterator().next());
        insert(context,d);
    }

}
