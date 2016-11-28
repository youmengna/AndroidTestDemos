package com.youmengna.byr.sdk.api;

import android.text.TextUtils;

import com.youmengna.byr.sdk.auth.Oauth2AccessToken;
import com.youmengna.byr.sdk.net.RequestListener;
import com.youmengna.byr.sdk.utils.LogUtil;
import com.youmengna.byr.sdk.utils.URLHelper;

/**
 * -API: query user information
 *
 * @author ALSO
 */
public class UserApi extends BaseApi {

    private String USER_URL = URLHelper.USER;

    public UserApi(Oauth2AccessToken accessToken) {
        super(accessToken);
    }

    /**
     * get user information
     *
     * @param listener
     */
    public void show(RequestListener listener) {
        String url = USER_URL + "/getinfo";
        asyncRequest(url, HTTP_GET, null, listener);
    }

    /**
     * query somebody's info.
     *
     * @param id      bbs id
     * @param listener
     */
    public void query(String id, RequestListener listener) {
        if (TextUtils.isEmpty(id)) {
            LogUtil.e(TAG, "id null error");
            return;
        }
        String url = USER_URL + "/query/" + id;
        asyncRequest(url, HTTP_GET, null, listener);
    }

}
