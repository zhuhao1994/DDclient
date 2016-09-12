package zhu.com.ddclient.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import android.view.View;

import android.widget.Toast;



import zhu.com.ddclient.R;

public class LoginActivity extends AppCompatActivity {
    private CustomDialog dialog;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
   // private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void login(View v) {
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, MainActivity.class);
        startActivity(intent);

    }

    public void dialogBtnClick(View v) {
        dialog = new CustomDialog(LoginActivity.this);
        dialog.setTitle("配置选项");
//        EditText editText = dialog.get
        dialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dosomething youself
                String string = dialog.getSettingText();
                Toast.makeText(getApplicationContext(), string, Toast.LENGTH_LONG).show();
                dialog.dismiss();

            }
        });
        dialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }


}
