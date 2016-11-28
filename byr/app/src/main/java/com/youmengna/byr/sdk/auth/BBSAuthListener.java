package com.youmengna.byr.sdk.auth;

import android.os.Bundle;

import com.youmengna.byr.sdk.exception.BBSException;


/**
 * Created by ALSO on 2015/3/31.
 */
public abstract interface BBSAuthListener {
    public abstract void onComplete(Bundle paramBundle);

    public abstract void onException(BBSException paramException);

    public abstract void onCancel();
}
