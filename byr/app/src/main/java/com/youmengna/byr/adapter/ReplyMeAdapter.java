package com.youmengna.byr.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.youmengna.byr.ByrApplication;
import com.youmengna.byr.bean.Refer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.byr.bbs.sdkdemo.R;


/**
 * Created by youmengna0 on 2016/8/19.
 */
public class ReplyMeAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context mContext;
    private ArrayList<Refer> replyMeArrayList;
    public ReplyMeAdapter(Context mContext, ArrayList<Refer> replyMeArrayList){
        mInflater= LayoutInflater.from(mContext);
        this.replyMeArrayList=replyMeArrayList;
    }
    @Override
    public int getCount() {
        return replyMeArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return replyMeArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        NetworkImageView inboxImg;
        TextView ReplyMeName;
        TextView replyMeTitle;
        TextView ReplyMeDate;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.activity_replyme_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.inboxImg = (NetworkImageView) convertView.findViewById(R.id.replyMeImg);
            viewHolder.ReplyMeName = (TextView) convertView.findViewById(R.id.replyMeName);
            viewHolder.replyMeTitle = (TextView) convertView.findViewById(R.id.replyMeTitle);
            viewHolder.ReplyMeDate = (TextView) convertView.findViewById(R.id.replyMeDate);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Refer refer = replyMeArrayList.get(position);
        //2016.10.24
        viewHolder.inboxImg.setImageUrl(refer.getUser().getFace_url(), ByrApplication.imageLoader);
        viewHolder.ReplyMeName.setText(refer.getUser().getId());
        viewHolder.replyMeTitle.setText(refer.getTitle());

        Date d = new Date((long)refer.getTime()*1000);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        viewHolder.ReplyMeDate.setText(sdf.format(d));

        //判断邮件是已读还是未读
        if(refer.isIs_read()){
            viewHolder.ReplyMeName.setTextColor(Color.parseColor("#BFBFBF"));
            viewHolder.replyMeTitle.setTextColor(Color.parseColor("#BFBFBF"));
            viewHolder.ReplyMeDate.setTextColor(Color.parseColor("#BFBFBF"));
        }else {
            viewHolder.ReplyMeName.setTextColor(Color.parseColor("#000000"));
            viewHolder.replyMeTitle.setTextColor(Color.parseColor("#000000"));
            viewHolder.ReplyMeDate.setTextColor(Color.parseColor("#000000"));
        }
        return convertView;
    }
}
