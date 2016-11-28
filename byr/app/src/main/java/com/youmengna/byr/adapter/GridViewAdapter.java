package com.youmengna.byr.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import java.util.ArrayList;
import cn.byr.bbs.sdkdemo.R;

/**
 * Created by youmengna0 on 2016/8/29.
 */
public class GridViewAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Context mContext;
    private ArrayList<String> imageArrayList;
    public GridViewAdapter(Context mContext, ArrayList<String> imageArrayList){
        mInflater= LayoutInflater.from(mContext);
        this.imageArrayList=imageArrayList;
    }
    @Override
    public int getCount() {
        return imageArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return imageArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        ImageView ItemImage;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.gridview_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.ItemImage = (ImageView) convertView.findViewById(R.id.ItemImage);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String imgSrc = imageArrayList.get(position);
        viewHolder.ItemImage.setImageBitmap(getImageThumbnail(imgSrc,50,60));
        return convertView;
    }

    /**
     * 获取图片的缩略图
     *
     * @param imagePath
     * @param width
     * @param height
     * @return
     */
    private Bitmap getImageThumbnail(String imagePath, int width, int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高，注意此处的bitmap为null
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false; // 设为 false
        // 计算缩放比
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }
}
