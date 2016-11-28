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
import com.youmengna.byr.adapter.InboxArrayAdapter;
import com.youmengna.byr.bean.Mail;
import com.youmengna.byr.bean.Mailbox;
import com.youmengna.byr.sdk.api.MailApi;
import com.youmengna.byr.sdk.auth.Oauth2AccessToken;
import com.youmengna.byr.sdk.exception.BBSException;
import com.youmengna.byr.sdk.net.RequestListener;
import com.youmengna.byr.sdk.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import cn.byr.bbs.sdkdemo.R;

public class InboxActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private static final String TAG = InboxActivity.class.getName();
    private InboxArrayAdapter inboxArrayAdapter;
    private ListView listView;
    protected static final int SUCEESS = 0;

    private int pagetotal = 0;
    private int curpage = 1;
    private TextView firstPage;
    private TextView prePage;
    private TextView nextPage;
    private TextView lastPage;
    private TextView pageOfSum;
    private EditText pageEdit;
    private TextView pageGo;
    private MailApi mMailApi;
    private Oauth2AccessToken mAccessToken;
    private Mailbox MailboxInfo;
    private ArrayList<Mail> maillist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        //设置AnctionBar
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        mMailApi = new MailApi(mAccessToken);

        //获取MailboxInfo信息
        Intent intent = getIntent();
        MailboxInfo = (Mailbox) intent.getSerializableExtra("MailboxInfo");
        List<Mail> mailListInfo = MailboxInfo.getMail();
        maillist.addAll(mailListInfo);
        listView = (ListView) findViewById(R.id.inboxListView);
        inboxArrayAdapter = new InboxArrayAdapter(InboxActivity.this, maillist);
        listView.setAdapter(inboxArrayAdapter);
        listView.setOnItemClickListener(InboxActivity.this);
        listView.setOnItemLongClickListener(InboxActivity.this);


        firstPage = (TextView) findViewById(R.id.firstPage);
        prePage = (TextView) findViewById(R.id.prePage);
        nextPage = (TextView) findViewById(R.id.nextPage);
        lastPage = (TextView) findViewById(R.id.lastPage);
        pageOfSum = (TextView) findViewById(R.id.pageOfSum);
        pageEdit = (EditText) findViewById(R.id.pageEdit);
        pageGo = (TextView) findViewById(R.id.pageGo);

        initPage();

        firstPage.setOnClickListener(this);
        prePage.setOnClickListener(this);
        nextPage.setOnClickListener(this);
        lastPage.setOnClickListener(this);
        pageGo.setOnClickListener(this);
    }

    void initPage() {
        //初始化页数
        pagetotal = MailboxInfo.getPagination().getPage_all_count();
        curpage = MailboxInfo.getPagination().getPage_current_count();
        firstPage.setEnabled(false);
        prePage.setEnabled(false);
        if (pagetotal <= 1) {
            nextPage.setEnabled(false);
            lastPage.setEnabled(false);
        } else {
            nextPage.setEnabled(true);
            lastPage.setEnabled(true);
        }
        pageOfSum.setText(curpage + "/" + pagetotal);
    }

    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i(TAG, response);
                Gson gson = new Gson();
                Mailbox mMailbox = gson.fromJson(response, Mailbox.class);
                List<Mail> mailInfo= mMailbox.getMail();
                handler.obtainMessage(SUCEESS, mailInfo).sendToTarget();
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
                maillist.clear();
                maillist.addAll((List<Mail>) msg.obj);
                inboxArrayAdapter.notifyDataSetChanged();
            }
        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.firstPage:
                curpage = 1;
                firstPage.setEnabled(false);
                prePage.setEnabled(false);
                if (pagetotal > 1) {
                    nextPage.setEnabled(true);
                    lastPage.setEnabled(true);
                }
                if (mAccessToken != null && mAccessToken.isSessionValid()) {
                    mMailApi.inbox(curpage, mListener);
                }
                break;
            case R.id.prePage:
                curpage--;
                if (curpage == 1) {
                    firstPage.setEnabled(false);
                    prePage.setEnabled(false);
                }
                if (pagetotal > 1) {
                    nextPage.setEnabled(true);
                    lastPage.setEnabled(true);
                }
                if (mAccessToken != null && mAccessToken.isSessionValid()) {
                    mMailApi.inbox(curpage, mListener);
                }
                break;
            case R.id.nextPage:
                curpage++;
                if (curpage == pagetotal) {
                    nextPage.setEnabled(false);
                    lastPage.setEnabled(false);
                }
                firstPage.setEnabled(true);
                prePage.setEnabled(true);
                if (mAccessToken != null && mAccessToken.isSessionValid()) {
                    mMailApi.inbox(curpage, mListener);
                }
                break;
            case R.id.lastPage:
                curpage = pagetotal;
                nextPage.setEnabled(false);
                lastPage.setEnabled(false);
                firstPage.setEnabled(true);
                prePage.setEnabled(true);
                if (mAccessToken != null && mAccessToken.isSessionValid()) {
                    mMailApi.inbox(curpage, mListener);
                }
                break;
            case R.id.pageGo:
                curpage=Integer.parseInt(pageEdit.getText().toString());
                pageEdit.setText("");
                if(curpage<1||curpage>pagetotal){
                    Toast.makeText(InboxActivity.this,"请输入正确的页码！",Toast.LENGTH_SHORT).show();
                }else if (mAccessToken != null && mAccessToken.isSessionValid()) {
                    mMailApi.inbox(curpage, mListener);
                }
                break;
        }
        pageOfSum.setText(curpage + "/" + pagetotal);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.inbox_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.InboxWriteMail:
                Intent intent = new Intent(InboxActivity.this, SendMailActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Mail mail = maillist.get(position);
        int index = mail.getIndex();
        Intent intent = new Intent(InboxActivity.this, MailDetailActivity.class);
        intent.putExtra("index", index);
        startActivity(intent);
    }

    private RequestListener DeleteListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i(TAG, response);
                Toast.makeText(InboxActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onException(BBSException e) {
            LogUtil.e(TAG, e.getMessage());
        }
    };

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        Mail mail = maillist.get(position);
        final int index = mail.getIndex();
        String[] mItems = {"设置已读", "删除信件"};
        AlertDialog.Builder builder = new AlertDialog.Builder(InboxActivity.this);
        builder.setItems(mItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {

                } else {
                    mMailApi.delete("inbox", index, DeleteListener);
                }
            }
        });
        builder.create();
        builder.show();
        return true;
    }
}
