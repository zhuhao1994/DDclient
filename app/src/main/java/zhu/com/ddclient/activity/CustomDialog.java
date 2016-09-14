package zhu.com.ddclient.activity;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;

import zhu.com.ddclient.R;

/**
 * @Function: 自定义对话框
 * @Date: 2013-10-28
 * @Time: 下午12:37:43
 * @author Tom.Cai
 */
public class CustomDialog extends Dialog {
    private EditText editText1,editText2;
    private Button positiveButton, negativeButton;

    public CustomDialog(Context context) {
        super(context);
        setCustomDialog();
    }


    private void setCustomDialog() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_normal_layout, null);
        editText1 = (EditText) mView.findViewById(R.id.ipAddress);
        editText2 = (EditText) mView.findViewById(R.id.port);
        positiveButton = (Button) mView.findViewById(R.id.positiveButton);
        negativeButton = (Button) mView.findViewById(R.id.negativeButton);
        super.setContentView(mView);


    }
    public void setSettingText(String ip,String port){
        editText1.setText(ip);
        editText2.setText(port);
    }
    public String getIpAddress(){
        return editText1.getText()+"";
    }
    public String getPort(){
        return editText2.getText()+"";
    }
    @Override
    public void setContentView(int layoutResID) {
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
    }

    @Override
    public void setContentView(View view) {
    }

    /**
     * 确定键监听器
     * @param listener
     */
    public void setOnPositiveListener(View.OnClickListener listener){
        positiveButton.setOnClickListener(listener);
    }
    /**
     * 取消键监听器
     * @param listener
     */
    public void setOnNegativeListener(View.OnClickListener listener){
        negativeButton.setOnClickListener(listener);
    }
}