package com.youmengna.byr;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.astuetz.PagerSlidingTabStrip;
import com.google.gson.Gson;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.youmengna.byr.UtilTools.UtilTool;
import com.youmengna.byr.bean.Mailbox;
import com.youmengna.byr.bean.User;
import com.youmengna.byr.fragment.Fragment_0;
import com.youmengna.byr.fragment.Fragment_1;
import com.youmengna.byr.fragment.Fragment_2;
import com.youmengna.byr.sdk.api.MailApi;
import com.youmengna.byr.sdk.api.UserApi;
import com.youmengna.byr.sdk.auth.Oauth2AccessToken;
import com.youmengna.byr.sdk.exception.BBSException;
import com.youmengna.byr.sdk.net.RequestListener;
import com.youmengna.byr.sdk.utils.DialogUtil;
import com.youmengna.byr.sdk.utils.LogUtil;
import com.youmengna.byr.sdk.utils.NotificationUtil;
import java.util.ArrayList;
import cn.byr.bbs.sdkdemo.R;


public class MainActivity extends FragmentActivity implements View.OnClickListener {
    private LinearLayout nightmodell;
    private LinearLayout votell;
    private LinearLayout setupll;
    private LinearLayout logoutll;
    private LinearLayout mailBoxll;
    private LinearLayout atmeLinearLayout;
    private LinearLayout replayll;
    private ImageView headImg;
    private TextView username;
    private TextView userID;
    private ImageView MyheadImg;
    private ViewPager pager;
    private MyPagerAdapter pagerAdapter;
    private PagerSlidingTabStrip tabs;
    private UserApi mUserApi;
    private MailApi mMailApi;
    private User user;
    private Mailbox mailbox;
    private static final String TAG = "MainActivity";
    protected static final int SUCEESS = 0;
    private Oauth2AccessToken mAccessToken;

    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i(TAG, response);
                Gson gson = new Gson();
                User UserAccountInfo = gson.fromJson(response, User.class);
                handler.obtainMessage(SUCEESS, UserAccountInfo).sendToTarget();
            }
        }

        @Override
        public void onException(BBSException e) {
            LogUtil.e(TAG, e.getMessage());
        }
    };

    private RequestListener mboxListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i(TAG, response);
                Gson gson = new Gson();
                Mailbox mailbox = gson.fromJson(response, Mailbox.class);
                handler1.obtainMessage(SUCEESS, mailbox).sendToTarget();
            }
        }

        @Override
        public void onException(BBSException e) {
            // TODO Auto-generated method stub
            LogUtil.e(TAG, e.getMessage());
        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SUCEESS) {
                user = (User) msg.obj;
                username.setText(user.getUser_name());
                userID.setText(user.getId());
                UtilTool.getPictureFromNet(user.getFace_url(), headImg, R.drawable.aa, R.drawable.bb);
            }
        }
    };

    Handler handler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SUCEESS) {
                mailbox = (Mailbox) (msg.obj);
                if (mailbox.isNew_mail() && mailbox.isCan_send()) {
                    NotificationUtil notiUtil = new NotificationUtil(MainActivity.this);
                    notiUtil.postNotification("新邮件", "亲爱哒北邮人，您有新消息啦~", "新消息");
                } else if (mailbox.isFull_mail()) {
                    Toast.makeText(MainActivity.this, "您的邮箱已满，请清理！", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        mUserApi = new UserApi(mAccessToken);
        mMailApi = new MailApi(mAccessToken);
        if (mAccessToken != null && mAccessToken.isSessionValid()) {
            mUserApi.show(mListener);
            mMailApi.boxInfo(mboxListener);
        } else {
            Toast.makeText(MainActivity.this, R.string.bbsSDK_token_empty, Toast.LENGTH_LONG).show();
        }
        /**
         * SlidingMenu设置
         */
        SlidingMenu menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.LEFT);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.layout_left_menu);

        /**
         *
         */
        nightmodell = (LinearLayout) menu.findViewById(R.id.nightmodell);
        nightmodell.setOnClickListener(this);

        votell = (LinearLayout) menu.findViewById(R.id.votell);
        votell.setOnClickListener(this);

        setupll = (LinearLayout) menu.findViewById(R.id.setupll);
        setupll.setOnClickListener(this);

        logoutll = (LinearLayout) menu.findViewById(R.id.logoutll);
        logoutll.setOnClickListener(this);

        mailBoxll = (LinearLayout) menu.findViewById(R.id.mailBoxll);
        mailBoxll.setOnClickListener(this);

        atmeLinearLayout = (LinearLayout) menu.findViewById(R.id.atmeLinearLayout);
        atmeLinearLayout.setOnClickListener(this);

        replayll = (LinearLayout) menu.findViewById(R.id.replayll);
        replayll.setOnClickListener(this);


        headImg = (ImageView) menu.findViewById(R.id.MyheadImg);
        headImg.setOnClickListener(this);

        username = (TextView) menu.findViewById(R.id.Myname);
        userID = (TextView) menu.findViewById(R.id.Myaccount);


        //ViewPager+Fragment
        tabs = (PagerSlidingTabStrip) this.findViewById(R.id.tabs);
        pager = (ViewPager) this.findViewById(R.id.pager);
        initTabs();
        ArrayList<Fragment> fragmentlist = new ArrayList<Fragment>();

        //注意，这三个Fragment需要自己实现
        Fragment_0 buttonFragment = new Fragment_0();
        Fragment_1 textFragment = new Fragment_1();
        Fragment_2 mylistFragment = new Fragment_2();
        fragmentlist.add(buttonFragment);
        fragmentlist.add(textFragment);
        fragmentlist.add(mylistFragment);

        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), fragmentlist);
        pager.setAdapter(pagerAdapter);

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);
        tabs.setViewPager(pager);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mailBoxll:
                Intent inboxIntent = new Intent(MainActivity.this, InboxMailActivity.class);
                startActivity(inboxIntent);
                break;
            case R.id.atmeLinearLayout:
                Intent atmeIntent = new Intent(MainActivity.this, ReplyMeActivity.class);
                atmeIntent.putExtra("refertype", "at");
                startActivity(atmeIntent);
                break;
            case R.id.replayll:
                Intent replayIntent = new Intent(MainActivity.this, ReplyMeActivity.class);
                replayIntent.putExtra("refertype", "reply");
                startActivity(replayIntent);
                break;
            case R.id.nightmodell:
                /*if (SharedPreferencesMgr.getInt("theme", 0) == 1) {
                    SharedPreferencesMgr.setInt("theme", 0);
                    setTheme(R.style.theme_1);
                } else {
                    SharedPreferencesMgr.setInt("theme", 1);
                    setTheme(R.style.theme_2);
                }
                final View rootView = getWindow().getDecorView();
                ColorUiUtil.changeTheme(rootView, getTheme());*/
                break;
            case R.id.votell:
                Intent intent = new Intent(MainActivity.this, VoteActivity.class);
                startActivity(intent);
                break;
            case R.id.setupll:
                Intent SetupActivity = new Intent(MainActivity.this, SetupActivity.class);
                startActivity(SetupActivity);
                break;
            case R.id.logoutll:
                alertInfo();
                break;
            case R.id.MyheadImg:
                Intent showUserInfo = new Intent(MainActivity.this, UserInfoActivity.class);
                showUserInfo.putExtra("UserInfo", user);
                startActivity(showUserInfo);
                break;
        }
    }

    void alertInfo() {
        DialogUtil.createNormalDialog(MainActivity.this, R.drawable.warning, "提示", "确认注销账户吗？", "确 定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AccessTokenKeeper.clear(getApplicationContext());
                Intent intent = new Intent(MainActivity.this, AuthActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }


    /**
     * 定义的属性，参考：https://github.com/astuetz/PagerSlidingTabStrip
     */
    private void initTabs() {

        DisplayMetrics dm;
        dm = getResources().getDisplayMetrics();  //获取当前屏幕的密度
        // 设置Tab是自动填充满屏幕的
        tabs.setShouldExpand(true);
        // 设置Tab的分割线是透明的
        tabs.setDividerColor(Color.TRANSPARENT);
        // 设置Tab底部线的高度
        tabs.setUnderlineHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 1, dm));
        // 设置Tab Indicator的高度
        tabs.setIndicatorHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 4, dm));
        // 设置Tab标题文字的大小
        tabs.setTextSize((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 16, dm));
        // 设置Tab Indicator的颜色
        tabs.setIndicatorColor(Color.parseColor("#6699FF"));
        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)
        tabs.setSelectedTextColor(Color.parseColor("#6699FF"));
        // 取消点击Tab时的背景色
        tabs.setTabBackground(0);
    }

    //MyPagerAdapter要和上面实现的三个Fragment对应起来
    class MyPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {"十大热门", "分区列表", "频道订阅"};
        private ArrayList<Fragment> fragmentlist;

        public MyPagerAdapter(FragmentManager fm, ArrayList<Fragment> list) {
            super(fm);
            fragmentlist = list;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return fragmentlist.size();
        }

        public Fragment getItem(int position) {
            return fragmentlist.get(position);
        }
    }
}