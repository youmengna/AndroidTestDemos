package com.youmengna.byr;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.youmengna.byr.sdk.api.MailApi;
import com.youmengna.byr.sdk.auth.Oauth2AccessToken;
import com.youmengna.byr.sdk.exception.BBSException;
import com.youmengna.byr.sdk.net.RequestListener;
import com.youmengna.byr.sdk.utils.LogUtil;

import cn.byr.bbs.sdkdemo.R;

public class SendMailActivity extends Activity {
    private EditText recipientID;
    private EditText mailtheme;
    private EditText mailcontent;

    private MailApi mMailApi;
    private Oauth2AccessToken mAccessToken;
    private static final String TAG = SendMailActivity.class.getName();
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i(TAG, response);
                Toast.makeText(SendMailActivity.this,"发送成功！", Toast.LENGTH_LONG).show();
            }
        }
        @Override
        public void onException(BBSException e) {
            // TODO Auto-generated method stub
            LogUtil.e(TAG, e.getMessage());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_mail);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        mMailApi = new MailApi(mAccessToken);
        initview();
    }

    void initview(){
        recipientID= (EditText) findViewById(R.id.recipientID);
        mailtheme= (EditText) findViewById(R.id.mailtheme);
        mailcontent= (EditText) findViewById(R.id.mailcontent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.sendmail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.InboxSendMail:
                String mRecipientID=recipientID.getText().toString();
                String mMailtheme=mailtheme.getText().toString();
                String mMailcontent=mailcontent.getText().toString();
                if("".equals(mRecipientID)||"".equals(mMailcontent)){
                    Toast.makeText(SendMailActivity.this,"请检查信息是否填写完整",Toast.LENGTH_SHORT).show();
                }
                if(mAccessToken != null && mAccessToken.isSessionValid()) {
                    mMailApi.send(mRecipientID, mMailtheme, mMailcontent, 1, 1, mListener);
                }
                recipientID.setText("");
                mailtheme.setText("");
                mailcontent.setText("");
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
