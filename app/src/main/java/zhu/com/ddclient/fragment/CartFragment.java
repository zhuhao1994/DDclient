package zhu.com.ddclient.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import zhu.com.ddclient.R;

/**
 * Created by zhu on 2016/9/11.
 */
public class CartFragment extends Fragment {
    private ListView lv = null;   //购物车列表
    private Context context;
    private BooKListAdapter adapter = null;
    public void setContext(Context context){
        this.context = context;
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.cart_fragment,container,false);
        adapter = new BooKListAdapter();
        lv = (ListView) root.findViewById(R.id.listView);
        lv.setAdapter(adapter);
        return root;
    }
    class BooKListAdapter extends BaseAdapter {
        public int getCount() {
            return 2;
        }
        public Object getItem(int position) {
            return 1;
        }
        public long getItemId(int position) {
            return position;
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            LinearLayout lineLayout = (LinearLayout) inflater.inflate(R.layout.cart_item,null);
            return lineLayout;
        }
    }
}
