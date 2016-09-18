package zhu.com.ddclient.fragment;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import zhu.com.ddclient.R;
import zhu.com.ddclient.activity.MainActivity;
import zhu.com.ddclient.myinterface.ShowFragment;
import zhu.com.ddclient.util.BitmapUtil;
import zhu.com.ddclient.util.HttpUtil;
import zhu.com.ddclient.util.UserUtil;

/**
 * Created by zhu on 2016/9/11.
 */
public class CartFragment extends Fragment {
    private ListView lv = null;   //购物车列表
    private Context context;
    private BooKListAdapter adapter = null;
    private ShowFragment showDetail = null;
    private JSONArray itemList = null; //购物车清单
    private JSONArray confirmItemList = new JSONArray(); //确认清单
    private ArrayList<JSONObject> tempConfirmItemList = new ArrayList<>();
    private TextView totalPriceTv = null;
    private Double totalPrice = 0.0;  //总价
    public void setShowDetail(MainActivity.ShowOrderConfirmFragment showDetail){
        this.showDetail = showDetail;
    }
    public void setContext(Context context){
        this.context = context;
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        requestServerData();
        View root = inflater.inflate(R.layout.cart_fragment,container,false);
        initView(root);
        return root;
    }
    //初始化
    public void initView(View root){
        totalPriceTv = (TextView) root.findViewById(R.id.totalPrice);
        adapter = new BooKListAdapter();
        lv = (ListView) root.findViewById(R.id.listView);
        lv.setAdapter(adapter);
        root.findViewById(R.id.confirm_btn).setOnClickListener(new View.OnClickListener() {   //下单
            @Override
            public void onClick(View v) {
                 JSONArray jsonArray = new JSONArray();
                 for (int i = 0 ;i < itemList.length() ; i++ ){
                     try {
                         JSONObject jsonObject = itemList.getJSONObject(i);
                         if(jsonObject.getBoolean("isChecked") == true ){
                             jsonArray.put(jsonObject);
                         }
                     } catch (JSONException e) {
                         e.printStackTrace();
                     }
                 }
                 Log.i("购买清单",itemList.toString());
                 showDetail.show(jsonArray,0.0);
            }
        });
        root.findViewById(R.id.update_btn).setOnClickListener(new View.OnClickListener() {  //修改
            @Override
            public void onClick(View v) {

            }
        });
    }
    //向请求服务器购物车数据数据 [{"regname":"zhangfei"}]
    protected void requestServerData(){
        Map<String,String> params = new HashMap<>();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("regname", UserUtil.getUserID());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("params",jsonObject.toString());
        try {
            String s = HttpUtil.postRequest(HttpUtil.getRequestUrl(context)+"/cart.json",params);
            itemList =  new JSONArray(s);
            Log.i("长度: ",itemList.length()+"");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //异步加载图片资源
    public void asynsetImage(String url,final ImageView iv){
        new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... params) {
                return BitmapUtil.getHttpBitmap(params[0]);
            }
            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                iv.setImageBitmap(bitmap);
            }
        }.execute(url);
    }
    class BooKListAdapter extends BaseAdapter {
        public int getCount() {
            return itemList.length();
        }
        public Object getItem(int position) {
            try {
                return itemList.get(position);
            } catch (JSONException e) {
                return null;
            }
        }
        public long getItemId(int position) {
            return position;
        }
        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = LayoutInflater.from(context);
            LinearLayout lineLayout = (LinearLayout) inflater.inflate(R.layout.cart_item,null);
            ImageView bookImgIV = (ImageView) lineLayout.findViewById(R.id.bookImg);
            TextView bookNameTV = (TextView) lineLayout.findViewById(R.id.bookName);
            final TextView priceTV    = (TextView) lineLayout.findViewById(R.id.price);
            final EditText quantityET = (EditText) lineLayout.findViewById(R.id.quantity);
            try {
                itemList.getJSONObject(position).put("isChecked",false);
                JSONObject iteminfo = (JSONObject) itemList.get(position);
                bookNameTV.setText(iteminfo.getString("bookName"));
                priceTV.setText(iteminfo.getString("bookPrice"));
                quantityET.setText(iteminfo.getString("quantity"));
                String  url = HttpUtil.getRequestUrl(context)+"/img/books/"+iteminfo.getString("imageName");
                asynsetImage(url,bookImgIV);
            }catch (Exception e){

            }

            //增加
            lineLayout.findViewById(R.id.add_btn).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                       Integer i = Integer.valueOf(quantityET.getText().toString().trim());
                       i++;
                       quantityET.setText(i.toString());
                       try{
                           itemList.getJSONObject(position).put("quantity",i);
                       }catch (Exception e){
                          Log.i("","修改数量失败");
                       }
                       refreshTotalPrice();
                }
            });
            //减少
            lineLayout.findViewById(R.id.decrase__btn).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Integer i = Integer.valueOf(quantityET.getText().toString().trim());
                    i--;
                    if(i<0)
                        i = 0;
                    quantityET.setText(i.toString());
                    try{
                        itemList.getJSONObject(position).put("quantity",i);
                    }catch (Exception e){
                        Log.i("","修改数量失败");
                    }
                    refreshTotalPrice();
                }
            });

            ((CheckBox)lineLayout.findViewById(R.id.checkBox)).setOnCheckedChangeListener(
                    new CompoundButton.OnCheckedChangeListener(){
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            // TODO Auto-generated method stub
                            if(isChecked){
                                try {
                                    itemList.getJSONObject(position).put("isChecked",true);
                                }catch (Exception e){

                                }
                            }else{
                               try{
                                    itemList.getJSONObject(position).put("isChecked",false);
                               }catch (Exception e){

                               }
                            }
                            refreshTotalPrice();
                        }
                    });
            return lineLayout;
            //请求的url:[/orderconfirm.json]对应的json数据是[{"cusid":"2","addressid":"5","books":[{"bookid":"9","quantity":"1","subtotal":"30.8"}],"total":"30.8","paytype":"0","invoicetype":"2","invoicecontent":"0","deliverprice":"0"}]
        }
    }
    //修改总价格
    public void refreshTotalPrice(){
        Double total = 0.0;
        for (int i = 0 ;i < itemList.length() ; i++ ){
               try {
                   JSONObject obj = itemList.getJSONObject(i);
                   if(obj.getBoolean("isChecked") == true)
                      total = total + obj.getDouble("bookPrice") * obj.getInt("quantity");
               }catch (Exception e){
                  Log.i("","计算总价错误");
               }
        }
        totalPrice = total;
        totalPriceTv.setText(total.toString());
    }


}
