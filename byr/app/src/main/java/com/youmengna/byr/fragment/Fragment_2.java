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
import android.widget.GridView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.youmengna.byr.AccessTokenKeeper;
import com.youmengna.byr.BoardDetailActivity;
import com.youmengna.byr.adapter.FavoriteGridAdapter;
import com.youmengna.byr.bean.Board;
import com.youmengna.byr.bean.Favorite;
import com.youmengna.byr.sdk.api.FavApi;
import com.youmengna.byr.sdk.auth.Oauth2AccessToken;
import com.youmengna.byr.sdk.exception.BBSException;
import com.youmengna.byr.sdk.net.RequestListener;
import com.youmengna.byr.sdk.utils.LogUtil;

import java.util.ArrayList;

import cn.byr.bbs.sdkdemo.R;

public class Fragment_2 extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private static final String TAG = Fragment_2.class.getName();
    private FavApi mFavApi;
    private Oauth2AccessToken mAccessToken;
    protected static final int SUCEESS = 0;
    private FavoriteGridAdapter mFavoriteGridAdapter;
    private ArrayList<Board> boardArrayList = new ArrayList<>();
    private GridView gridView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_fragment_2, container, false);
        mAccessToken = AccessTokenKeeper.readAccessToken(getContext());
        mFavApi = new FavApi(mAccessToken);
        gridView = (GridView) mView.findViewById(R.id.FavouriteGridView);
        //gridview item点击事件
        gridView.setOnItemClickListener(Fragment_2.this);
        gridView.setOnItemLongClickListener(Fragment_2.this);
        if (mAccessToken != null && mAccessToken.isSessionValid()) {
            mFavApi.show(0, mListener);
        }
        return mView;
    }

    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i(TAG, response);
                Gson gson = new Gson();
                Favorite favoriteInfo = gson.fromJson(response, Favorite.class);
                handler.obtainMessage(SUCEESS, favoriteInfo).sendToTarget();
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
                Favorite favoriteInfo = (Favorite) msg.obj;
                boardArrayList.clear();  //清空之前信息
                boardArrayList.addAll(favoriteInfo.getBoard());
                mFavoriteGridAdapter = new FavoriteGridAdapter(getContext(), boardArrayList, getActivity());
                gridView.setAdapter(mFavoriteGridAdapter);
            }
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*if (position == boardArrayList.size()) {
            //Fragment之间跳转
        } else {*/
        Board board = boardArrayList.get(position);
        Intent intent = new Intent(getContext(), BoardDetailActivity.class);
        intent.putExtra("board", board);
        startActivity(intent);
      /*  }*/
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
       /* if (position == boardArrayList.size()) {
            //Fragment之间跳转
        } else {*/
        final Board board = boardArrayList.get(position);
       /* }*/

        String[] mItems = {"取消收藏"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setItems(mItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    mFavApi.delete(0, board.getName(), 0, nListener);
                }
            }
        });
        builder.create();
        builder.show();
        return true;
    }

    private RequestListener nListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i(TAG, response);
                handler1.obtainMessage(SUCEESS).sendToTarget();
                Toast.makeText(getContext(), "取消收藏成功！", Toast.LENGTH_SHORT).show();
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
                mFavApi.show(0, mListener);
            }
        }
    };
}
