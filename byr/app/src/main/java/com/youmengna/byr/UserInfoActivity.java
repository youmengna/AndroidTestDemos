package com.youmengna.byr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.youmengna.byr.UtilTools.UtilTool;
import com.youmengna.byr.bean.User;
import java.text.SimpleDateFormat;
import java.util.Date;
import cn.byr.bbs.sdkdemo.R;

public class UserInfoActivity extends Activity {

    private ImageView headImg;
    private TextView accountID;
    private TextView accountname;
    private TextView userSex;
    private TextView userConstellation;
    private TextView userQQ;
    private TextView userMSN;
    private TextView userHome;
    private TextView userLevel;
    private TextView userIsOnline;
    private TextView userScore;
    private TextView userLife;
    private TextView userArticles;
    private TextView userLastLoginTime;
    private TextView userIP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);


        Intent intent=getIntent();
        User user = (User)intent.getSerializableExtra("UserInfo");


        headImg= (ImageView) findViewById(R.id.headImg);
        UtilTool.getPictureFromNet(user.getFace_url(),headImg,R.drawable.aa,R.drawable.error);

        accountID= (TextView) findViewById(R.id.accountID);
        accountID.setText(user.getId());

        accountname= (TextView) findViewById(R.id.accountname);
        accountname.setText(user.getUser_name());

        userSex= (TextView) findViewById(R.id.userSex);
        String sex=user.getGender();
        if("m".equals(sex)){
            userSex.setText("美女");
        }
        if("f".equals(sex)){
            userSex.setText("帅哥");
        }
        if("n".equals(sex)){
            userSex.setText("隐藏");
        }

        userConstellation= (TextView) findViewById(R.id.userConstellation);
        userConstellation.setText(user.getAstro());

        userQQ= (TextView) findViewById(R.id.userQQ);
        userQQ.setText(user.getQq());

        userMSN= (TextView) findViewById(R.id.userMSN);
        userMSN.setText(user.getMsn());

        userHome= (TextView) findViewById(R.id.userHome);
        userHome.setText(user.getHome_page());

        userLevel= (TextView) findViewById(R.id.userLevel);
        userLevel.setText(user.getLevel());

        userIsOnline= (TextView) findViewById(R.id.userIsOnline);
        if(user.isIs_online()){
            userIsOnline.setText("在线");
        }else{
            userIsOnline.setText("离线");
        }
        userScore= (TextView) findViewById(R.id.userScore);
        userScore.setText(user.getScore());

        userLife= (TextView) findViewById(R.id.userLife);
        userLife.setText(user.getLife()+"");

        userArticles= (TextView) findViewById(R.id.userArticles);
        userArticles.setText(user.getPost_count()+"");

        userLastLoginTime= (TextView) findViewById(R.id.userLastLoginTime);
        Date d = new Date((long)user.getFirst_login_time()*1000);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        userLastLoginTime.setText(sdf.format(d));


        userIP= (TextView) findViewById(R.id.userIP);
        userIP.setText(user.getLast_login_ip());
    }
}
