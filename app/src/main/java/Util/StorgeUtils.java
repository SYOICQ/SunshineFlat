package Util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class StorgeUtils {
    private String cachePath = "";
    public StorgeUtils(Context context){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            cachePath = Environment.getExternalStorageDirectory().toString()
                    + "/SunshineFlat/cache/";
            File file = new File(cachePath);
            if(!file.exists()){
                file.mkdirs();
            }
            //图片缓存路径
            File file1 = new File(cachePath+"jpg/");
            if(!file1.exists()){
                file1.mkdirs();
            }
        }else {
            cachePath = context.getFilesDir().getAbsolutePath() + "/SunshineFlat/cache/";
            File file = new File(cachePath);
            if(!file.exists()){
                file.mkdirs();
            }
            //图片缓存路径
            File file1 = new File(cachePath+"jpg/");
            if(!file1.exists()){
                file1.mkdirs();
            }
        }
        Log.d("StorgeUtils", cachePath);
    }

    public String getCachePath(){
        return cachePath;
    }

    public void copyDrawableToSd(Context context,int id){
        try {
            File sdFile = new File(cachePath+"jpg/"+id+".png");
            if(sdFile.exists()){
                sdFile.delete();
            }
            sdFile.createNewFile();
            InputStream inputStream = context.getResources().openRawResource(id);
            FileOutputStream fos = new FileOutputStream(sdFile);
            byte[] buffer = new byte[8 * 1024];// 8K
            while (inputStream.read(buffer) > 0){
                fos.write(buffer);
            }
            inputStream.close();
            fos.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
