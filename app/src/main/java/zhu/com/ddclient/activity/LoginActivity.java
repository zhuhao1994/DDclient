package zhu.com.ddclient.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;

import android.util.Log;

import android.view.View;


import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.oracle.dd.tool.json.request.entity.LoginRequestParams;
import com.oracle.dd.tool.json.response.entity.LoginResponseParams;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import zhu.com.ddclient.R;
import zhu.com.ddclient.util.BitmapUtil;
import zhu.com.ddclient.util.CommonUtil;
import zhu.com.ddclient.util.HttpUtil;

public class LoginActivity extends AppCompatActivity {
    private CustomDialog dialog;
    private ImageView imageView;
    private String ipAddress = "10.0.2.2";//ip地址
    private String port = "8080";//端口号
    private SharedPreferences sharedPreferences;
    private EditText editText1;//用户名
    private EditText editText2;//密码
    private EditText editText3;//验证码
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
   // private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
//        new Thread(){
//            @Override
//            public void run() {
//                BitmapUtil.getBitmap(LoginActivity.this,"http://172.19.22.17:8080/img/books/20285763-1_b.jpg");
//            }
//        }.start();

        showToast("start!");
        sharedPreferences = this.getSharedPreferences("my",MODE_WORLD_READABLE);
        //从shareReference中获取ip和port
        getSocket();
        //发请求
        imageView = (ImageView)findViewById(R.id.codeView);
        Log.i("imageView","http://"+ipAddress+":"+port+"/code.jhtml");
        asynsetImage(HttpUtil.getRequestUrl(this)+"/code.jhtml",imageView);
         imageView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 asynsetImage("http://"+ipAddress+":"+port+"/code.jhtml",imageView);
             }
         });
        editText1 = (EditText) findViewById(R.id.uid);
        editText2 = (EditText) findViewById(R.id.password);
        editText3 = (EditText) findViewById(R.id.code);

    }

    public void login(View v) {
        String uid = editText1.getText().toString();
        String password = editText2.getText().toString();
        String code = editText3.getText().toString();
        if(validCheck(uid,password,code)){
            LoginRequestParams params = new LoginRequestParams();
            params.setCode(code);
            params.setPwd(password);
            params.setUid(uid);
            LoginResponseParams answer = requestServerData(params);
            if(answer.getIsOk() == true){
                CommonUtil.saveValueToLocal(getApplicationContext(),"uid",answer.getCusid());
                CommonUtil.saveValueToLocal(getApplicationContext(),"name",uid);
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
            else if(answer.getIsOk() == false){
                showToast(answer.getErrorinfo());
            }

        }
    }
    protected LoginResponseParams requestServerData(LoginRequestParams loginRequestParams){
        LoginResponseParams responseParams = new LoginResponseParams();
        JSONArray responseArray = null;
        Map<String,String> params = new HashMap<>();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("code",loginRequestParams.getCode());
            jsonObject.put("pwd",loginRequestParams.getPwd());
            jsonObject.put("uid",loginRequestParams.getUid());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("params",jsonObject.toString());

        try {
             String s = HttpUtil.postRequest(getApplicationContext(),HttpUtil.getRequestUrl(this)+"/login.json",params);
            JSONObject object = new JSONObject(s);
            boolean isOK = false;
            try {
                isOK = object.getBoolean("isOk");

            }catch (JSONException jse){
                 return responseParams;
            }
            if(isOK == true)
                responseParams.setCusid(object.getString("cusid"));
            else
                responseParams.setErrorinfo(object.getString("errorinfo"));
            responseParams.setIsOk(isOK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseParams;
    }
    public void dialogBtnClick(View v) {
        dialog = new CustomDialog(LoginActivity.this);
        dialog.setTitle("配置选项");
        getSocket();
        dialog.setSettingText(ipAddress,port);
        dialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //修改全局变量 ipAddress和port 和shareReference
                ipAddress = dialog.getIpAddress();
                port = dialog.getPort();
                setSocket();
                dialog.dismiss();
                //重新加载验证码
                asynsetImage("http://"+ipAddress+":"+port+"/code.jhtml",imageView);

            }
        });
        dialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public void getSocket(){
        ipAddress = sharedPreferences.getString("ipAddress","");
        port = sharedPreferences.getString("port","");
    }
    public  void setSocket(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("ipAddress",ipAddress);
        editor.putString("port",port);
        editor.commit();
    }
    //异步加载图片资源
    public void asynsetImage(String url,final ImageView iv){
        new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... params) {
                Bitmap bitmap =  BitmapUtil.getHttpBitmap(getApplicationContext(),params[0]);
                return  bitmap;
            }
            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                if(bitmap == null){
                    showToast("无法连接到服务器");
                }
                iv.setImageBitmap(bitmap);
            }
        }.execute(url);
    }
   public  void showToast(String msg){
       Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
   }
    public boolean validCheck(String uid,String password,String code){
        Log.i("validCheck",uid+"|"+password+"|"+code);
        boolean result = false;
        if(uid == null || "".endsWith(uid))
            showToast("请填写用户名");
        else if(password == null || "".endsWith(password))
            showToast("请填写密码");
        else if (code == null || "".endsWith(code))
            showToast("请填写验证码");
        else
            result = true;
        return result;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CommonUtil.saveValueToLocal(getApplicationContext(),"Set-Cookie","");
    }
}
