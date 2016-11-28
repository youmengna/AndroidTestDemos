package com.youmengna.byr.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.youmengna.byr.ByrApplication;
import com.youmengna.byr.ReplyArticle;
import com.youmengna.byr.bean.Article;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.byr.bbs.sdkdemo.R;

/**
 * Created by youmengna0 on 2016/10/12.
 */
public class TopTenDetailAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Context mContext;
    private ArrayList<Article> articleArrayList;
    public TopTenDetailAdapter(Context context,ArrayList<Article> articleArrayList){
        this.mContext=context;
        mInflater=LayoutInflater.from(context);
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

    public class ViewHolder{
        NetworkImageView topTenDetail_Img;
        TextView topTenDetail_name;
        TextView topTenDetail_position;
        TextView post_time;
        TextView topTenDetail_Content;
        RelativeLayout showMore;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            convertView=mInflater.inflate(R.layout.activity_top_ten_detail_list_item,null);
            viewHolder=new ViewHolder();
            viewHolder.topTenDetail_Img= (NetworkImageView) convertView.findViewById(R.id.topTenDetail_Img);
            viewHolder.topTenDetail_name= (TextView) convertView.findViewById(R.id.topTenDetail_name);
            viewHolder.topTenDetail_position= (TextView) convertView.findViewById(R.id.topTenDetail_position);
            viewHolder.topTenDetail_Content= (TextView) convertView.findViewById(R.id.topTenDetail_Content);
            viewHolder.post_time= (TextView) convertView.findViewById(R.id.post_time);
            viewHolder.showMore= (RelativeLayout) convertView.findViewById(R.id.topTenDetail_showMore);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }
        final Article article = articleArrayList.get(position);
        viewHolder.topTenDetail_Img.setImageUrl(article.getUser().getFace_url(), ByrApplication.imageLoader);
        viewHolder.topTenDetail_name.setText(article.getUser().getId());
        viewHolder.topTenDetail_position.setText("#"+article.getPosition()+"");
        viewHolder.topTenDetail_Content.setText(article.getContent());

        Date d = new Date((long)article.getPost_time()*1000);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        viewHolder.post_time.setText(sdf.format(d));

        viewHolder.showMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, ReplyArticle.class);
                intent.putExtra("boardname",article.getBoard_name());
                intent.putExtra("ReplyArticle_Id",article.getId());
                intent.putExtra("topTenDatail_Title",article.getTitle());
                intent.putExtra("topTenDatail_Content",article.getContent());
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }
}
