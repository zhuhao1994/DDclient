package zhu.com.ddclient.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * Created by zhu on 2016/9/13.
 */
public class HttpUtil
{
    // 创建HttpClient对象
    public static HttpClient httpClient = new DefaultHttpClient();
    public static final String BASE_URL = "http:/10.0.2.2:8080/";
    /**
     *
     * @param url 发送请求的URL
     * @return 服务器响应字符串
     * @throws Exception
     */
    public static String getRequest(final String url) throws Exception
    {
        FutureTask<String> task = new FutureTask<String>(new Callable<String>()
                 {
                    @Override
                    public String call() throws Exception
                    {
                        // 创建HttpGet对象。
                        HttpGet get = new HttpGet(url);
                        // 发送GET请求
                        HttpResponse httpResponse = httpClient.execute(get);
                        // 如果服务器成功地返回响应
                        if (httpResponse.getStatusLine()
                                .getStatusCode() == 200)
                        {
                            // 获取服务器响应字符串
                            String result = EntityUtils
                                    .toString(httpResponse.getEntity());
                            return result;
                        }
                        return null;
                    }
                });
        new Thread(task).start();
        return task.get();
    }

    /**
     * @param url 发送请求的URL
     * @param rawParams 请求参数
     * @return 服务器响应字符串
     * @throws Exception
     */
    public static String postRequest(final Context context, final String url
            , final Map<String ,String> rawParams)throws Exception
    {
        FutureTask<String> task = new FutureTask<String>(
                new Callable<String>()
                {
                    @Override
                    public String call() throws Exception
                    {
                        // 创建HttpPost对象。
                        HttpPost post = new HttpPost(url);
                        // 如果传递参数个数比较多的话可以对传递的参数进行封装
                        List<NameValuePair> params = new ArrayList<>();
                        for(String key : rawParams.keySet())
                        {
                            //封装请求参数
                            params.add(new BasicNameValuePair(key, rawParams.get(key)));
                        }
                        String cookies = CommonUtil.getValueFromLocal(context,"Set-Cookie");
                        if (cookies != "")
                          post.setHeader("Cookie",cookies);
                        Log.i("http请求会话",cookies);
                        // 设置请求参数
                        post.setEntity(new UrlEncodedFormEntity(params, "gbk"));
                        // 发送POST请求
                        HttpResponse httpResponse = httpClient.execute(post);
                        // 如果服务器成功地返回响应
                        if (httpResponse.getStatusLine().getStatusCode() == 200)
                        {
                            HttpEntity entity = httpResponse.getEntity();
                            //将会话id写入本次
                            Header headers[] = httpResponse.getHeaders("Set-Cookie");
                            for(Header header:headers){
                                HeaderElement elements[] = header.getElements();
                                for(HeaderElement element:elements)
                                   if("JSESSIONID".equals(element.getName()))
                                       CommonUtil.saveValueToLocal(context,"Set-Cookie","JSESSIONID="+element.getValue());
                            }
                            // 获取服务器响应字符串
                                String  result = EntityUtils.toString(entity);

                            return result;
                        }
                        return null;
                    }
                });
        new Thread(task).start();
        return task.get();
    }
    public static String postRequest(final String url
            , final Map<String ,String> rawParams)throws Exception
    {
        FutureTask<String> task = new FutureTask<String>(
                new Callable<String>()
                {
                    @Override
                    public String call() throws Exception
                    {
                        // 创建HttpPost对象。
                        HttpPost post = new HttpPost(url);
                        // 如果传递参数个数比较多的话可以对传递的参数进行封装
                        List<NameValuePair> params = new ArrayList<>();
                        for(String key : rawParams.keySet())
                        {
                            //封装请求参数
                            params.add(new BasicNameValuePair(key, rawParams.get(key)));
                        }
                        // 设置请求参数
                        post.setEntity(new UrlEncodedFormEntity(params, "gbk"));
                        // 发送POST请求
                        HttpResponse httpResponse = httpClient.execute(post);
                        // 如果服务器成功地返回响应
                        if (httpResponse.getStatusLine().getStatusCode() == 200)
                        {
                            // 获取服务器响应字符串
                            String  result = EntityUtils.toString(httpResponse.getEntity());
                            return result;
                        }
                        return null;
                    }
                });
        new Thread(task).start();
        return task.get();
    }
    public static String getRequestUrl(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("my",Context.MODE_WORLD_READABLE);
        String ipAddress =sharedPreferences.getString("ipAddress","");
        String port = sharedPreferences.getString("port","");
        return "http://"+ipAddress+":"+port;
    }
}
