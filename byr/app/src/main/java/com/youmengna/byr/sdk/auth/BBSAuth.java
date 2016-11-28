package com.youmengna.byr.sdk.auth;

import android.content.Context;
import android.os.Bundle;

import com.youmengna.byr.sdk.net.BBSParameters;
import com.youmengna.byr.sdk.utils.Lang;
import com.youmengna.byr.sdk.utils.LogUtil;
import com.youmengna.byr.sdk.utils.NetworkHelper;
import com.youmengna.byr.sdk.utils.UIUtils;
import com.youmengna.byr.sdk.utils.URLHelper;
import com.youmengna.byr.sdk.utils.Utility;

/**
 * Created by ALSO on 2015/3/31.
 */
public class BBSAuth {
    public static final String TAG = "bbsAuth_login";
    private static final String response_type = "token";

    private Context mContext;
    private AuthInfo authInfo;

    /**
     * Constructor
     *
     * @param client_id:    developer's APP_KEY
     * @param redirect_uri: uri which developer signed
     * @param scope:        null for default/ {mail, attachment, article, favor, black}
     */
    public BBSAuth(Context context, String client_id, String redirect_uri, String scope) {
        this.mContext = context;
        this.setAuthInfo(new AuthInfo(context, client_id, redirect_uri, response_type, scope));
    }

    public BBSAuth(Context context, AuthInfo info) {
        this.mContext = context;
        this.setAuthInfo(info);
    }

    /**
     * Oauth2 method to get bbs access token
     *
     * @param listener: call back class where call back informations include
     *                  access token and expire time stored.
     */
    public void authorize(BBSAuthListener listener) {
        startAuthDialog(listener);
    }

    /*
     * start the authorization dialog
     * @param listener: call back class
     */
    private void startAuthDialog(BBSAuthListener listener) {
        if (listener == null) return;

        // put reuqest parameters for uri encode later
        BBSParameters requestParams = new BBSParameters();
        requestParams.put("client_id", this.authInfo.client_id);
        requestParams.put("redirect_uri", this.authInfo.redirect_uri);
        requestParams.put("response_type", this.authInfo.response_type);
        requestParams.put("scope", this.authInfo.scope);
        // put package name and signature !!REQUESTED
        requestParams.put("packagename", this.authInfo.pkgName);
        requestParams.put("signature", this.authInfo.keyHash);
        String uri = URLHelper.URL_OAUTH2_AUTHORIZE + "?" +  requestParams.encodeUrl();

        if (!NetworkHelper.hasInternetPermission(this.mContext)) {
            UIUtils.showAlert(this.mContext, "Error", "Application requires permission to access the Internet");
        } else if (NetworkHelper.isNetworkAvailable(this.mContext)) {
            new BBSDialog(this.mContext, uri, listener, this).show();
        } else { // network not available, show toast
            String networkNotAvailable = Lang.getString(this.mContext, 2);
            LogUtil.i("bbs_login", "String: " + networkNotAvailable);
            UIUtils.showToast(this.mContext, networkNotAvailable, 0);
        }
    }

    // getters and setters
    public AuthInfo getAuthInfo() {
        return authInfo;
    }

    public void setAuthInfo(AuthInfo authInfo) {
        this.authInfo = authInfo;
    }

    /*
     * AuthInfo class
     * mainly stores authorization request parameters
     */
    public static class AuthInfo {
        private String client_id = "";
        private String redirect_uri = "";
        private String scope = "";
        private String response_type = "";

        // package name and app signature
        private String pkgName = "";
        private String keyHash = "";

        private Bundle mBundle = null;

        public AuthInfo(Context context, String client_id, String redirect_uri, String response_type, String scope) {

            this.client_id = client_id;
            this.redirect_uri = redirect_uri;
            this.scope = scope;
            this.response_type = response_type;

            // get package name from context
            this.pkgName = context.getPackageName();

            this.keyHash = Utility.getSign(context, this.pkgName);

            initAuthBundle();
        }

        // getters
        public String getClient_id() {
            return this.client_id;
        }

        public String getRedirect_uri() {
            return this.redirect_uri;
        }

        public String getScope() {
            return this.scope;
        }

        public Bundle getmBundle() {
            return this.mBundle;
        }

        public String getPkgName() {
            return this.pkgName;
        }

        public String getKeyHash() {
            return this.keyHash;
        }

        // init Bundle
        private void initAuthBundle() {
            this.mBundle = new Bundle();
            mBundle.putString("client_id", this.client_id);
            mBundle.putString("redirect_uri", this.redirect_uri);
            mBundle.putString("pkgName", this.pkgName);
            mBundle.putString("scope", this.scope);
            mBundle.putString("keyHash", this.keyHash);
            mBundle.putString("response_type", this.response_type);
        }
    } // class AuthInfo
}
