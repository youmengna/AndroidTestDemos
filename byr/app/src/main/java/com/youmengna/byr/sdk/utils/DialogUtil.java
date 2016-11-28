package com.youmengna.byr.sdk.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import cn.byr.bbs.sdkdemo.R;

/**
 * Created by youmengna0 on 2016/8/18.
 */
public class DialogUtil {
    public static void showDialog(final Context ctx, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx).setMessage(msg).setCancelable(false);
        builder.setTitle("提示信息");
        builder.setIcon(R.drawable.warning);
        builder.setPositiveButton("确定", null);
        builder.setNegativeButton("取消", null);
        builder.create().show();
    }

    public static void createNormalDialog(Context ctx, int iconId, String title, String message, String btnName, DialogInterface.OnClickListener listener, String btnName1, DialogInterface.OnClickListener listener1) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        // 设置对话框的图标
        builder.setIcon(iconId);
        // 设置对话框的标题
        builder.setTitle(title);
        // 设置对话框的显示内容
        builder.setMessage(message);
        // 添加按钮，android.content.DialogInterface.OnClickListener.OnClickListener
        builder.setPositiveButton(btnName, listener);
        builder.setNegativeButton(btnName1,listener1);
        // 创建一个普通对话框
        builder.create().show();
        }
    public static void createDialog(Context ctx, String title, String message, String btnName, DialogInterface.OnClickListener listener, String btnName1, DialogInterface.OnClickListener listener1) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        // 设置对话框的标题
        builder.setTitle(title);
        // 设置对话框的显示内容
        builder.setMessage(message);
        // 添加按钮，android.content.DialogInterface.OnClickListener.OnClickListener
        builder.setPositiveButton(btnName, listener);
        builder.setNegativeButton(btnName1,listener1);
        // 创建一个普通对话框
        builder.create().show();
    }
}
