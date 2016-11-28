package com.youmengna.byr.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import com.google.gson.Gson;
import com.youmengna.byr.AccessTokenKeeper;
import com.youmengna.byr.BoardDetailActivity;
import com.youmengna.byr.adapter.SectionAdapter;
import com.youmengna.byr.bean.Board;
import com.youmengna.byr.bean.Section;
import com.youmengna.byr.sdk.api.SectionApi;
import com.youmengna.byr.sdk.auth.Oauth2AccessToken;
import com.youmengna.byr.sdk.exception.BBSException;
import com.youmengna.byr.sdk.net.RequestListener;
import com.youmengna.byr.sdk.utils.LogUtil;
import java.util.ArrayList;
import java.util.List;
import cn.byr.bbs.sdkdemo.R;

/**
 * 分区列表
 */
public class Fragment_1 extends Fragment{
    private static final String TAG = Fragment_1.class.getName();
    private ExpandableListView sectionListView;
    private SectionAdapter sectionAdapter;
    private List<Section> SectionArryList = new ArrayList<>();
    private SectionApi mSectionApi;
    private Oauth2AccessToken mAccessToken;
    protected static final int SUCEESS = 0;

    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i(TAG, response);
                Gson gson = new Gson();
                Section sectionInfo = gson.fromJson(response, Section.class);
                List<Section> RootSectionList = sectionInfo.getSection();
                handler.obtainMessage(SUCEESS, RootSectionList).sendToTarget();
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
                SectionArryList.addAll((List<Section>) msg.obj);
                sectionAdapter = new SectionAdapter(SectionArryList,getActivity());
                sectionListView.setAdapter(sectionAdapter);
            }
        }
    };

    private RequestListener nListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i(TAG, response);
                Gson gson = new Gson();
                Section RootSection=gson.fromJson(response,Section.class);
                handler1.obtainMessage(SUCEESS, RootSection).sendToTarget();
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
                Section RootSection=(Section)(msg.obj);
                List<Board> board=RootSection.getBoard();
                String[] sub_section=RootSection.getSub_section();
                SectionArryList.get(Integer.parseInt(RootSection.getName())).setBoard(board);
                SectionArryList.get(Integer.parseInt(RootSection.getName())).setSub_section(sub_section);
                sectionAdapter.notifyDataSetChanged();
            }
        }
    };

    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_fragment_1, container, false);
        sectionListView = (ExpandableListView) mView.findViewById(R.id.section_expand_listview);
        mAccessToken = AccessTokenKeeper.readAccessToken(getContext());
        mSectionApi = new SectionApi(mAccessToken);
        if (mAccessToken != null && mAccessToken.isSessionValid()) {
            mSectionApi.getRootSection(mListener);
        }
        sectionListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                String sectionName=SectionArryList.get(groupPosition).getName();
                mSectionApi.getSection(sectionName,nListener);
            }
        });
        //进入BoardDetailActivity
        sectionListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Board board;
                if(SectionArryList.get(groupPosition).getSub_section()!=null){
                    board = SectionArryList.get(groupPosition).getBoard().get(childPosition-1);
                }else {
                    board = SectionArryList.get(groupPosition).getBoard().get(childPosition);
                }
                Intent intent=new Intent(getContext(), BoardDetailActivity.class);
                intent.putExtra("board",board);
                startActivity(intent);
                return false;
            }
        });
        return mView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
