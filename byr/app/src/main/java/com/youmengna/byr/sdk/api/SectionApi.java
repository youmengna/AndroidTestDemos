package com.youmengna.byr.sdk.api;


import com.youmengna.byr.sdk.auth.Oauth2AccessToken;
import com.youmengna.byr.sdk.net.RequestListener;
import com.youmengna.byr.sdk.utils.URLHelper;

/**
 * SectionAPI: get section list
 *
 * @author ALSO
 */
public class SectionApi extends BaseApi {

    private String SEC_URL = URLHelper.SECTION;

    public SectionApi(Oauth2AccessToken accessToken) {
        super(accessToken);
    }

    /**
     * get all root sections
     *
     * @param listener
     */
    public void getRootSection(RequestListener listener) {
        asyncRequest(SEC_URL, HTTP_GET, null, listener);
    }

    /**
     * get section info(sub section if exited and board)
     *
     * @param name    valid section name
     * @param listener
     */
    public void getSection(String name, RequestListener listener) {
        if (name == null) return;
        String url = SEC_URL + "/" + name;
        asyncRequest(url, HTTP_GET, null, listener);
    }
}
