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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.Gson;
import com.youmengna.byr.bean.Article;
import com.youmengna.byr.bean.Exception;
import com.youmengna.byr.bean.Refer;
import com.youmengna.byr.sdk.api.ArticleApi;
import com.youmengna.byr.sdk.auth.Oauth2AccessToken;
import com.youmengna.byr.sdk.exception.BBSException;
import com.youmengna.byr.sdk.net.RequestListener;
import com.youmengna.byr.sdk.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.byr.bbs.sdkdemo.R;

public class ReplyMeDetailAty extends Activity implements View.OnClickListener{
    private static final String TAG = ReplyMeDetailAty.class.getName();
    protected static final int SUCEESS = 0;
    private Oauth2AccessToken mAccessToken;
    private ArticleApi mArticleApi;

    private LinearLayout ReplyMeDetailAbove;
    private NetworkImageView ReplyMeDetailImg;
    private RelativeLayout ReplyMeDetailRela;
    private TextView ReplyMeDetailName;
    private TextView ReplyMeDetailDate;
    private TextView ReplyMeDetailTitle;
    private TextView ReplyMeDetailContent;
    private LinearLayout ReplyMeDetailForwardLineLayout;
    private LinearLayout ReplyMeDetailForward;
    private LinearLayout ReplyMeDetailReply;
    private LinearLayout ReplyMeDetailTraceability;
    private LinearLayout ReplyMeDetailForwarLL2;
    private EditText ReplyMeDetailForwardEdit;
    private TextView ReplyMeDetailForwardSend;

    private String board_name;
    private int referArticle_id;
    private Article mArticle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_me_detail_aty);
        initView();

        Refer refer= (Refer) getIntent().getSerializableExtra("refer");
        board_name=refer.getBoard_name();
        referArticle_id=refer.getId();

        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        mArticleApi=new ArticleApi(mAccessToken);
        if(mAccessToken!=null&&mAccessToken.isSessionValid()){
            mArticleApi.showArticle(board_name,referArticle_id,mListener);
        }else {
            Toast.makeText(ReplyMeDetailAty.this, R.string.bbsSDK_token_empty, Toast.LENGTH_LONG).show();
        }
    }
    public void initView(){
        //设置ActionBar
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        ReplyMeDetailRela= (RelativeLayout) findViewById(R.id.ReplyMeDetailRela);
        ReplyMeDetailAbove= (LinearLayout) findViewById(R.id.ReplyMeDetailAbove);
        ReplyMeDetailForwardLineLayout= (LinearLayout) findViewById(R.id.ReplyMeDetailForwardLineLayout);
        ReplyMeDetailForward= (LinearLayout) findViewById(R.id.ReplyMeDetailForward);
        ReplyMeDetailReply= (LinearLayout) findViewById(R.id.ReplyMeDetailReply);
        ReplyMeDetailTraceability= (LinearLayout) findViewById(R.id.ReplyMeDetailTraceability);
        ReplyMeDetailForwarLL2= (LinearLayout) findViewById(R.id.ReplyMeDetailForwarLL2);
        ReplyMeDetailImg= (NetworkImageView) findViewById(R.id.ReplyMeDetailImg);
        ReplyMeDetailForwardEdit= (EditText) findViewById(R.id.ReplyMeDetailForwardEdit);
        ReplyMeDetailName= (TextView) findViewById(R.id.ReplyMeDetailName);
        ReplyMeDetailDate= (TextView) findViewById(R.id.ReplyMeDetailDate);
        ReplyMeDetailTitle= (TextView) findViewById(R.id.ReplyMeDetailTitle);
        ReplyMeDetailContent= (TextView) findViewById(R.id.ReplyMeDetailContent);
        ReplyMeDetailForwardSend= (TextView) findViewById(R.id.ReplyMeDetailForwardSend);

        //注册监听
        ReplyMeDetailForward.setOnClickListener(this);
        ReplyMeDetailReply.setOnClickListener(this);
        ReplyMeDetailTraceability.setOnClickListener(this);
        ReplyMeDetailForwardSend.setOnClickListener(this);
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
        switch (v.getId()){
            case R.id.ReplyMeDetailForward:
                ReplyMeDetailForwardLineLayout.setVisibility(View.INVISIBLE);
                ReplyMeDetailForwarLL2.setVisibility(View.VISIBLE);
                break;
            case R.id.ReplyMeDetailForwardSend:
                String forward = ReplyMeDetailForwardEdit.getText().toString();
                if ("".equals(forward)) {
                    Toast.makeText(ReplyMeDetailAty.this, "输入的用户ID错误！", Toast.LENGTH_SHORT).show();
                } else if (mAccessToken != null && mAccessToken.isSessionValid()) {
                    mArticleApi.forward(board_name,referArticle_id,forward,ForwardListener);
                    ReplyMeDetailForwardEdit.setText("");
                    ReplyMeDetailForwarLL2.setVisibility(View.INVISIBLE);
                    ReplyMeDetailForwardLineLayout.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(ReplyMeDetailAty.this, R.string.bbsSDK_token_empty, Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.ReplyMeDetailReply:
                Intent replyIntent = new Intent(ReplyMeDetailAty.this, ReplyArticle.class);
                replyIntent.putExtra("boardname",mArticle.getBoard_name());
                replyIntent.putExtra("ReplyArticle_Id",mArticle.getId());
                replyIntent.putExtra("topTenDatail_Title",mArticle.getTitle());
                replyIntent.putExtra("topTenDatail_Content",mArticle.getContent());
                startActivity(replyIntent);
                break;
            case R.id.ReplyMeDetailTraceability:
                Intent intent=new Intent(ReplyMeDetailAty.this,TopTenDetail.class);
                intent.putExtra("article",mArticle);
                startActivity(intent);
                break;
        }
    }

    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i(TAG, response);
                Gson gson = new Gson();
                Article article = gson.fromJson(response, Article.class);
                handler.obtainMessage(SUCEESS, article).sendToTarget();
            }
        }

        @Override
        public void onException(BBSException e) {
            Gson gson=new Gson();
            Exception exception=gson.fromJson(e.getMessage(), Exception.class);
            finish();
            Toast.makeText(ReplyMeDetailAty.this,exception.getMsg(),Toast.LENGTH_SHORT).show();
            LogUtil.e(TAG, e.getMessage());
        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SUCEESS) {
                mArticle = (Article) msg.obj;
                ReplyMeDetailImg.setImageUrl(mArticle.getUser().getFace_url(),ByrApplication.imageLoader);
                ReplyMeDetailContent.setText(mArticle.getContent());
                ReplyMeDetailName.setText(mArticle.getUser().getId());
                Date d = new Date((long) mArticle.getPost_time() * 1000);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                ReplyMeDetailDate.setText(sdf.format(d));
                ReplyMeDetailTitle.setText(mArticle.getTitle());
            }
        }
    };

    private RequestListener ForwardListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i(TAG, response);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    Toast.makeText(ReplyMeDetailAty.this, "转寄成功", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (jsonObject.has("code")) {
                    Toast.makeText(ReplyMeDetailAty.this, "No such id.", Toast.LENGTH_LONG).show();
                }

            }
        }

        @Override
        public void onException(BBSException e) {
            LogUtil.e(TAG, e.getMessage());
        }
    };
}
