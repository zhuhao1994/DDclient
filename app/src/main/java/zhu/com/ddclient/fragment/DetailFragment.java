package zhu.com.ddclient.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import zhu.com.ddclient.R;
import zhu.com.ddclient.activity.MainActivity;
import zhu.com.ddclient.myinterface.ShowFragment;
import zhu.com.ddclient.activity.MainActivity.ShowCommentsFragment;
/**
 * Created by zhu on 2016/9/9.
 */
public class DetailFragment extends Fragment {
    private ShowFragment  showComments =null;
    private Button detail_btn = null;
    public void setShowComments(ShowCommentsFragment showComments){
        this.showComments = showComments;
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.deatil_fragment,container,false);
        detail_btn = (Button)root.findViewById(R.id.detail_btn);
        detail_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showComments.show();
            }
        });
        return root;
    }

}
