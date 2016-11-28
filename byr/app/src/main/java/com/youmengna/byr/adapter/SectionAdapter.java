package com.youmengna.byr.adapter;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.youmengna.byr.AccessTokenKeeper;
import com.youmengna.byr.BoardDetailActivity;
import com.youmengna.byr.bean.Board;
import com.youmengna.byr.bean.Section;
import com.youmengna.byr.sdk.api.FavApi;
import com.youmengna.byr.sdk.api.SectionApi;
import com.youmengna.byr.sdk.auth.Oauth2AccessToken;
import com.youmengna.byr.sdk.exception.BBSException;
import com.youmengna.byr.sdk.net.RequestListener;
import com.youmengna.byr.sdk.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import cn.byr.bbs.sdkdemo.R;


/**
 * Created by youmengna0 on 2016/8/19.
 */
public class SectionAdapter extends BaseExpandableListAdapter {
    private static final String TAG = SectionAdapter.class.getName();
    // RootSection的集合
    private List<Section> rootSections;
    // 创建布局使用
    private Activity activity;
    //subSection下的board集合
//    private List<Board> board_subsection=new ArrayList<>();
    private SectionApi mSectionApi;
    private FavApi mFavApi;
    private Oauth2AccessToken mAccessToken;
    protected static final int SUCEESS = 0;

    //    SubSectionExpandableListViewAdapter adapter;
    public SectionAdapter(List<Section> rootSections, Activity activity) {
        this.rootSections = rootSections;
        this.activity = activity;
    }

    @Override
    public int getGroupCount() {
        // 获取一级条目的数量  就是根目录的大小
        return rootSections.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        Section rootSection = rootSections.get(groupPosition);
        // 获取对应一级条目下二级条目的数量，就是sub_section的数量和board的数量
        if (rootSection.getBoard() != null && rootSection.getSub_section() != null) {
            LogUtil.e("SectionAdapter", "sub_section_count" + rootSection.getSub_section().length + "board_count" + rootSection.getBoard().size());
            return 1 + rootSection.getBoard().size();
        }
        if (rootSection.getBoard() != null && rootSection.getSub_section() == null) {
            return rootSection.getBoard().size();
        }
        if (rootSection.getBoard() == null && rootSection.getSub_section() != null) {
            return 1;
        }
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        // 获取一级条目的对应数据  ，感觉没什么用
        return rootSections.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        // 获取对应一级条目下二级条目的对应数据  感觉没什么用
        Section rootSection = rootSections.get(groupPosition);

        if (rootSection.getBoard() != null && rootSection.getSub_section() != null) {
            if (childPosition < 1) {
                return rootSection.getSub_section()[childPosition];
            } else {
                return rootSection.getBoard().get(childPosition - 1);
            }
        }
        if (rootSection.getBoard() != null && rootSection.getSub_section() == null) {
            return rootSection.getBoard().get(childPosition);
        }
        if (rootSection.getBoard() == null && rootSection.getSub_section() != null) {
            return rootSection.getSub_section()[childPosition];
        }
        return null;

    }

    @Override
    public long getGroupId(int groupPosition) {
        // 直接返回，没什么用
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        // 直接返回，没什么用
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        // 谁知道这个是干什么。。。。
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        // 获取对应一级条目的View  和ListView 的getView相似
        return getGenericView(rootSections.get(groupPosition).getDescription());
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        // 获取对应二级条目的View  和ListView 的getView相似
        LogUtil.e("SectionAdapter", childPosition + "");
        Section rootSection = rootSections.get(groupPosition);
        if (rootSection.getBoard() != null && rootSection.getSub_section() != null) {
            if (childPosition < 1) {
                return getGenericExpandableListView(rootSection);
            } else {
                View view = getGenericView_Board(rootSection.getBoard().get(childPosition - 1));
                return view;
            }
        } else if (rootSection.getBoard() != null && rootSection.getSub_section() == null) {
            return getGenericView_Board(rootSection.getBoard().get(childPosition));
        } else if (rootSection.getBoard() == null && rootSection.getSub_section() != null) {
            return getGenericExpandableListView(rootSection);
        } else {
            return null;
        }
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        // 根据方法名，此处应该表示二级条目是否可以被点击   先返回true 再讲
        return true;
    }


    /**
     * 根据字符串生成布局，，因为我没有写layout.xml 所以用java 代码生成
     * <p/>
     * 实际中可以通过Inflate加载自己的自定义布局文件，设置数据之后并返回
     *
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

    //这个是跟subSection同一级别的board
    private View getGenericView_Board(final Board board) {
        RelativeLayout.LayoutParams realLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
        RelativeLayout view = new RelativeLayout(activity);
        view.setLayoutParams(realLayoutParams);

        //定义子View中两个元素的布局
        RelativeLayout.LayoutParams vlp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        vlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        TextView textView = new TextView(activity);
        textView.setLayoutParams(vlp);
        textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        textView.setPadding(80, 20, 0, 20);
        textView.setText(board.getDescription());
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(18);
        view.addView(textView);

        RelativeLayout.LayoutParams vlp2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        vlp2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        final ImageView imageView = new ImageView(activity);
        imageView.setLayoutParams(vlp2);
        if (board.is_favorite()) {
            imageView.setImageResource(R.drawable.main3_star_select);
        } else {
            imageView.setImageResource(R.drawable.main3_star);
        }
        view.addView(imageView);
        ///
        ///添加收藏夹的功能，需完善
        //
        mAccessToken = AccessTokenKeeper.readAccessToken(activity);
        mFavApi = new FavApi(mAccessToken);
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

    /**
     * 返回子ExpandableListView 的对象  此时传入的是该大学下所有班级的集合。
     *
     * @param rootSection
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public ExpandableListView getGenericExpandableListView(final Section rootSection) {
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        final CustomExpandableListView view = new CustomExpandableListView(activity);
        mAccessToken = AccessTokenKeeper.readAccessToken(activity);
        mSectionApi = new SectionApi(mAccessToken);
        //ExpandableListView view=new ExpandableListView(activity);
        // 加载班级的适配器
        final List<List<Board>> board_subsection = new ArrayList<>();
        for (int i = 0; i < rootSection.getSub_section().length; i++) {
            board_subsection.add(new ArrayList<Board>());
        }
        final SubSectionExpandableListViewAdapter adapter = new SubSectionExpandableListViewAdapter(rootSection.getSub_section(), board_subsection, activity);
        view.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                String subSection = rootSection.getSub_section()[groupPosition];
                MyRequestListener mListener = new MyRequestListener();
                mListener.setadapter(adapter);
                mListener.setBoard(board_subsection);
                mListener.setGroupposion(groupPosition);
                mSectionApi.getSection(subSection, mListener);
            }
        });
        view.setAdapter(adapter);
        view.setPadding(20, 0, 0, 0);

        view.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Board board;
                board = board_subsection.get(groupPosition).get(childPosition);
                Intent intent = new Intent(activity, BoardDetailActivity.class);
                intent.putExtra("board", board);
                activity.startActivity(intent);
                return false;
            }
        });
        return view;
    }

    class MyRequestListener implements RequestListener {
        SubSectionExpandableListViewAdapter mAdapter;

        public void setadapter(SubSectionExpandableListViewAdapter adapter) {
            mAdapter = adapter;
        }

        List<List<Board>> mboard_subsectio;

        public void setBoard(List<List<Board>> board_subsectio) {
            mboard_subsectio = board_subsectio;
        }

        int mgroupposion;

        public void setGroupposion(int groupposion) {
            mgroupposion = groupposion;
        }

        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                Gson gson = new Gson();
                final Section subSectionInfo = gson.fromJson(response, Section.class);
                mboard_subsectio.get(mgroupposion).clear();
                mboard_subsectio.get(mgroupposion).addAll(subSectionInfo.getBoard());
                handler.obtainMessage(SUCEESS, mboard_subsectio);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        }

        @Override
        public void onException(BBSException e) {
            LogUtil.e("SectionAdapter", e.getMessage());
        }
    }

    Handler handler = new Handler();

}
