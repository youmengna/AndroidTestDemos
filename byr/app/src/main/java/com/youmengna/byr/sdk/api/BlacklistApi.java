package com.youmengna.byr.sdk.api;

import android.text.TextUtils;

import com.youmengna.byr.sdk.auth.Oauth2AccessToken;
import com.youmengna.byr.sdk.net.BBSParameters;
import com.youmengna.byr.sdk.net.RequestListener;
import com.youmengna.byr.sdk.utils.URLHelper;

/**
 * Blacklist API
 *
 * @author ALSO
 */
public class BlacklistApi extends BaseApi {
    private String BL_URL = URLHelper.BLACKLIST;

    public BlacklistApi(Oauth2AccessToken accessToken) {
        super(accessToken);
    }

    /**
     * show list
     *
     * @param count   number of every page
     * @param page    list page
     * @param listener
     */
    public void showList(int count, int page, RequestListener listener) {
        BBSParameters param = new BBSParameters();
        param.put("count", count);
        param.put("page", page);

        String url = BL_URL + "/list";
        asyncRequest(url, HTTP_GET, param, listener);
    }

    /**
     * show blacklist using default parameters count=20 page=1
     *
     * @param listener
     */
    public void showList(RequestListener listener) {
        showList(20, 1, listener);
    }

    /**
     * add a member to blacklist
     *
     * @param userid
     * @param listener
     */
    public void add(String userid, RequestListener listener) {
        if (!TextUtils.isEmpty(userid)) {
            BBSParameters param = new BBSParameters();
            param.put("id", userid);
            String url = BL_URL + "/add";
            asyncRequest(url, HTTP_POST, param, listener);
        }
    }

    /**
     * remove a member from blacklist
     *
     * @param userid
     * @param listener
     */
    public void delete(String userid, RequestListener listener) {
        if (!TextUtils.isEmpty(userid)) {
            BBSParameters param = new BBSParameters();
            param.put("id", userid);
            String url = BL_URL + "/delete";
            asyncRequest(url, HTTP_POST, param, listener);
        }
    }

}
