package zhu.com.ddclient.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by zhu on 2016/9/13.
 */
public class BitmapUtil {
    public static final String BASE_URL = "http://10.0.2.2:8080/";
    /**
     * 获取网落图片资源 
     * @param url
     * @return
     */
    public static Bitmap getHttpBitmap(String url) {
        URL myFileURL;
        Bitmap bitmap = null;
        try {
            myFileURL = new URL(url);
            //获得连接
            HttpURLConnection conn = (HttpURLConnection) myFileURL.openConnection();
            //设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
            conn.setConnectTimeout(6000);
            //连接设置获得数据流
            conn.setDoInput(true);
            //不使用缓存
            conn.setUseCaches(false);
            //这句可有可无，没有影响
            //conn.connect();
            //得到数据流
            InputStream is = conn.getInputStream();
            //解析得到图片
            bitmap = BitmapFactory.decodeStream(is);
            //关闭数据流
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            return bitmap;
        }
    }
    //获取图片，若本地存在该图片，即在本地加载，否则请求服务器图片数据
    public static Bitmap getBitmap(Context context ,String url) {
        URL myFileURL;
        Bitmap bitmap = null;
        String filename = url.substring(url.lastIndexOf("/")+1,url.length());
        Log.i("图片名称:",filename);
        String localpath = context.getCacheDir().getAbsolutePath();
        Log.i("本地图片路径:",localpath);
        File file = new File(localpath+"/"+filename);
        if(file.exists()){
            Log.i(filename,"本地图片存在");
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            return bitmap;
        }else {
            Log.i(filename,"本地图片不存在");
            try {
                myFileURL = new URL(url);
                //获得连接
                HttpURLConnection conn = (HttpURLConnection) myFileURL.openConnection();
                //设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
                conn.setConnectTimeout(6000);
                //连接设置获得数据流
                conn.setDoInput(true);
                //不使用缓存
                conn.setUseCaches(false);
                //这句可有可无，没有影响
                //conn.connect();
                //得到数据流
                InputStream is = conn.getInputStream();
                //解析得到图片
                bitmap = BitmapFactory.decodeStream(is);
                //将图片二进制写入本地文件
                FileOutputStream out  = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);//把Bitmap对象解析成流
                //关闭数据流
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                return bitmap;
            }

        }

    }
    //向服务器请求验证码图片
    public static Bitmap getHttpBitmap(Context context,String url) {
        URL myFileURL;
        Bitmap bitmap = null;
        try {
            myFileURL = new URL(url);
            //获得连接
            HttpURLConnection conn = (HttpURLConnection) myFileURL.openConnection();
            //设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
            String cookies = CommonUtil.getValueFromLocal(context,"Set-Cookie");
            if (!"".equals(cookies)){
                conn.addRequestProperty("Cookie",cookies);
            }
            Log.i("验证码请求-会话",cookies);
            conn.setConnectTimeout(6000);
            //连接设置获得数据流
            conn.setDoInput(true);
            //不使用缓存
            conn.setUseCaches(false);
            //这句可有可无，没有影响
            //conn.connect();
            //得到数据流
            InputStream is = conn.getInputStream();
            String string =conn.getHeaderField("Set-Cookie");
            if (string != null && !"".equals(string)){
                String headers[] = string.split(";");
                if (headers.length>0 && headers[0].contains("JSESSIONID"))
                    CommonUtil.saveValueToLocal(context,"Set-Cookie",headers[0]);
            }
            //解析得到图片
            bitmap = BitmapFactory.decodeStream(is);
            //关闭数据流
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            return bitmap;
        }
    }
}
