package com.youmengna.byr.sdk.net;


import com.youmengna.byr.sdk.exception.BBSException;

import org.json.JSONException;

/**
 * Created by ALSO on 2015/3/31.
 */
public abstract interface RequestListener {
    public abstract void onComplete(String paramString) throws JSONException;

    public abstract void onException(BBSException paramException);
}
