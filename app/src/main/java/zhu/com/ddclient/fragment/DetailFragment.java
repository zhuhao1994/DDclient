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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import zhu.com.ddclient.R;
import zhu.com.ddclient.activity.MainActivity;
import zhu.com.ddclient.myinterface.ShowFragment;
import zhu.com.ddclient.activity.MainActivity.ShowCommentsFragment;
import zhu.com.ddclient.util.BitmapUtil;
import zhu.com.ddclient.util.HttpUtil;

/**
 * Created by zhu on 2016/9/9.
 */
public class DetailFragment extends Fragment {
    private ShowFragment  showComments =null;
    private JSONObject bookInfo = null; //书籍信息
    private Context context;
    public void setContext(Context context){
        this.context = context;
    }
    public void setShowComments(ShowCommentsFragment showComments){
        this.showComments = showComments;
    }
    public void setBookInfo(JSONObject bookInfo){
        this.bookInfo = bookInfo;
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.deatil_fragment,container,false);
        initView(root);

        return root;
    }
    //初始化
    public void initView(final View root){
        Button detail_btn = (Button)root.findViewById(R.id.detail_btn);
        Button add_btn = (Button)root.findViewById(R.id.add_btn);
        TextView bookNameTV = (TextView) root.findViewById(R.id.bookName);
        TextView priceTV = (TextView) root.findViewById(R.id.price);
        RatingBar startnumRB = (RatingBar) root.findViewById(R.id.starnum);
        TextView authorTV = (TextView) root.findViewById(R.id.author);
        TextView publisherTV = (TextView) root.findViewById(R.id.publisher);
        TextView stockstatusTV = (TextView) root.findViewById(R.id.stockstatus);
        TextView sellVolumeTV = (TextView) root.findViewById(R.id.sellVolume);
        ImageView bookImgIV = (ImageView)root.findViewById(R.id.bookImg);
        final TextView introductionTV = (TextView) root.findViewById(R.id.introduction);
        final TextView catalogTV = (TextView) root.findViewById(R.id.catalog);
        try {
            bookNameTV.setText(bookInfo.getString("bookName"));
            priceTV.setText(bookInfo.getString("price"));
            startnumRB.setRating(bookInfo.getInt("starnum"));
            authorTV.setText(bookInfo.getString("author"));
            publisherTV.setText(bookInfo.getString("publisher"));
            stockstatusTV.setText(bookInfo.getString("stockstatus"));
            sellVolumeTV.setText(bookInfo.getString("salesVolume"));
            introductionTV.setText(bookInfo.getString("introduction"));
            catalogTV.setText(bookInfo.getString("catalog"));
            String  url = HttpUtil.getRequestUrl(context)+"/"+bookInfo.getString("imagePath")+bookInfo.getString("imageName");
            asynsetImage(url,bookImgIV);
        }catch (Exception e){

        }

        root.findViewById(R.id.tr_catalog_btn).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                View tr = root.findViewById(R.id.tr_catalog);
                if(tr.getVisibility() == View.VISIBLE)
                    tr.setVisibility(View.GONE);
                else {
                    tr.setVisibility(View.VISIBLE);
                }
            }
        });
        root.findViewById(R.id.tr_introduction_btn).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                View tr = root.findViewById(R.id.tr_introduction);
                if(tr.getVisibility() == View.VISIBLE)
                    tr.setVisibility(View.GONE);
                else {
                    tr.setVisibility(View.VISIBLE);
                }
            }
        });
        detail_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showComments.show(bookInfo);
            }
        });
        add_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                 addCart();              //加入购物车
            }
        });

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
    //假如购物车 [{"bookid":2,"regname":"zhangfei","op":"add"}]
    public void addCart(){
        Map<String,String> params = new HashMap<>();
        JSONObject jsonObject = new JSONObject();
        JSONObject rst = null;
        try {
            jsonObject.put("bookid",bookInfo.getString("bookId"));
            jsonObject.put("regname","zhangfei");
            jsonObject.put("op","add");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("params",jsonObject.toString());
        try {
            String s = HttpUtil.postRequest(HttpUtil.getRequestUrl(context)+"/cart.json",params);
            rst = new JSONObject(s);
            if(rst.getBoolean("isOk") == true){
                Toast.makeText(context,"加入购物车成功",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(context,"加入购物车失败",Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
