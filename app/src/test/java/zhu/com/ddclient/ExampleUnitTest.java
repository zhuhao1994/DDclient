package zhu.com.ddclient;

import android.util.Log;

import com.oracle.dd.tool.json.request.entity.LoginRequestParams;
import com.oracle.dd.tool.json.response.entity.LoginResponseParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import zhu.com.ddclient.util.HttpUtil;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {

    }
    public static void main(String args[]){
        LoginRequestParams loginRequestParams = new LoginRequestParams();
        loginRequestParams.setUid("02");
        loginRequestParams.setCode("23");
        loginRequestParams.setPwd("1234600");
        LoginResponseParams responseParams = requestServerData(loginRequestParams);
        System.out.println(responseParams);
    }
    protected static LoginResponseParams requestServerData(LoginRequestParams loginRequestParams){
        LoginResponseParams responseParams = null;
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
        Log.i("LoginRequestParams",jsonObject.toString());
        try {
            Log.i("databefore",jsonObject.toString());
            String s = HttpUtil.postRequest("http://10.0.2.2:8080"+"/login.json",params);
            System.out.println(s);
            Log.i("dataafter",s);
//            responseArray =  new JSONArray(s);
//            Log.i("长度: ",responseArray.length()+"");
//            if ( responseArray.length()>0){
//                JSONObject object = (JSONObject)responseArray.get(0);
//                boolean isOK = false;
//                 try {
//                     isOK = object.getBoolean("isOk");
//
//                 }catch (JSONException jse){
//                      return responseParams;
//                }
//                 if(isOK == true)
//                    responseParams.setCusid(object.getString("cusid"));
//                else
//                    responseParams.setErrorinfo(object.getString("errorinfo"));
//                 responseParams.setIsOk(isOK);
//            }
//            JSONObject object = new JSONObject(s);
//            boolean isOK = false;
//            try {
//                isOK = object.getBoolean("isOk");
//
//            }catch (JSONException jse){
//                 return responseParams;
//            }
//            if(isOK == true)
//                responseParams.setCusid(object.getString("cusid"));
//            else
//                responseParams.setErrorinfo(object.getString("errorinfo"));
//            responseParams.setIsOk(isOK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseParams;
    }
}