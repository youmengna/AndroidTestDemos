package com.youmengna.byr.UtilTools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.youmengna.byr.ByrApplication;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.byr.bbs.sdkdemo.R;

/**
 * Created by youmengna0 on 2016/9/25.
 */
public class UtilTool {
    public static int[] ArrayListToArray(ArrayList<Integer> list) {
        int array[] = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    public static String getTime(){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date current=new Date();
        String time=simpleDateFormat.format(current);
        return time;
    }

    /**
     * 获取图片的缩略图
     *
     * @param imagePath
     * @param width
     * @param height
     * @return
     */
    private static Bitmap getImageThumbnail(String imagePath, int width, int height) {
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

    /**
     * 获取视频缩略图
     *
     * @param videoPath
     * @param width
     * @param height
     * @param kind
     * @return
     */
    private static Bitmap getVideoThumbnail(String videoPath, int width, int height,
                                     int kind) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }


 /*   //获取网络图片
    public static void getPictureFromNet(Context context, String url, ImageView imageView,int defaultImageResId,int errorImageResId){
        RequestQueue mQueue= Volley.newRequestQueue(context);
        ImageLoader imageLoader=new ImageLoader(mQueue,bitmapCache);
        ImageLoader.ImageListener listener=ImageLoader.getImageListener(imageView,defaultImageResId,errorImageResId);
        imageLoader.get(url,listener,200,200, ImageView.ScaleType.CENTER);
    }*/



    //获取网络图片
    public static void getPictureFromNet(String url, ImageView imageView, int defaultImageResId, int errorImageResId){
        ImageLoader.ImageListener listener=ImageLoader.getImageListener(imageView,defaultImageResId,errorImageResId);
        ByrApplication.imageLoader.get(url,listener,200,200, ImageView.ScaleType.CENTER);
    }

    //获取网络图片
    public static void getPictureFromNet(String url, ImageView imageView){
        ImageLoader.ImageListener listener=ImageLoader.getImageListener(imageView, R.drawable.aa,R.drawable.error);
        ByrApplication.imageLoader.get(url,listener,200,200, ImageView.ScaleType.CENTER);
    }
}
