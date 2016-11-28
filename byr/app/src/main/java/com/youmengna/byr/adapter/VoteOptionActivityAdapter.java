package com.youmengna.byr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import com.youmengna.byr.bean.VoteOption;

import java.text.NumberFormat;
import java.util.ArrayList;

import cn.byr.bbs.sdkdemo.R;

/**
 * Created by youmengna0 on 2016/8/25.
 */
public class VoteOptionActivityAdapter extends BaseAdapter {
    private boolean isMulti;
    private int vote_count;
    private LayoutInflater mInflater;
    private Context mContext;
    private ArrayList<VoteOption> VoteOptionList;
    public VoteOptionActivityAdapter(Context mContext, ArrayList<VoteOption> VoteOptionList) {
        mInflater = LayoutInflater.from(mContext);
        this.VoteOptionList = VoteOptionList;
    }

    @Override
    public int getCount() {
        return VoteOptionList.size();
    }

    @Override
    public Object getItem(int position) {
        return VoteOptionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.vote_option_item, null);
            viewHolder = new ViewHolder();
            //   viewHolder.inboxImg = (ImageView) convertView.findViewById(R.id.inboxImg);
            viewHolder.VoteOptionTitle = (TextView) convertView.findViewById(R.id.VoteOptionTitle);
            viewHolder.VoteOptionWeight = (TextView) convertView.findViewById(R.id.VoteOptionWeight);
            viewHolder.VoteOptionCheckbox = (CheckBox) convertView.findViewById(R.id.VoteOptionCheckbox);
            viewHolder.VoteOptionRadioBtn = (RadioButton) convertView.findViewById(R.id.VoteOptionRadioBtn);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        VoteOption voteOption = VoteOptionList.get(position);
        if(isMulti){
            //多选
            viewHolder.VoteOptionCheckbox.setChecked(voteOption.isChecked());
            viewHolder.VoteOptionCheckbox.setVisibility(View.VISIBLE);
            viewHolder.VoteOptionRadioBtn.setVisibility(View.INVISIBLE);
        }else {
            viewHolder.VoteOptionRadioBtn.setChecked(voteOption.isChecked());
            //单选
            viewHolder.VoteOptionCheckbox.setVisibility(View.INVISIBLE);
            viewHolder.VoteOptionRadioBtn.setVisibility(View.VISIBLE);
        }
        viewHolder.VoteOptionTitle.setText(voteOption.getLabel());
        double d1 = (double)voteOption.getNum()/vote_count;
        NumberFormat nFromat = NumberFormat.getPercentInstance();
        String rates = nFromat.format(d1);
        viewHolder.VoteOptionWeight.setText(rates);
        return convertView;
    }
    private class ViewHolder {
        // ImageView inboxImg;
        TextView VoteOptionTitle;
        TextView VoteOptionWeight;
        //选中CheckBox控件（主题类型为多选时显示）
        CheckBox VoteOptionCheckbox;
        //选中RadioButton控件（主题类型为单选时显示）
        RadioButton VoteOptionRadioBtn;
    }

    public void setisMulti(boolean isMulti){
        this.isMulti=isMulti;
    }
    public void setvote_count(int vote_count){
        this.vote_count=vote_count;
    }
}
