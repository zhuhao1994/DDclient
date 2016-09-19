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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import zhu.com.ddclient.R;
import zhu.com.ddclient.activity.MainActivity.ShowDetailFragment;
import zhu.com.ddclient.util.BitmapUtil;
import zhu.com.ddclient.util.HttpUtil;
import zhu.com.ddclient.myinterface.ShowFragment;

/**
 * Created by zhu on 2016/9/8.
 */
public class HomeFragment extends Fragment implements AdapterView.OnItemClickListener {
    private ListView lv = null;   //书目信息列表
    private Context context;
    private BooKListAdapter adapter = null;
    private ShowFragment showDetail = null;
    private JSONArray serverData = null ;//存储服务器数据
    public void setShowDetail(ShowDetailFragment showDetail){
        this.showDetail = showDetail;
    }
    public void setContext(Context context){
        this.context = context;
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,Bundle savedInstanceState) {

        requestServerData("");  //向服务器请求书籍列表
        View root = inflater.inflate(R.layout.fragment_home,container,false);
        lv = (ListView) root.findViewById(R.id.listView);
        lv.setOnItemClickListener(this);
        adapter = new BooKListAdapter();
        lv.setAdapter(adapter);
        return root;
    }

    //监听listview
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            JSONObject bookinfo = (JSONObject) serverData.get(position);
            showDetail.show(bookinfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    //请求服务数据 参数 bookName
    protected void requestServerData(String bookName){
        Map<String,String> params = new HashMap<>();
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonObject.put("name",bookName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        jsonArray.put(jsonObject);
        params.put("params",jsonObject.toString());
        try {
            String s = HttpUtil.postRequest(HttpUtil.getRequestUrl(context)+"/books.json",params);
            Log.i("服务器数据: ",s);
            serverData =  new JSONArray(s);
            Log.i("长度: ",serverData.length()+"");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class BooKListAdapter extends BaseAdapter {
        public int getCount() {
            return serverData.length();
        }
        public Object getItem(int position) {
            try {
                return serverData.get(position);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        public long getItemId(int position) {
            return position;
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            LinearLayout lineLayout = (LinearLayout) inflater.inflate(R.layout.book_item,null);
            TextView bookNameTV = (TextView) lineLayout.findViewById(R.id.bookName);
            TextView priceTV = (TextView) lineLayout.findViewById(R.id.price);
            RatingBar starNumTV = (RatingBar) lineLayout.findViewById(R.id.starnum);
            ImageView bookImgIV = (ImageView) lineLayout.findViewById(R.id.bookImg);
            try {
                JSONObject bookInfo = (JSONObject) serverData.get(position);
                String  url = HttpUtil.getRequestUrl(context)+"/"+bookInfo.getString("imagePath")+bookInfo.getString("imageName");
                asynsetImage(url,bookImgIV);
                bookNameTV.setText(bookInfo.getString("bookName"));
                priceTV.setText(bookInfo.getString("price"));
                starNumTV.setRating(bookInfo.getInt("starnum"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return lineLayout;
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
