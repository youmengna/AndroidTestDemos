package com.youmengna.byr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.youmengna.byr.bean.Article;

import java.util.ArrayList;

import cn.byr.bbs.sdkdemo.R;

/**
 * Created by youmengna0 on 2016/8/29.
 */
public class TopTenAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Context mContext;
    private ArrayList<Article> articleArrayList;
    public TopTenAdapter(Context mContext, ArrayList<Article> articleArrayList){
        mInflater= LayoutInflater.from(mContext);
        this.articleArrayList=articleArrayList;
    }
    @Override
    public int getCount() {
        return articleArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return articleArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView topTenTitle;
        TextView topTenReplyCount;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.activity_topten_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.topTenTitle = (TextView) convertView.findViewById(R.id.topTenTitle);
            viewHolder.topTenReplyCount = (TextView) convertView.findViewById(R.id.topTenReplyCount);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Article article = articleArrayList.get(position);
        //viewHolder.inboxImg.setImageURI(mail.get());
        viewHolder.topTenTitle.setText(article.getTitle());
        viewHolder.topTenReplyCount.setText(article.getId_count());
        return convertView;
    }
}
