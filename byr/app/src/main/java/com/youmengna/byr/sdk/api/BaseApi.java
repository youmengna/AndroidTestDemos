package com.youmengna.byr.sdk.api;

import android.text.TextUtils;

import com.youmengna.byr.sdk.auth.Oauth2AccessToken;
import com.youmengna.byr.sdk.net.BBSParameters;
import com.youmengna.byr.sdk.net.RequestListener;
import com.youmengna.byr.sdk.utils.LogUtil;
import com.youmengna.byr.sdk.Config;
import com.youmengna.byr.sdk.net.AsyncRunner;


/**
 * base class
 *
 * @author ALSO
 */
public class BaseApi {
    protected static final String TAG = "api";

    protected static final String ACCESS_TOKEN = "oauth_token";

    protected static final String HTTP_POST = "POST";
    protected static final String HTTP_GET = "GET";

    protected Oauth2AccessToken mAccessToken;

    /**
     * construct
     *
     * @param accessToken: access token obj
     */
    public BaseApi(Oauth2AccessToken accessToken) {
        this.mAccessToken = accessToken;
    }

    /**
     * send a asynchronous HTTP request
     *
     * @param url
     * @param httpMethod
     * @param params
     * @param rqListener callback interface
     */
    public void asyncRequest(String url, String httpMethod, BBSParameters params, RequestListener rqListener) {
        if (null == mAccessToken
                || TextUtils.isEmpty(url)
                || TextUtils.isEmpty(httpMethod)
                || null == rqListener) {
            LogUtil.e(TAG, "Argument error!");
            return;
        }
        if (params == null) params = new BBSParameters();
        params.put(ACCESS_TOKEN, mAccessToken.getAccessToken());
        url = url + '.' + Config.RETURN_FORMAT;
        AsyncRunner.requestAsync(url, params, httpMethod, rqListener);
    }

}
