package com.youmengna.byr;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.youmengna.byr.adapter.ReplyMeAdapter;
import com.youmengna.byr.bean.Refer;
import com.youmengna.byr.bean.ReplyMeReturn;
import com.youmengna.byr.sdk.api.ReferApi;
import com.youmengna.byr.sdk.auth.Oauth2AccessToken;
import com.youmengna.byr.sdk.exception.BBSException;
import com.youmengna.byr.sdk.net.RequestListener;
import com.youmengna.byr.sdk.utils.DialogUtil;
import com.youmengna.byr.sdk.utils.LogUtil;

import java.util.ArrayList;

import cn.byr.bbs.sdkdemo.R;

public class ReplyMeActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private static final String TAG = ReplyMeActivity.class.getName();
    protected static final int SUCEESS = 0;
    private String referType;
    private static int item_page_count = 10;
    private int pagetotal = 0;
    private int curpage = 1;
    private ReferApi mReferApi;
    private Oauth2AccessToken mAccessToken;
    private ListView replyMeListView;
    private TextView replyMeFirstPage;
    private TextView replyMePrePage;
    private TextView replyMeNextPage;
    private TextView replyMeLastPage;
    private TextView replyMePageOfSum;
    private EditText replyMePageEdit;
    private TextView replyMePageGo;
    private ReplyMeAdapter replyMeAdapter;
    private ArrayList<Refer> referArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_me);
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        referType = getIntent().getStringExtra("refertype");
        mReferApi = new ReferApi(mAccessToken);
        if (mAccessToken != null && mAccessToken.isSessionValid()) {
            mReferApi.refer(referType, item_page_count, 1, firstListener);
        }
        initView();
    }

    public void initView() {
        //设置ActionBar
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        if ("reply".equals(referType)) {
            actionBar.setTitle("回复我的文章");
        } else if ("at".equals(referType)) {
            actionBar.setTitle("@我的文章");
        }
        replyMeListView = (ListView) findViewById(R.id.replyMeListView);
        replyMeListView.setOnItemClickListener(ReplyMeActivity.this);
        replyMeListView.setOnItemLongClickListener(ReplyMeActivity.this);
        replyMeFirstPage = (TextView) findViewById(R.id.replyMeFirstPage);
        replyMePrePage = (TextView) findViewById(R.id.replyMePrePage);
        replyMeNextPage = (TextView) findViewById(R.id.replyMeNextPage);
        replyMeLastPage = (TextView) findViewById(R.id.replyMeLastPage);
        replyMePageOfSum = (TextView) findViewById(R.id.replyMePageOfSum);
        replyMePageEdit = (EditText) findViewById(R.id.replyMePageEdit);
        replyMePageGo = (TextView) findViewById(R.id.replyMePageGo);

        replyMeFirstPage.setOnClickListener(this);
        replyMePrePage.setOnClickListener(this);
        replyMeNextPage.setOnClickListener(this);
        replyMeLastPage.setOnClickListener(this);
        replyMePageGo.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.replyme_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.SetRead:
                //mReferApi.readAll(referType,setReadListener);
                alertInfo();
                return true;
            case R.id.replyMeRefresh:
                mReferApi.refer(referType, item_page_count, curpage, mListener);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.replyMeFirstPage:
                curpage = 1;
                replyMeFirstPage.setEnabled(false);
                replyMePrePage.setEnabled(false);
                if (pagetotal > 1) {
                    replyMeNextPage.setEnabled(true);
                    replyMeLastPage.setEnabled(true);
                }
                if (mAccessToken != null && mAccessToken.isSessionValid()) {
                    mReferApi.refer(referType, item_page_count, curpage, mListener);
                }
                break;
            case R.id.replyMePrePage:
                curpage--;
                if (curpage == 1) {
                    replyMeFirstPage.setEnabled(false);
                    replyMePrePage.setEnabled(false);
                }
                if (pagetotal > 1) {
                    replyMeNextPage.setEnabled(true);
                    replyMeLastPage.setEnabled(true);
                }
                if (mAccessToken != null && mAccessToken.isSessionValid()) {
                    mReferApi.refer(referType, item_page_count, curpage, mListener);
                }
                break;
            case R.id.replyMeNextPage:
                curpage++;
                if (curpage == pagetotal) {
                    replyMeNextPage.setEnabled(false);
                    replyMeLastPage.setEnabled(false);
                }
                replyMeFirstPage.setEnabled(true);
                replyMePrePage.setEnabled(true);
                if (mAccessToken != null && mAccessToken.isSessionValid()) {
                    mReferApi.refer(referType, item_page_count, curpage, mListener);
                }
                break;
            case R.id.replyMeLastPage:
                curpage = pagetotal;
                replyMeNextPage.setEnabled(false);
                replyMeLastPage.setEnabled(false);
                replyMeFirstPage.setEnabled(true);
                replyMePrePage.setEnabled(true);
                if (mAccessToken != null && mAccessToken.isSessionValid()) {
                    mReferApi.refer(referType, item_page_count, curpage, mListener);
                }
                break;
            case R.id.replyMePageGo:
                curpage = Integer.parseInt(replyMePageEdit.getText().toString());
                replyMePageEdit.setText("");
                if (curpage < 1 || curpage > pagetotal) {
                    Toast.makeText(ReplyMeActivity.this, "请输入正确的页码！", Toast.LENGTH_SHORT).show();
                } else if (mAccessToken != null && mAccessToken.isSessionValid()) {
                    mReferApi.refer(referType, item_page_count, curpage, mListener);
                }
                break;
        }
        replyMePageOfSum.setText(curpage + "/" + pagetotal);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Refer refer = referArrayList.get(position);
        //设置已读
        int index = refer.getIndex();
        if (!refer.isIs_read()) {
            mReferApi.setRead(referType, index, setReadListener);
        }
        Intent intent = new Intent(ReplyMeActivity.this, ReplyMeDetailAty.class);
        intent.putExtra("refer", refer);
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Refer refer = referArrayList.get(position);
        final int index = refer.getIndex();
        String[] mItems = {"设置已读", "删除提醒"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(mItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    mReferApi.setRead(referType, index, setReadListener);
                } else {
                    mReferApi.delete(referType, index, DeleteListener);
                }
            }
        });
        builder.create();
        builder.show();
        return true;
    }

    private RequestListener setReadListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i(TAG, response);
                refreshhandler.obtainMessage(SUCEESS).sendToTarget();
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
                refreshhandler.obtainMessage(SUCEESS).sendToTarget();
                Toast.makeText(ReplyMeActivity.this, "删除成功！", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onException(BBSException e) {
            LogUtil.e(TAG, e.getMessage());
        }
    };
    Handler refreshhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SUCEESS) {
                mReferApi.refer(referType, item_page_count, curpage, mListener);
            }
        }
    };


    /**
     * 初始化数据
     */
    private RequestListener firstListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i(TAG, response);
                Gson gson = new Gson();
                ReplyMeReturn replyMeReturn = gson.fromJson(response, ReplyMeReturn.class);
                firsthandler.obtainMessage(SUCEESS, replyMeReturn).sendToTarget();
            }
        }

        @Override
        public void onException(BBSException e) {
            LogUtil.e(TAG, e.getMessage());
        }
    };

    Handler firsthandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SUCEESS) {
                pagetotal = ((ReplyMeReturn) (msg.obj)).getPagination().getPage_all_count();
                curpage = ((ReplyMeReturn) (msg.obj)).getPagination().getPage_current_count();
                referArrayList.addAll(((ReplyMeReturn) (msg.obj)).getArticle());
                replyMeAdapter = new ReplyMeAdapter(ReplyMeActivity.this, referArrayList);
                replyMeListView.setAdapter(replyMeAdapter);
                initPage();
            }
        }
    };

    void initPage() {
        //初始化页数
        /*pagetotal = MailboxInfo.getPagination().getPage_all_count();
        curpage = MailboxInfo.getPagination().getPage_current_count();*/
        replyMeFirstPage.setEnabled(false);
        replyMePrePage.setEnabled(false);
        if (pagetotal <= 1) {
            replyMeNextPage.setEnabled(false);
            replyMeLastPage.setEnabled(false);
        } else {
            replyMeNextPage.setEnabled(true);
            replyMeLastPage.setEnabled(true);
        }
        replyMePageOfSum.setText(curpage + "/" + pagetotal);
    }

    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i(TAG, response);
                Gson gson = new Gson();
                ReplyMeReturn replyMeReturn = gson.fromJson(response, ReplyMeReturn.class);
                handler.obtainMessage(SUCEESS, replyMeReturn).sendToTarget();
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
                referArrayList.clear();
                referArrayList.addAll(((ReplyMeReturn) (msg.obj)).getArticle());
                replyMeAdapter.notifyDataSetChanged();
            }
        }
    };

    void alertInfo() {
        DialogUtil.createNormalDialog(ReplyMeActivity.this, 0, "提示", "确认将所有提醒设为已读吗？", "确 定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mReferApi.readAll(referType, setReadListener);
            }
        }, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }
}
