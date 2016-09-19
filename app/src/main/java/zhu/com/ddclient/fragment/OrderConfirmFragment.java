package zhu.com.ddclient.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import zhu.com.ddclient.R;
import zhu.com.ddclient.myinterface.ShowFragment;
import zhu.com.ddclient.util.BitmapUtil;
import zhu.com.ddclient.util.CommonUtil;
import zhu.com.ddclient.util.HttpUtil;

/**
 * Created by zhu on 2016/9/12.
 */
public class OrderConfirmFragment extends Fragment {
    private ListView lv = null;   //
    private TextView devilerView = null;
    private Context context;
    private OrderConfirmListAdapter adapter = null;
    private JSONArray confirmList = null;
    private TableRow deliverRow = null;
    private  TextView address = null;
    private float total = 0;
    private String deliverWayArray[] = {"顺丰快递","邦德物流","EMS"};
    private String addressId = "";
    private Button button = null;

    public void setTotal(float total) {
        this.total = total;
    }

    public void setConfirmList(JSONArray confirmList) {
        this.confirmList = confirmList;
    }

    public void setContext(Context context){
        this.context = context;
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(confirmList != null && confirmList.length()>0){
            View root = inflater.inflate(R.layout.order_confirm_fragment,container,false);
            button = (Button) root.findViewById(R.id.comfirmBtn);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JSONObject requestObj = new JSONObject();
                    try{
                        requestObj.put("cusid",CommonUtil.getValueFromLocal(context,"uid"));
                        requestObj.put("addressid",addressId);
                        JSONArray books = new JSONArray();
                        for(int i = 0; i<confirmList.length();i++){
                            JSONObject object = new JSONObject();
                            JSONObject io = (JSONObject) confirmList.get(i);
                            object.put("bookid",io.getString("bookId"));
                            object.put("quantity",io.getString("quantity"));
                            float subtotal = Float.parseFloat(io.getString("quantity")) * Float.parseFloat(io.getString("bookPrice"));
                            object.put("subtotal",subtotal+"");
                            books.put(object);
                        }
                        requestObj.put("books",books);
                        requestObj.put("total",total+"");
                        requestObj.put("paytype","0");
                        requestObj.put("invoicetype","2");
                        requestObj.put("invoicecontent","0");
                        requestObj.put("deliverprice","0");


                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    Map<String,String> map = new HashMap<>();
                    map.put("params",requestObj.toString());
                    Log.i("订单确认请求的参数",requestObj.toString());
                    try{
                        String s = HttpUtil.postRequest(HttpUtil.getRequestUrl(context)+"/orderconfirm.json",map);
                        JSONObject object = new JSONObject(s);
                        Log.i("订单确认请求数据",object.toString());
                        if(object.getBoolean("isOk")){
                            Toast.makeText(context,"下单成功",Toast.LENGTH_SHORT).show();

                        }
                        else{
                            Toast.makeText(context,"下单失败",Toast.LENGTH_SHORT).show();
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(context,"加载收获地址错误",Toast.LENGTH_SHORT).show();
                    }
                }


            });
            //选择收货方式
            deliverRow = (TableRow) root.findViewById(R.id.deliverRow);
            devilerView = (TextView) root.findViewById(R.id.devilerView);
            final ChoiceOnClickListener choiceOnClickListener = new ChoiceOnClickListener();
            deliverRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(context)
                            .setTitle("请选择")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setSingleChoiceItems(deliverWayArray , 0, choiceOnClickListener)
                            .setPositiveButton("确定",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    devilerView.setText(deliverWayArray[choiceOnClickListener.getWhich()]);

                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();
                }
            });
            //加载用户名
            TextView nameView = (TextView) root.findViewById(R.id.name);
            nameView.setText(CommonUtil.getValueFromLocal(context,"name"));
            //加载收获地址
            address = (TextView) root.findViewById(R.id.address);
            JSONObject requestObj = new JSONObject();
            try{
                requestObj.put("cusid", CommonUtil.getValueFromLocal(context,"uid")+"");
            }catch (JSONException e){
                e.printStackTrace();
            }
            Map<String,String> map = new HashMap<>();
            map.put("params",requestObj.toString());
            Log.i("收货地址请求参数",requestObj.toString());
            try{
                String s = HttpUtil.postRequest(HttpUtil.getRequestUrl(context)+"/address.json",map);
                JSONObject addressObject = new JSONObject(s);
                Log.i("收货地址请求数据",addressObject.toString());
                String str = addressObject.getString("address");
                addressId = addressObject.getString("addressid");
                address.setText(str);

            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(context,"加载收获地址错误",Toast.LENGTH_SHORT).show();
            }

            //加载书籍
            adapter = new OrderConfirmListAdapter();
            lv = (ListView) root.findViewById(R.id.listview);
            lv.setAdapter(adapter);

            //总价钱
            TextView totalView = (TextView) root.findViewById(R.id.total);
            totalView.setText(total+"");
            return root;

            //点击下单
        }
        else {
            View root = inflater.inflate(R.layout.no_orders,container,false);
            return root;
        }

    }
    class OrderConfirmListAdapter extends BaseAdapter {
        public int getCount() {
            return confirmList.length();
        }
        public Object getItem(int position) {
            Object object = null;

            try{
                object = confirmList.get(position);
            }catch (JSONException e){
                e.printStackTrace();
            }
            return  object;
        }
        public long getItemId(int position) {
            return position;
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            LinearLayout lineLayout = (LinearLayout) inflater.inflate(R.layout.order_confirm_item,null);
            try{
                JSONObject book = (JSONObject) confirmList.get(position);
                if (book.getBoolean("isChecked")){
                    TextView bookName = (TextView) lineLayout.findViewById(R.id.bookName);
                    bookName.setText(book.getString("bookName"));

                    TextView bookPrice = (TextView)lineLayout.findViewById(R.id.bookPrice);
                    bookPrice.setText(book.getString("bookPrice"));

                    TextView quantity = (TextView) lineLayout.findViewById(R.id.quantity);
                    quantity.setText("x"+book.getString("quantity"));

                    ImageView bookImg = (ImageView) lineLayout.findViewById(R.id.bookImg);
                    String url = HttpUtil.getRequestUrl(context)+"/img/books/"+book.getString("imageName");
                    asynsetImage(url,bookImg);
                }

            }catch (JSONException e){
                e.printStackTrace();
            }


            return lineLayout;
        }
    }
    private class ChoiceOnClickListener implements DialogInterface.OnClickListener {

        private int which = 0;
        @Override
        public void onClick(DialogInterface dialogInterface, int which) {
            this.which = which;
        }

        public int getWhich() {
            return which;
        }
    }
    //异步加载图片资源
    public void asynsetImage(String url,final ImageView iv){
        new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... params) {
                return BitmapUtil.getBitmap(context,params[0]);
            }
            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                iv.setImageBitmap(bitmap);
            }
        }.execute(url);
    }


}


