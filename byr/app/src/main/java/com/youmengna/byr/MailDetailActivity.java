package com.youmengna.byr;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.Gson;
import com.youmengna.byr.bean.Mail;
import com.youmengna.byr.sdk.api.MailApi;
import com.youmengna.byr.sdk.auth.Oauth2AccessToken;
import com.youmengna.byr.sdk.exception.BBSException;
import com.youmengna.byr.sdk.net.RequestListener;
import com.youmengna.byr.sdk.utils.LogUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.byr.bbs.sdkdemo.R;


public class MailDetailActivity extends Activity implements View.OnClickListener {
    private static final String TAG = MailDetailActivity.class.getName();
    protected static final int SUCEESS = 0;
    private MailApi mMailApi;
    private Oauth2AccessToken mAccessToken;
    private Mail MailInfoShow;
    private String mailtype;
    private NetworkImageView networkImageView;

    private LinearLayout MailDetailForward;
    private LinearLayout MailDetailReply;
    private LinearLayout MailDetailDelete;

    private LinearLayout ForwarLL2;
    private LinearLayout ForwardLineLayout;

    private EditText ForwardEdit;
    private TextView ForwardSend;

    private TextView MailDetailName;
    private TextView MailDetailDate;
    private TextView MailDetailTitle;
    private TextView MailDetailContent;

    private int index;
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i(TAG, response);
                Gson gson = new Gson();
                Mail Mailinfo = gson.fromJson(response, Mail.class);
                handler.obtainMessage(SUCEESS, Mailinfo).sendToTarget();
            }
        }

        @Override
        public void onException(BBSException e) {
            LogUtil.e(TAG, e.getMessage());
        }
    };
    private RequestListener ForwardListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i(TAG, response);
                Toast.makeText(MailDetailActivity.this, "转寄成功", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onException(BBSException e) {
            LogUtil.e(TAG, e.getMessage());
        }
    };

    private RequestListener DeleteListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i(TAG, response);
                Toast.makeText(MailDetailActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onException(BBSException e) {
            LogUtil.e(TAG, e.getMessage());
        }
    };
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SUCEESS) {
                MailInfoShow = (Mail) msg.obj;

                networkImageView= (NetworkImageView) findViewById(R.id.MailDetailImg);
                networkImageView.setImageUrl(MailInfoShow.getUser().getFace_url(),ByrApplication.imageLoader);
                MailDetailContent = (TextView) findViewById(R.id.MailDetailContent);
                MailDetailContent.setText(MailInfoShow.getContent());

                MailDetailName = (TextView) findViewById(R.id.MailDetailName);
                MailDetailName.setText(MailInfoShow.getUser().getId());

                MailDetailDate = (TextView) findViewById(R.id.MailDetailDate);
                Date d = new Date((long) MailInfoShow.getPost_time() * 1000);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                MailDetailDate.setText(sdf.format(d));

                MailDetailTitle = (TextView) findViewById(R.id.MailDetailTitle);
                MailDetailTitle.setText(MailInfoShow.getTitle());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_detail);
        initview();

        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        mMailApi = new MailApi(mAccessToken);
        /**
         * * 通过Index，获取指定信件信息
         * */
        Intent intent = getIntent();
        index = intent.getIntExtra("index", 0);
        mailtype=intent.getStringExtra("boxtype");
        if (mAccessToken != null && mAccessToken.isSessionValid()) {
            mMailApi.showMail(mailtype, index, mListener);
        } else {
            Toast.makeText(MailDetailActivity.this, R.string.bbsSDK_token_empty, Toast.LENGTH_LONG).show();
        }
    }

    void initview() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        MailDetailForward = (LinearLayout) findViewById(R.id.MailDetailForward);
        MailDetailReply = (LinearLayout) findViewById(R.id.MailDetailReply);
        MailDetailDelete = (LinearLayout) findViewById(R.id.MailDetailDelete);
        ForwarLL2 = (LinearLayout) findViewById(R.id.ForwarLL2);
        ForwardLineLayout = (LinearLayout) findViewById(R.id.ForwardLineLayout);
        ForwardEdit = (EditText) findViewById(R.id.ForwardEdit);
        ForwardSend = (TextView) findViewById(R.id.ForwardSend);

        MailDetailForward.setOnClickListener(this);
        MailDetailReply.setOnClickListener(this);
        MailDetailDelete.setOnClickListener(this);
        ForwardSend.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.MailDetailForward:
                ForwardLineLayout.setVisibility(View.INVISIBLE);
                ForwarLL2.setVisibility(View.VISIBLE);
                break;
            case R.id.ForwardSend:
                String forward = ForwardEdit.getText().toString();
                if ("".equals(forward)) {
                    Toast.makeText(MailDetailActivity.this, "输入的用户ID错误！", Toast.LENGTH_SHORT).show();
                } else if (mAccessToken != null && mAccessToken.isSessionValid()) {
                    mMailApi.forward(mailtype, index, forward, ForwardListener);
                    ForwardEdit.setText("");
                    ForwarLL2.setVisibility(View.INVISIBLE);
                    ForwardLineLayout.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(MailDetailActivity.this, R.string.bbsSDK_token_empty, Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.MailDetailReply:
                Intent replyIntent = new Intent(MailDetailActivity.this, ReplyMailActivity.class);
                replyIntent.putExtra("MailInfo",MailInfoShow);
                startActivityForResult(replyIntent,0);
                break;
            case R.id.MailDetailDelete:
                if(mAccessToken!=null&&mAccessToken.isSessionValid()){
                    mMailApi.delete(mailtype,index , DeleteListener);
                    Intent intent=new Intent(MailDetailActivity.this,MainActivity.class);
                    startActivity(intent);
                }
                break;
        }
    }
}
