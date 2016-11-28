package com.youmengna.byr.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.youmengna.byr.AccessTokenKeeper;
import com.youmengna.byr.VoteOptionActivity;
import com.youmengna.byr.adapter.VoteActivityAdapter;
import com.youmengna.byr.bean.Vote;
import com.youmengna.byr.sdk.api.VoteApi;
import com.youmengna.byr.sdk.auth.Oauth2AccessToken;
import com.youmengna.byr.sdk.exception.BBSException;
import com.youmengna.byr.sdk.net.RequestListener;
import com.youmengna.byr.sdk.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import cn.byr.bbs.sdkdemo.R;

public class VoteFragment extends Fragment implements AdapterView.OnItemClickListener {
    private String voteType;
    private static final String TAG = VoteFragment.class.getName();
    protected static final int SUCEESS = 0;
    private int page = 1;
    private VoteApi mVoteApi;
    private Oauth2AccessToken mAccessToken;
    private PullToRefreshListView refresh_lv;
    private VoteActivityAdapter voteActivityAdapter;
    private ArrayList<Vote> voteArrayList = null;
    private Vote voteDetail;
    private int Vid;
    private int isMulti;
    private RequestListener mFirstListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i(TAG, response);
                Gson gson = new Gson();
                Vote mVote = gson.fromJson(response, Vote.class);
                List<Vote> voteInfo = mVote.getVotes();
                handler.obtainMessage(SUCEESS, voteInfo).sendToTarget();
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
                voteArrayList.addAll((List<Vote>) msg.obj);
                voteActivityAdapter = new VoteActivityAdapter(getContext(), voteArrayList);
                refresh_lv.setAdapter(voteActivityAdapter);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_vote, container, false);
        Bundle bundle1 = getArguments();
        voteType = bundle1.getString("votetype");
        mAccessToken = AccessTokenKeeper.readAccessToken(getContext());
        mVoteApi = new VoteApi(mAccessToken);
        initView(mView);
        setEventListener();
        return mView;
    }

    private void initView(View view) {
        refresh_lv = (PullToRefreshListView) view.findViewById(R.id.voteListView);
        refresh_lv.setOnItemClickListener(VoteFragment.this);
        voteArrayList = new ArrayList<>();
        //设置可上拉刷新和下拉刷新
        refresh_lv.setMode(PullToRefreshBase.Mode.BOTH);
        ILoadingLayout startLayout = refresh_lv.getLoadingLayoutProxy(true, false);
        startLayout.setPullLabel("正在下拉刷新...");
        startLayout.setRefreshingLabel("正在玩命加载中...");
        startLayout.setReleaseLabel("放开以刷新");

        ILoadingLayout endLayout = refresh_lv.getLoadingLayoutProxy(false, true);
        endLayout.setPullLabel("正在上拉刷新...");
        endLayout.setRefreshingLabel("正在玩命加载中...");
        endLayout.setReleaseLabel("放开以刷新");

        mVoteApi.voteList(voteType, 1, mFirstListener);
    }

    private void setEventListener() {
        refresh_lv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                new FinishRefresh().execute();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                //TODO 设置上拉时候触发的方法,加载下一页
                page++;
                mVoteApi.voteList(voteType, page, mFirstListener);
                new FinishRefresh().execute();
            }
        });
    }

    //20160919
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Vote vote = voteArrayList.get(position - 1);
        Vid = vote.getVid();
        isMulti = vote.getType();
        mVoteApi.voteoption(Vid, mListener);
    }

    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i(TAG, response);
                Gson gson = new Gson();
                Vote vote = gson.fromJson(response, Vote.class);
                handler1.obtainMessage(SUCEESS, vote).sendToTarget();
            }
        }

        @Override
        public void onException(BBSException e) {
            LogUtil.e(TAG, e.getMessage());
        }
    };

    Handler handler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SUCEESS) {
                voteDetail = (Vote) msg.obj;
                Intent intent = new Intent(getContext(), VoteOptionActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("voteDatail", voteDetail);
                bundle.putInt("Vid", Vid);
                bundle.putInt("isMulti", isMulti);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }
    };

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
}

