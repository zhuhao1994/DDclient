package zhu.com.ddclient.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import zhu.com.ddclient.R;
import zhu.com.ddclient.fragment.CartFragment;
import zhu.com.ddclient.fragment.CommentsFragment;
import zhu.com.ddclient.fragment.DetailFragment;
import zhu.com.ddclient.fragment.HomeFragment;
import zhu.com.ddclient.fragment.OrderConfirmFragment;
import zhu.com.ddclient.fragment.OrderListFragment;
import zhu.com.ddclient.myinterface.ShowFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout fragment_container = null;
    private RadioButton btn1 = null;    //主页
    private RadioButton btn2 = null;    //购物车
    private RadioButton btn3 = null;    //订单
    private RadioButton btn4 = null;    //订单确认
    private RadioButton btn5 = null ;   //设置
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }
    //初始化888888888
    protected void initView(){

        btn1  = (RadioButton) findViewById(R.id.bt1);
        btn2  = (RadioButton) findViewById(R.id.bt2);
        btn3  = (RadioButton) findViewById(R.id.bt3);
        btn4  = (RadioButton) findViewById(R.id.bt4);
        btn5  = (RadioButton) findViewById(R.id.bt5);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);

        HomeFragment home = new HomeFragment();
        home.setContext(MainActivity.this);
        home.setShowDetail(new ShowDetailFragment());
        chageView(home);
    }
    //切换fragment
    public void chageView(Fragment fragment){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container,fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    //监听函数
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt1:
                HomeFragment home = new HomeFragment();
                home.setContext(MainActivity.this);
                home.setShowDetail(new ShowDetailFragment());
                chageView(home);
                break;
            case R.id.bt2:

                CartFragment cart = new CartFragment();
                cart.setContext(MainActivity.this);
                cart.setShowDetail(new ShowOrderConfirmFragment());
                chageView(cart);

                break;
            case R.id.bt3:
                OrderListFragment orderListFragment = new OrderListFragment();
                orderListFragment.setContext(MainActivity.this);
                chageView(orderListFragment);
                break;
            case R.id.bt4:
                OrderConfirmFragment orderConfirm = new OrderConfirmFragment();
                orderConfirm.setContext(MainActivity.this);
                orderConfirm.setTotal(0);
                orderConfirm.setConfirmList(new JSONArray());
                chageView(orderConfirm);
                break;
            case R.id.bt5:
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                this.finish();
                break;
        }
    }


    //显示CommentsFragment
    public class ShowCommentsFragment implements ShowFragment{
        public void show() {

        }
        public void show(JSONObject jsonStr) {
            CommentsFragment commentsFragment = new CommentsFragment();
            commentsFragment.setContext(MainActivity.this);
            commentsFragment.setBookinfo(jsonStr);
            chageView(commentsFragment);
        }

        @Override
        public void show(JSONArray s, double d) {

        }
    }


    //显示DetailFragment
    public class ShowDetailFragment implements ShowFragment{
        public void show() {

        }
        public void show(JSONObject jsonobj) {
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setShowComments(new ShowCommentsFragment());
            detailFragment.setContext(MainActivity.this);
            detailFragment.setBookInfo(jsonobj);
            chageView(detailFragment);
        }
        @Override
        public void show(JSONArray s, double d) {

        }
    }

    //显示ConfirmFragment
    public class ShowOrderConfirmFragment implements ShowFragment{

        public void show() {

        }
        public void show(JSONObject jsonobj) {

        }

        @Override
        public void show(JSONArray s, double d) {
            OrderConfirmFragment orderConfirmFragment = new OrderConfirmFragment();
            orderConfirmFragment.setContext(MainActivity.this);
            orderConfirmFragment.setTotal((float)d);
            orderConfirmFragment.setConfirmList(s);
            chageView(orderConfirmFragment);
        }
    }


}
