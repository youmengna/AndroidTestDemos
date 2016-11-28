package com.youmengna.byr.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.youmengna.byr.bean.Article;
import com.youmengna.byr.bean.Board;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.byr.bbs.sdkdemo.R;


/**
 * Created by youmengna0 on 2016/8/19.
 */
public class BoardDetailAdapter extends BaseAdapter {

    private final int TYPE_1=0;
    private final int TYPE_2=1;
    private int currentpage;
    private LayoutInflater mInflater;
    private Context mContext;
    private ArrayList<Article> boardArticleArrayList;
    private Board board;
    public BoardDetailAdapter(Context mContext, ArrayList<Article> boardArticleArrayList,int currentpage,Board board){
        mInflater= LayoutInflater.from(mContext);
        this.boardArticleArrayList=boardArticleArrayList;
        this.currentpage=currentpage;
        this.board=board;
    }
    @Override
    public int getCount() {
        return boardArticleArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return boardArticleArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        ViewHolderTop viewHolderTop=null;
        int type = getItemViewType(position);
        Article article=boardArticleArrayList.get(position);
        if (convertView == null) {
            switch (type){
                case TYPE_1:
                    convertView=mInflater.inflate(R.layout.board_detail_list_top_item,null);
                    viewHolderTop=new ViewHolderTop();
                    viewHolderTop.boardDetailTopImg= (ImageView) convertView.findViewById(R.id.boardDetailTopImg);
                    viewHolderTop.boardDetailName= (TextView) convertView.findViewById(R.id.boardDetailName);
                    viewHolderTop.boardDetailManager= (TextView) convertView.findViewById(R.id.boardDetailManager);
                    viewHolderTop.boardDetailPostCount= (TextView) convertView.findViewById(R.id.boardDetailPostCount);
                    convertView.setTag(viewHolderTop);
                    break;
                case TYPE_2:
                    convertView=mInflater.inflate(R.layout.board_detail_list_below_item,null);
                    viewHolder=new ViewHolder();
                    viewHolder.boardDetailArticleContent= (TextView) convertView.findViewById(R.id.boardDetailArticleContent);
                    viewHolder.boardDetailArticleDate= (TextView) convertView.findViewById(R.id.boardDetailArticleDate);
                    viewHolder.boardDetailBelowImg= (ImageView) convertView.findViewById(R.id.boardDetailBelowImg);
                    viewHolder.boardDetailUser= (TextView) convertView.findViewById(R.id.boardDetailUser);
                    viewHolder.boardDetailMsg= (ImageView) convertView.findViewById(R.id.boardDetailMsg);
                    viewHolder.boardDetailReplyCount= (TextView) convertView.findViewById(R.id.boardDetailReplyCount);
                    convertView.setTag(viewHolder);
                    break;
            }
        } else {
            switch (type){
                case TYPE_1:
                    viewHolderTop= (ViewHolderTop) convertView.getTag();
                    break;
                case TYPE_2:
                    viewHolder= (ViewHolder) convertView.getTag();
                    break;
            }
        }
        switch (type){
            case TYPE_1:
                viewHolderTop.boardDetailTopImg.setImageResource(R.drawable.ic_launcher);
                viewHolderTop.boardDetailName.setText(board.getDescription());
                viewHolderTop.boardDetailManager.setText("版主："+board.getManager());
                viewHolderTop.boardDetailPostCount.setText("帖子数："+board.getPost_all_count()+"");
                break;
            case TYPE_2:
                viewHolder.boardDetailMsg.setImageResource(R.drawable.topten_msg);
                viewHolder.boardDetailBelowImg.setImageResource(R.drawable.main2_user);
                viewHolder.boardDetailArticleContent.setText(article.getTitle());
                Date d = new Date((long)article.getPost_time()*1000);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                viewHolder.boardDetailArticleDate.setText(sdf.format(d));

                if(null==article.getUser()){
                    viewHolder.boardDetailUser.setText("原帖已删除");
                }else{
                    viewHolder.boardDetailUser.setText(article.getUser().getId());
                }
                viewHolder.boardDetailReplyCount.setText(article.getReply_count()+"");
                break;
        }
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        if(currentpage==1&&position==0){
            return TYPE_1;
        }else {
            return TYPE_2;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }


    private class ViewHolderTop{
        ImageView boardDetailTopImg;
        TextView boardDetailName;
        TextView boardDetailManager;
        TextView boardDetailPostCount;
    }

    private class ViewHolder {
        TextView boardDetailArticleContent;
        TextView boardDetailArticleDate;
        ImageView boardDetailBelowImg;
        TextView boardDetailUser;
        ImageView boardDetailMsg;
        TextView boardDetailReplyCount;
    }
}
