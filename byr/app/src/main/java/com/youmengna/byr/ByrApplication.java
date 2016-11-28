package com.youmengna.byr;

import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.youmengna.byr.UtilTools.BitmapCache;

/**
 * Created by youmengna0 on 2016/10/23.
 */
public class ByrApplication extends Application{
    public RequestQueue mqueue;
    public static ImageLoader imageLoader;
    private Context context;
    public BitmapCache bitmapCache;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        mqueue=MySingleton.getInstance(this).getRequestQueue();
        bitmapCache=new BitmapCache();
        imageLoader=new ImageLoader(mqueue,bitmapCache);
    }
}
