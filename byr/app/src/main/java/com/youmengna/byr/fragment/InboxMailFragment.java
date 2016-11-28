package com.youmengna.byr.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.youmengna.byr.AccessTokenKeeper;
import com.youmengna.byr.MailDetailActivity;
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

public class InboxMailFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private String mailtype;
    private static final String TAG = InboxMailFragment.class.getName();
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
    private ArrayList<Mail> maillist = new ArrayList<>();
    private InboxArrayAdapter inboxArrayAdapter;
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.activity_inbox, container, false);
        Bundle bundle1 = getArguments();
        mailtype = bundle1.getString("boxtype");
        mAccessToken = AccessTokenKeeper.readAccessToken(getContext());
        mMailApi = new MailApi(mAccessToken);
        mMailApi.mailList(mailtype,1,firstListener);
        initView(mView);
        return mView;
    }

    private void initView(View view) {
        listView = (ListView) view.findViewById(R.id.inboxListView);

        listView.setOnItemClickListener(InboxMailFragment.this);
        listView.setOnItemLongClickListener(InboxMailFragment.this);


        firstPage = (TextView) view.findViewById(R.id.firstPage);
        prePage = (TextView) view.findViewById(R.id.prePage);
        nextPage = (TextView) view.findViewById(R.id.nextPage);
        lastPage = (TextView) view.findViewById(R.id.lastPage);
        pageOfSum = (TextView) view.findViewById(R.id.pageOfSum);
        pageEdit = (EditText) view.findViewById(R.id.pageEdit);
        pageGo = (TextView) view.findViewById(R.id.pageGo);

        firstPage.setOnClickListener(this);
        prePage.setOnClickListener(this);
        nextPage.setOnClickListener(this);
        lastPage.setOnClickListener(this);
        pageGo.setOnClickListener(this);
    }


    /**
     * 初始化数据
     */
    private RequestListener firstListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i(TAG, response);
                Gson gson = new Gson();
                Mailbox mMailbox = gson.fromJson(response, Mailbox.class);
                firsthandler.obtainMessage(SUCEESS, mMailbox).sendToTarget();
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
                pagetotal=((Mailbox)(msg.obj)).getPagination().getPage_all_count();
                curpage=((Mailbox)(msg.obj)).getPagination().getPage_current_count();
                maillist.addAll(((Mailbox)(msg.obj)).getMail());
                inboxArrayAdapter = new InboxArrayAdapter(getContext(), maillist);
                listView.setAdapter(inboxArrayAdapter);
                initPage();
            }
        }
    };

    void initPage() {
        //初始化页数
        /*pagetotal = MailboxInfo.getPagination().getPage_all_count();
        curpage = MailboxInfo.getPagination().getPage_current_count();*/
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
                    mMailApi.mailList(mailtype,curpage, mListener);
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
                    mMailApi.mailList(mailtype,curpage, mListener);
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
                    mMailApi.mailList(mailtype,curpage, mListener);
                }
                break;
            case R.id.lastPage:
                curpage = pagetotal;
                nextPage.setEnabled(false);
                lastPage.setEnabled(false);
                firstPage.setEnabled(true);
                prePage.setEnabled(true);
                if (mAccessToken != null && mAccessToken.isSessionValid()) {
                    mMailApi.mailList(mailtype,curpage, mListener);
                }
                break;
            case R.id.pageGo:
                curpage=Integer.parseInt(pageEdit.getText().toString());
                pageEdit.setText("");
                if(curpage<1||curpage>pagetotal){
                    Toast.makeText(getContext(),"请输入正确的页码！",Toast.LENGTH_SHORT).show();
                }else if (mAccessToken != null && mAccessToken.isSessionValid()) {
                    mMailApi.mailList(mailtype,curpage, mListener);
                    pageOfSum.setText(curpage + "/" + pagetotal);
                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Mail mail = maillist.get(position);
        //设置已读
        if(!mail.isIs_read()){
            mail.setIs_read(true);
            inboxArrayAdapter.notifyDataSetChanged(); //刷新列表
        }
        int index = mail.getIndex();
        Intent intent = new Intent(getContext(), MailDetailActivity.class);
        intent.putExtra("index", index);
        intent.putExtra("boxtype",mailtype);
        startActivity(intent);
    }

    private RequestListener DeleteListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i(TAG, response);
                Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                DeleteHandler.obtainMessage(SUCEESS).sendToTarget();
            }
        }

        @Override
        public void onException(BBSException e) {
            LogUtil.e(TAG, e.getMessage());
        }
    };

    Handler DeleteHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SUCEESS) {
                mMailApi.mailList(mailtype,curpage, mListener);
            }
        }
    };

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        final Mail mail = maillist.get(position);
        final int index = mail.getIndex();
        String[] mItems = {"删除信件"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setItems(mItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    mMailApi.delete(mailtype, index, DeleteListener);
                }
            }
        });
        builder.create();
        builder.show();
        return true;
    }
}

