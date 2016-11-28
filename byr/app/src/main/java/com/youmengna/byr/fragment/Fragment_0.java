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
import android.widget.AdapterView;
import android.widget.ListView;
import com.google.gson.Gson;
import com.youmengna.byr.AccessTokenKeeper;
import com.youmengna.byr.TopTenDetail;
import com.youmengna.byr.adapter.TopTenAdapter;
import com.youmengna.byr.bean.Article;
import com.youmengna.byr.bean.Widget;
import com.youmengna.byr.sdk.api.WidgetApi;
import com.youmengna.byr.sdk.auth.Oauth2AccessToken;
import com.youmengna.byr.sdk.exception.BBSException;
import com.youmengna.byr.sdk.net.RequestListener;
import com.youmengna.byr.sdk.utils.LogUtil;
import java.util.ArrayList;
import java.util.List;
import cn.byr.bbs.sdkdemo.R;

public class Fragment_0 extends Fragment implements AdapterView.OnItemClickListener{
    private static final String TAG = Fragment_1.class.getName();
    private ListView TopTenList;
    private TopTenAdapter topTenAdapter;
    private ArrayList<Article> ArticleArryList = new ArrayList<>();
    private WidgetApi mWidgetApi;
    private Oauth2AccessToken mAccessToken;
    protected static final int SUCEESS = 0;

    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i(TAG, response);
                Gson gson = new Gson();
                Widget widgetInfo = gson.fromJson(response, Widget.class);
                List<Article> ArticleList = widgetInfo.getArticle();
                handler.obtainMessage(SUCEESS, ArticleList).sendToTarget();
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
                ArticleArryList.clear();
                ArticleArryList.addAll((List<Article>) msg.obj);
                topTenAdapter = new TopTenAdapter(getContext(), ArticleArryList);
                TopTenList.setAdapter(topTenAdapter);
                TopTenList.setOnItemClickListener(Fragment_0.this);
            }
        }
    };

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_fragment_0, container, false);
        TopTenList = (ListView) mView.findViewById(R.id.TopTenList);
        mAccessToken = AccessTokenKeeper.readAccessToken(getContext());
        mWidgetApi = new WidgetApi(mAccessToken);
        if (mAccessToken != null && mAccessToken.isSessionValid()) {
            mWidgetApi.topten(mListener);
        }
        return mView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Article article = ArticleArryList.get(position);
        Intent iTopTenDetail=new Intent(getContext(),TopTenDetail.class);
        iTopTenDetail.putExtra("article",article);
        startActivity(iTopTenDetail);
    }
}
