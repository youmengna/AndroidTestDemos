package com.youmengna.byr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.youmengna.byr.ByrApplication;
import com.youmengna.byr.bean.Vote;
import java.util.ArrayList;
import cn.byr.bbs.sdkdemo.R;


/**
 * Created by youmengna0 on 2016/8/25.
 */
public class VoteActivityAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context mContext;
    private ArrayList<Vote> VoteArrayList;

    public VoteActivityAdapter(Context mContext, ArrayList<Vote> VoteArrayList) {
        mInflater = LayoutInflater.from(mContext);
        this.VoteArrayList = VoteArrayList;
    }

    @Override
    public int getCount() {
        return VoteArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return VoteArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.vote_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.voteImage = (NetworkImageView) convertView.findViewById(R.id.voteImage);
            viewHolder.voteCount = (TextView) convertView.findViewById(R.id.voteCount);
            viewHolder.voteTitle = (TextView) convertView.findViewById(R.id.voteTitle);
            viewHolder.voteName = (TextView) convertView.findViewById(R.id.voteName);
            viewHolder.voteState = (TextView) convertView.findViewById(R.id.voteState);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Vote vote = VoteArrayList.get(position);
        viewHolder.voteImage.setImageUrl(vote.getUser().getFace_url(), ByrApplication.imageLoader);
        viewHolder.voteCount.setText(vote.getUser_count()+"");
        viewHolder.voteTitle.setText(vote.getTitle());
        viewHolder.voteName.setText(vote.getUser().getId());
        if(vote.isIs_end()){
            viewHolder.voteState.setText("已截止");
        }else{
            viewHolder.voteState.setText("未截止");
        }
        return convertView;
    }
    private class ViewHolder {
        NetworkImageView voteImage;
        TextView voteCount;
        TextView voteTitle;
        TextView voteName;
        TextView voteState;
    }
}
