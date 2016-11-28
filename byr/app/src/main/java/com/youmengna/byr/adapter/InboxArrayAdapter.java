package com.youmengna.byr.adapter;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.youmengna.byr.UtilTools.UtilTool;
import com.youmengna.byr.bean.Mail;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.byr.bbs.sdkdemo.R;


/**
 * Created by youmengna0 on 2016/8/19.
 */
public class InboxArrayAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context mContext;
    private ArrayList<Mail> inboxArrayList;
    public InboxArrayAdapter(Context mContext,ArrayList<Mail> inboxArrayList){
        this.mContext=mContext;
        mInflater= LayoutInflater.from(mContext);
        this.inboxArrayList=inboxArrayList;
    }
    @Override
    public int getCount() {
        return inboxArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return inboxArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        ImageView inboxImg;
        TextView inboxName;
        TextView inboxContent;
        TextView inboxDate;
        TextView inboxState;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.activity_inbox_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.inboxImg = (ImageView) convertView.findViewById(R.id.inboxImg);
            viewHolder.inboxName = (TextView) convertView.findViewById(R.id.inboxName);
            viewHolder.inboxContent = (TextView) convertView.findViewById(R.id.inboxContent);
            viewHolder.inboxDate = (TextView) convertView.findViewById(R.id.inboxDate);
            viewHolder.inboxState = (TextView) convertView.findViewById(R.id.inboxState);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Mail mail = inboxArrayList.get(position);
        UtilTool.getPictureFromNet(mail.getUser().getFace_url(),viewHolder.inboxImg);
        viewHolder.inboxName.setText(mail.getUser().getId());
        viewHolder.inboxContent.setText(mail.getTitle());

        Date d = new Date((long)mail.getPost_time()*1000);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        viewHolder.inboxDate.setText(sdf.format(d));

        if(mail.isIs_reply()){
            viewHolder.inboxState.setText("【已回复】");
        }

        //判断邮件是已读还是未读
        if(mail.isIs_read()){
            viewHolder.inboxName.setTextColor(Color.parseColor("#BFBFBF"));
            viewHolder.inboxContent.setTextColor(Color.parseColor("#BFBFBF"));
            viewHolder.inboxState.setTextColor(Color.parseColor("#BFBFBF"));
            viewHolder.inboxDate.setTextColor(Color.parseColor("#BFBFBF"));
        }else {
            viewHolder.inboxName.setTextColor(Color.parseColor("#000000"));
            viewHolder.inboxContent.setTextColor(Color.parseColor("#000000"));
            viewHolder.inboxState.setTextColor(Color.parseColor("#000000"));
            viewHolder.inboxDate.setTextColor(Color.parseColor("#000000"));
        }

        return convertView;
    }
}
