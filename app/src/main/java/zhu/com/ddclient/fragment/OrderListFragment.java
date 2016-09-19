package zhu.com.ddclient.fragment;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import zhu.com.ddclient.R;
import zhu.com.ddclient.util.BitmapUtil;
import zhu.com.ddclient.util.CommonUtil;
import zhu.com.ddclient.util.HttpUtil;

/**
 * Created by zss on 2016/9/12.
 */
public class OrderListFragment extends Fragment {
    private ListView lv = null;//订单列表
    private Context context;
    private OrderListAdapter adapter = null;
    private JSONArray orderArray = null;

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JSONObject requestObj =new JSONObject();
        try{
            requestObj.put("cusid", CommonUtil.getValueFromLocal(context,"uid")+"");
        }catch (JSONException e){
            e.printStackTrace();
        }
        Map<String,String> map = new HashMap<>();
        map.put("params",requestObj.toString());
        Log.i("订单请求参数",requestObj.toString());
        try{
            String s = HttpUtil.postRequest(HttpUtil.getRequestUrl(context)+"/orders.json",map);
            orderArray = new JSONArray(s);
            Log.i("订单请求数据",orderArray.toString());
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.order_list_fragment,container,false);
        adapter = new OrderListAdapter();
        lv = (ListView) root.findViewById(R.id.listView);
        lv.setAdapter(adapter);
        return root;
    }

    class OrderListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return orderArray.length();
        }

        @Override
        public Object getItem(int position) {
            Object object = null;
            try{
               object = orderArray.get(position);
            }catch (JSONException e){
                Log.i("load order error",e.getMessage());
            }
            return object;

        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.order_item,null);
            try {
                JSONObject jsonObject = (JSONObject) orderArray.get(position);
                //订单编号
                ((TextView)linearLayout.findViewById(R.id.ordid)).setText(jsonObject.getString("ordid"));

                //订单总价
                ((TextView)linearLayout.findViewById(R.id.price)).setText(jsonObject.getString("price"));

                //下单日期
                ((TextView)linearLayout.findViewById(R.id.createdate)).setText(jsonObject.getString("createdate"));

                ListView listView = (ListView) linearLayout.findViewById(R.id.listView);
                OrderBookAdapter orderBookAdapter = new OrderBookAdapter(jsonObject.getJSONArray("orderitems"));
                listView.setAdapter(orderBookAdapter);
            }catch (JSONException e){
                Log.i("load order item error",e.getMessage());
            }

            return linearLayout;
        }
    }
    class OrderBookAdapter extends BaseAdapter{
        private JSONArray bookArray = null;
        public OrderBookAdapter(){}
        public OrderBookAdapter(JSONArray bookArray){
            this.bookArray = bookArray;
        }

        @Override
        public int getCount() {
            return bookArray.length();
        }
        @Override
        public Object getItem(int position) {
            Object object = null;
            try{
                object = orderArray.get(position);
            }catch (JSONException e){
                Log.i("load order error",e.getMessage());
            }
            return object;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.order_book_item,null);

            try{
                JSONObject jsonObject = (JSONObject) bookArray.get(position);

                ((TextView)linearLayout.findViewById(R.id.bookName)).setText(jsonObject.getString("bookName"));

                ((TextView)linearLayout.findViewById(R.id.quantity)).setText(jsonObject.getString("quantity"));

                ((TextView)linearLayout.findViewById(R.id.subTotal)).setText(jsonObject.getString("subTotal"));

                String imgUrl = HttpUtil.getRequestUrl(context)+"/img/books/"+jsonObject.getString("bookImageName");

                ImageView imageView = (ImageView) linearLayout.findViewById(R.id.bookImg);
                asynsetImage(imgUrl,imageView);
            }catch (JSONException e){
                Log.i("book load error",e.getMessage());
            }
            return linearLayout;
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
}
