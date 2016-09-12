package zhu.com.ddclient.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import zhu.com.ddclient.myinterface.ShowFragment;
import zhu.com.ddclient.R;

/**
 * Created by zhu on 2016/9/12.
 */
public class CommentsFragment extends Fragment {
    private Context context;
    private CommentsAdapter adapter = null;
    private ListView lv = null;
    public void setContext(Context context){
        this.context = context;
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.comments_fragment,container,false);
        lv = (ListView) root.findViewById(R.id.listView);
        adapter = new CommentsAdapter();
        lv.setAdapter(adapter);
        return root;
    }
    class CommentsAdapter extends BaseAdapter {
        public int getCount() {
            return 8;
        }
        public Object getItem(int position) {
            return 1;
        }
        public long getItemId(int position) {
            return position;
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            LinearLayout lineLayout = (LinearLayout) inflater.inflate(R.layout.comments_item,null);
            return lineLayout;
        }
    }

}
