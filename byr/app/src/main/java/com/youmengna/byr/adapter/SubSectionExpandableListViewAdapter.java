package com.youmengna.byr.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.youmengna.byr.AccessTokenKeeper;
import com.youmengna.byr.bean.Board;
import com.youmengna.byr.sdk.api.FavApi;
import com.youmengna.byr.sdk.auth.Oauth2AccessToken;
import com.youmengna.byr.sdk.exception.BBSException;
import com.youmengna.byr.sdk.net.RequestListener;
import com.youmengna.byr.sdk.utils.LogUtil;

import java.util.List;

import cn.byr.bbs.sdkdemo.R;


/**
 * Created by youmengna0 on 2016/10/28.
 */
public class SubSectionExpandableListViewAdapter extends BaseExpandableListAdapter {

    private static final String TAG=SubSectionExpandableListViewAdapter.class.getName();
    // subSection的集合
    private String[] sub_section;

    // subSection下Board的集合
    private List<List<Board>> board_subsection;

    // 创建布局使用
    private Activity activity;


    private FavApi mFavApi;
    private Oauth2AccessToken mAccessToken;
    protected static final int SUCEESS = 0;

    public SubSectionExpandableListViewAdapter(String[] sub_section,List<List<Board>> board_subsection, Activity activity) {
        this.sub_section = sub_section;
        this.board_subsection=board_subsection;
        this.activity = activity;
    }

    @Override
    public int getGroupCount() {
        // 获取一级条目的数量  就是subsection的大小
        LogUtil.e("SubSectionExpandableListViewAdapter","sub_lengh"+sub_section.length);
        return sub_section.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        // 获取对应一级条目下二级条目的数量，就是各个班学生的数量
        LogUtil.e("SubSectionExpandableListViewAdapter","board_subsection.size"+board_subsection.size());
        return board_subsection.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        // 获取一级条目的对应数据  ，感觉没什么用
        return sub_section[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        // 获取对应一级条目下二级条目的对应数据  感觉没什么用
        return board_subsection.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
//        board_subsection.add(new ArrayList<Board>());
        // 获取对应一级条目的View  和ListView 的getView相似
        return getGenericView(sub_section[groupPosition]);
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        // 获取对应二级条目的View  和ListView 的getView相似
        return getGenericView_Board(board_subsection.get(groupPosition).get(childPosition));
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        // 根据方法名，此处应该表示二级条目是否可以被点击   先返回true 再讲
        return true;
    }
    /**
     * 根据字符串生成布局，，因为我没有写layout.xml 所以用java 代码生成
     *
     *      实际中可以通过Inflate加载自己的自定义布局文件，设置数据之后并返回
     * @param string
     * @return
     */
    private TextView getGenericView(String string) {

        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView textView = new TextView(activity);
        textView.setLayoutParams(layoutParams);
        textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        textView.setPadding(80, 20, 0, 20);
        textView.setTextSize(20);
        textView.setText(string);
        textView.setTextColor(Color.BLACK);
        return textView;
    }

    private View getGenericView_Board(final Board board){
        String boardDescription=board.getDescription();
        RelativeLayout.LayoutParams realLayoutParams=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
        RelativeLayout view=new RelativeLayout(activity);
        view.setLayoutParams(realLayoutParams);

        //定义子View中两个元素的布局
        RelativeLayout.LayoutParams vlp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        vlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        TextView textView=new TextView(activity);
        textView.setLayoutParams(vlp);
        textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        textView.setPadding(80, 20, 0, 20);
        textView.setText(boardDescription);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(18);
        view.addView(textView);

        RelativeLayout.LayoutParams vlp2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        vlp2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        final ImageView imageView=new ImageView(activity);
        imageView.setLayoutParams(vlp2);
        if (board.is_favorite()) {
            imageView.setImageResource(R.drawable.main3_star_select);
        } else {
            imageView.setImageResource(R.drawable.main3_star);
        }
        mAccessToken = AccessTokenKeeper.readAccessToken(activity);
        mFavApi = new FavApi(mAccessToken);
       /* 收藏版面信息*/
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(board.is_favorite()){
                    board.setIs_favorite(false);
                    imageView.setImageResource(R.drawable.main3_star);
                    mFavApi.delete(0,board.getName(),0,nListener);
                }else {
                    board.setIs_favorite(true);
                    imageView.setImageResource(R.drawable.main3_star_select);
                    mFavApi.add(0, board.getName(), 0, mListener);
                }
            }
        });
        view.addView(imageView);
        return view;
    }
    private RequestListener nListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i(TAG, response);
                Toast.makeText(activity,"取消收藏成功!",Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        public void onException(BBSException e) {
            LogUtil.e(TAG, e.getMessage());
        }
    };

    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i(TAG, response);
                Toast.makeText(activity,"收藏成功!",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onException(BBSException e) {
            LogUtil.e(TAG, e.getMessage());
        }
    };
}
