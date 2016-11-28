package com.youmengna.byr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.youmengna.byr.bean.Mail;
import com.youmengna.byr.sdk.api.MailApi;
import com.youmengna.byr.sdk.auth.Oauth2AccessToken;
import com.youmengna.byr.sdk.exception.BBSException;
import com.youmengna.byr.sdk.net.RequestListener;
import com.youmengna.byr.sdk.utils.LogUtil;

import cn.byr.bbs.sdkdemo.R;

public class ReplyMailActivity extends Activity {
    private MailApi mMailApi;
    private Oauth2AccessToken mAccessToken;
    private Mail MailInfo;

    private EditText ReplyEdit;
    private static final String TAG = ReplyMailActivity.class.getName();
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i(TAG, response);
                Toast.makeText(ReplyMailActivity.this, "回复成功", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onException(BBSException e) {
            LogUtil.e(TAG, e.getMessage());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_mail);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.replymail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ReplyMail:
                Intent intent = getIntent();
                MailInfo = (Mail) intent.getSerializableExtra("MailInfo");
                ReplyEdit = (EditText) findViewById(R.id.ReplyEdit);
                mAccessToken = AccessTokenKeeper.readAccessToken(this);
                mMailApi = new MailApi(mAccessToken);
                if (mAccessToken != null && mAccessToken.isSessionValid()) {
                    mMailApi.reply("inbox", MailInfo.getIndex(), MailInfo.getTitle(), ReplyEdit.getText().toString(), 0, 1, mListener);
                    finish();
                } else {
                    Toast.makeText(ReplyMailActivity.this, R.string.bbsSDK_token_empty, Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
