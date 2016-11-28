package com.youmengna.byr;

import android.app.Activity;
import android.os.Bundle;

import com.youmengna.byr.colorUi.util.SharedPreferencesMgr;

import cn.byr.bbs.sdkdemo.R;

public class BaseActivity extends Activity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(SharedPreferencesMgr.getInt("theme", 0) == 1) {
            setTheme(R.style.theme_2);
        } else {
            setTheme(R.style.theme_1);
        }
    }
}
