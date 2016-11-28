package com.youmengna.byr;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.ListView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.youmengna.byr.adapter.TopTenDetailAdapter;
import com.youmengna.byr.bean.Article;
import com.youmengna.byr.bean.Attachment;
import com.youmengna.byr.bean.File;
import com.youmengna.byr.bean.Pagination;
import com.youmengna.byr.bean.Threads;
import com.youmengna.byr.sdk.api.ArticleApi;
import com.youmengna.byr.sdk.api.AttachmentApi;
import com.youmengna.byr.sdk.auth.Oauth2AccessToken;
import com.youmengna.byr.sdk.exception.BBSException;
import com.youmengna.byr.sdk.net.RequestListener;
import com.youmengna.byr.sdk.utils.LogUtil;
import java.util.ArrayList;
import java.util.List;
import cn.byr.bbs.sdkdemo.R;

public class TopTenDetail extends Activity {

    private static final String TAG = TopTenDetail.class.getName();
    protected static final int SUCEESS = 0;
    private ArticleApi mArticleApi;
    private AttachmentApi mAttachmentApi;
    private Attachment attachmentInfo;
    private List<File> filelist;//附件文件列表
    /**
     * 剩余空间大小
     */
    private String remain_space;
    /**
     * 剩余附件个数
     */
    private int remain_count;
    private Oauth2AccessToken mAccessToken;
    private TextView topTenDetail_Title;
    private TextView topTenDetail_BoardName;
    private TextView topTenDetail_ReplayCount;
    private PullToRefreshListView refresh_lv;
    private Article article;
    private Threads threadsInfo;
    private Pagination pagination;
    private TopTenDetailAdapter topTenDetailAdapter;
    //总页数
    private int page_all_count;
    //当前页数
    private int page_current_count=1;
    //每页元素个数
    private int item_page_count=10;
    //所有元素个数
    private int item_all_count=0;
    private ArrayList<Article> articleArrayList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_ten_detail);
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        mArticleApi = new ArticleApi(mAccessToken);
        mAttachmentApi=new AttachmentApi(mAccessToken);
        initView();
        setEventListener();
    }

    public void initView(){
        //设置AnctionBar
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        article= (Article) intent.getSerializableExtra("article");
        topTenDetail_Title= (TextView) findViewById(R.id.topTenDetail_Title);
        topTenDetail_BoardName= (TextView) findViewById(R.id.topTenDetail_BoardName);
        topTenDetail_ReplayCount= (TextView) findViewById(R.id.topTenDetail_ReplayCount);
        topTenDetail_Title.setText(article.getTitle());
        topTenDetail_BoardName.setText(article.getBoard_name());
        topTenDetail_ReplayCount.setText("回复("+article.getReply_count()+")");

        //设置下拉刷新，上拉加载控件
        refresh_lv= (PullToRefreshListView) findViewById(R.id.topTenDetail_listview);
        refresh_lv.setMode(PullToRefreshBase.Mode.BOTH);
        ILoadingLayout startLayout = refresh_lv.getLoadingLayoutProxy(true, false);
        startLayout.setPullLabel("正在下拉刷新...");
        startLayout.setRefreshingLabel("正在玩命加载中...");
        startLayout.setReleaseLabel("放开以刷新");

        ILoadingLayout endLayout = refresh_lv.getLoadingLayoutProxy(false, true);
        endLayout.setPullLabel("正在上拉刷新...");
        endLayout.setRefreshingLabel("正在玩命加载中...");
        endLayout.setReleaseLabel("放开以刷新");
        //第一次加载数据
        //group_id 	int 	该文章所属主题的id
        mArticleApi.showThread(article.getBoard_name(),article.getGroup_id(),null,page_current_count,item_page_count,mListener);
        LogUtil.e(TAG,article.getBoard_name()+article.getGroup_id());
    }

    private void setEventListener() {
        refresh_lv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page_current_count--;
                mArticleApi.showThread(article.getBoard_name(),article.getGroup_id(),null,page_current_count,item_page_count,mListener);
                new FinishRefresh().execute();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                //TODO 设置上拉时候触发的方法,加载下一页
                page_current_count++;
                mArticleApi.showThread(article.getBoard_name(),article.getGroup_id(),null,page_current_count,item_page_count,mListener);
                new FinishRefresh().execute();
            }
        });
    }

    /**
     * 开启异步任务，用于加载刷新
     */
    private class FinishRefresh extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            refresh_lv.onRefreshComplete();
        }
    }

    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i(TAG, response);
                Gson gson=new Gson();
                threadsInfo=gson.fromJson(response,Threads.class);
                handler.obtainMessage(SUCEESS, threadsInfo).sendToTarget();
            }
        }

        @Override
        public void onException(BBSException e) {
            // TODO Auto-generated method stub
            LogUtil.e(TAG, e.getMessage());
        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SUCEESS) {
                if(page_current_count==1){
                    attachmentInfo=((Threads)(msg.obj)).getArticle().get(0).getAttachment();
                    filelist=attachmentInfo.getFile();
                }
                articleArrayList.clear();
                articleArrayList.addAll(((Threads)(msg.obj)).getArticle());
                page_all_count = ((Threads)(msg.obj)).getPagination().getPage_all_count();
                item_all_count=((Threads)(msg.obj)).getPagination().getItem_all_count();
                topTenDetailAdapter=new TopTenDetailAdapter(TopTenDetail.this,articleArrayList);
                refresh_lv.setAdapter(topTenDetailAdapter);
            }
        }
    };
}
