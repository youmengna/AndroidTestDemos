package com.youmengna.byr.sdk.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.youmengna.byr.InboxMailActivity;

import cn.byr.bbs.sdkdemo.R;

/**
 * Created by youmengna0 on 2016/10/20.
 */
public class NotificationUtil {
    private Context context;
    private NotificationManager notificationManager;
    public NotificationUtil(Context context) {
        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**
     * 普通的Notification
     */
    public void postNotification(String contentTitle,String contentText,String ticker) {
        Notification.Builder builder = new Notification.Builder(context);
        Intent intent = new Intent(context,InboxMailActivity.class);  //需要跳转指定的页面
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.mipmap.ic_launcher);// 设置图标
        builder.setContentTitle(contentTitle);// 设置通知的标题
        builder.setContentText(contentText);// 设置通知的内容
        builder.setWhen(System.currentTimeMillis());// 设置通知来到的时间
        builder.setAutoCancel(true); //自己维护通知的消失
        builder.setTicker(ticker);// 第一次提示消失的时候显示在通知栏上的
        builder.setOngoing(true);
        //builder.setNumber(20);

        Notification notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;  //只有全部清除时，Notification才会清除
        notificationManager.notify(0,notification);
    }
    public void cancelById() {
        notificationManager.cancel(0);  //对应NotificationManager.notify(id,notification);第一个参数
    }

    public void cancelAllNotification() {
        notificationManager.cancelAll();
    }
}
