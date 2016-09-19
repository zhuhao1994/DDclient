package zhu.com.ddclient.fragment;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import zhu.com.ddclient.myinterface.ShowFragment;
import zhu.com.ddclient.R;
import zhu.com.ddclient.util.BitmapUtil;
import zhu.com.ddclient.util.HttpUtil;

/**
 * Created by zhu on 2016/9/12.
 */
public class CommentsFragment extends Fragment {
    private Context context;
    private CommentsAdapter adapter = null;
    private JSONObject bookinfo = null;     //书籍信息
    private ListView lv = null;             //评论列表
    private JSONArray commentsList = null ; //评论信息
    public void setBookinfo(JSONObject bookinfo){
        this.bookinfo = bookinfo;
    }
    public void setContext(Context context){

        this.context = context;
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        requestServerData();
        View root = inflater.inflate(R.layout.comments_fragment,container,false);
        initView(root);
        return root;
    }
    public void initView(View root){
        lv = (ListView) root.findViewById(R.id.listView);
        ImageView bookImgIV = (ImageView) root.findViewById(R.id.bookImg);
        TextView  bookNameTV = (TextView) root.findViewById(R.id.bookName);
        try {
            bookNameTV.setText(bookinfo.getString("bookName"));
            String  url = BitmapUtil.BASE_URL+bookinfo.getString("imagePath")+bookinfo.getString("imageName");
            asynsetImage(url,bookImgIV);
        }catch (Exception e){
        }

        adapter = new CommentsAdapter();
        lv.setAdapter(adapter);
    }
    //向请求服务评论数据数据
    protected void requestServerData(){
        Map<String,String> params = new HashMap<>();
       // JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("bookid",bookinfo.getString("bookId"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
       // jsonArray.put(jsonObject);
        params.put("params",jsonObject.toString());
        try {
            String s = HttpUtil.postRequest(HttpUtil.getRequestUrl(context)+"/comments.json",params);
            commentsList =  new JSONArray(s);
            Log.i("长度: ",commentsList.length()+"");
        } catch (Exception e) {
            e.printStackTrace();
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

    class CommentsAdapter extends BaseAdapter {
        public int getCount() {
            return commentsList.length();
        }
        public Object getItem(int position) {
            try {
                return commentsList.get(position);
            } catch (JSONException e) {
               return null;
            }
        }
        public long getItemId(int position) {
            return position;
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            LinearLayout lineLayout = (LinearLayout) inflater.inflate(R.layout.comments_item,null);
            TextView customerNameTV = (TextView) lineLayout.findViewById(R.id.customerName);
            TextView contentDateTV = (TextView) lineLayout.findViewById(R.id.contentDate);
            TextView contentTV = (TextView) lineLayout.findViewById(R.id.content);
            RatingBar starTV = (RatingBar) lineLayout.findViewById(R.id.star);
            try {
                JSONObject comments = (JSONObject) commentsList.get(position);
                customerNameTV.setText(comments.getString("customerName"));
                contentDateTV.setText(comments.getString("contentDate"));
                contentTV.setText("     "+comments.getString("content"));
                starTV.setRating(comments.getInt("star"));
            }catch (Exception e){

            }
            return lineLayout;
        }
    }

}
