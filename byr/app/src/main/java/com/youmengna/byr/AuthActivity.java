package com.youmengna.byr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.youmengna.byr.sdk.auth.BBSAuth;
import com.youmengna.byr.sdk.auth.BBSAuthListener;
import com.youmengna.byr.sdk.auth.Oauth2AccessToken;
import com.youmengna.byr.sdk.exception.BBSException;

import java.text.SimpleDateFormat;

import cn.byr.bbs.sdkdemo.R;

public class AuthActivity extends Activity implements View.OnClickListener {

    private static final String TAG = AuthActivity.class.getName();
    protected static final int SUCEESS = 0;
    private Button obtain_token_via_signature;
    /**
     * show info after authorization AccessToken
     */
    //private TextView mTokenText;

    /**
     * web authorization
     */
    private BBSAuth mBBSAuth;

    /**
     * contains "access_token"，"expires_in"，"refresh_token". "refresh_token_expires_in" ...
     */
    private Oauth2AccessToken mAccessToken;

    /**
     * @see {@link Activity#onCreate}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        // Token View，
        //mTokenText = (TextView) findViewById(R.id.token_text_view);
        mBBSAuth = new BBSAuth(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);

        // get token info from sharedpreference
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        if (mAccessToken.isSessionValid()) {
            updateTokenView(true);
            Intent home = new Intent(AuthActivity.this, MainActivity.class);
            startActivity(home);
            AuthActivity.this.finish();
        }

        initView();
    }

    void initView() {
        obtain_token_via_signature = (Button) findViewById(R.id.obtain_token_via_signature);
        obtain_token_via_signature.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.obtain_token_via_signature:
                mBBSAuth.authorize(new AuthListener());
                break;
        }
    }


    /**
     * stored token informations(token OR error code)  after handle redirect url
     */
    class AuthListener implements BBSAuthListener {

        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {
                // show it
                updateTokenView(false);

                // store Token into SharedPreferences
                AccessTokenKeeper.writeAccessToken(AuthActivity.this, mAccessToken);
                /**
                 * 跳转到登录页面
                 */
                AuthActivity.this.finish();
                Intent login = new Intent(AuthActivity.this, MainActivity.class);
                startActivity(login);
            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                String code = values.getString("code");
                String message = getString(R.string.bbsDemo_oauth_failed);
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code: " + code;
                }
                Toast.makeText(AuthActivity.this, message, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onCancel() {
            Toast.makeText(AuthActivity.this,
                    R.string.bbsDemo_oauth_canceled, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onException(BBSException e) {
            Toast.makeText(AuthActivity.this,
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * show Token info
     *
     * @param hasExisted 配置文件中是否已存在 token 信息并且合法
     */
    private void updateTokenView(boolean hasExisted) {
        String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(
                new java.util.Date(mAccessToken.getExpiresTime()));
        String format = getString(R.string.bbsDemo_token_to_string_format_1);
        //mTokenText.setText(String.format(format, mAccessToken.getAccessToken(), date, mAccessToken.getRefreshToken()));

        String message = String.format(format, mAccessToken.getAccessToken(), date, mAccessToken.getRefreshToken());
        if (hasExisted) {
            message = getString(R.string.bbsDemo_token_has_existed) + "\n" + message;
        }
        //mTokenText.setText(message);
    }

}