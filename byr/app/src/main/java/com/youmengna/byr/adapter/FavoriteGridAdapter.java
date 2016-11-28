package com.youmengna.byr.adapter;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.youmengna.byr.bean.Board;

import java.util.ArrayList;

import cn.byr.bbs.sdkdemo.R;

/**
 * Created by youmengna0 on 2016/8/29.
 */
public class FavoriteGridAdapter extends BaseAdapter {
    private static final String TAG = FavoriteGridAdapter.class.getName();
    protected static final int SUCEESS = 0;
    /* private final int TYPE_1 = 0;
     private final int TYPE_2 = 1;*/
    private LayoutInflater mInflater;
    private Context mContext;
    private FragmentActivity mFragmentActivity;
    private ArrayList<Board> boardArrayList;

    public FavoriteGridAdapter(Context mContext, ArrayList<Board> boardArrayList, FragmentActivity mFragmentActivity) {
        mInflater = LayoutInflater.from(mContext);
        this.boardArrayList = boardArrayList;
        this.mFragmentActivity = mFragmentActivity;
    }

    @Override
    public int getCount() {
        //return boardArrayList.size() + 1;
        return boardArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return boardArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView FavouriteText;
    }

   /* private class ViewHolderLast {
        ImageView AddFavourite;
    }*/

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        //ViewHolderLast viewHolderLast = null;
        int type = getItemViewType(position);
        Board board = null;
        if (position < boardArrayList.size()) {
            board = boardArrayList.get(position);
        }
/*        if (convertView == null) {
            switch (type) {
                case TYPE_1:
                    convertView = mInflater.inflate(R.layout.favourite_grid_list_item, null);
                    viewHolder = new ViewHolder();
                    viewHolder.FavouriteText = (TextView) convertView.findViewById(R.id.FavouriteText);
                    convertView.setTag(viewHolder);
                    break;
                case TYPE_2:
                    convertView = mInflater.inflate(R.layout.add_favourite_grid_item, null);
                    viewHolderLast = new ViewHolderLast();
                    viewHolderLast.AddFavourite = (ImageView) convertView.findViewById(R.id.addFavouriteBtn);
                    convertView.setTag(viewHolder);
                    break;
            }
        } else {
            switch (type) {
                case TYPE_1:
                    viewHolder = (ViewHolder) convertView.getTag();
                    break;
                case TYPE_2:
                    viewHolderLast = (ViewHolderLast) convertView.getTag();
                    break;
            }
        }
        switch (type){
            case TYPE_1:
                viewHolder.FavouriteText.setText(board.getDescription());
                break;
            case TYPE_2:
                viewHolderLast.AddFavourite.setImageResource(R.drawable.addfavourite);
                break;
        }*/

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.favourite_grid_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.FavouriteText = (TextView) convertView.findViewById(R.id.FavouriteText);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.FavouriteText.setText(board.getDescription());
        return convertView;
    }

   /* @Override
    public int getViewTypeCount() {
        return 2;
    }*/

    /*@Override
    public int getItemViewType(int position) {
        if (position + 1 > boardArrayList.size()) {
            return TYPE_2;
        }
        return TYPE_1;
    }*/
}
