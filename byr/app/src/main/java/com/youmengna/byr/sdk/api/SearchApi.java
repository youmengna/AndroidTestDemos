package com.youmengna.byr.sdk.api;

import android.text.TextUtils;

import com.youmengna.byr.sdk.auth.Oauth2AccessToken;
import com.youmengna.byr.sdk.net.BBSParameters;
import com.youmengna.byr.sdk.net.RequestListener;
import com.youmengna.byr.sdk.utils.URLHelper;

/**
 * search(board, article, thread) API
 *
 * @author ALSO
 */

/*
 * TODO list:
 * search article
 * search thread by day OR m OR attachment and default parameters(count, page)
 * 
 */
public class SearchApi extends BaseApi {
    private String SR_URL = URLHelper.SEARCH;

    public SearchApi(Oauth2AccessToken accessToken) {
        super(accessToken);
    }

    /**
     * search board
     *
     * @param board    board name
     * @param listener
     */
    public void board(String board, RequestListener listener) {
        if (TextUtils.isEmpty(board)) return;

        BBSParameters param = new BBSParameters();
        param.put("board", board);
        String url = SR_URL + "/board";
        asyncRequest(url, HTTP_GET, param, listener);
    }

    /**
     * search thread with title by default parameters
     *
     * @param board
     * @param title1
     * @param title2
     * @param titlen   threads do not contained
     * @param listener
     */
    public void threadByTitle(String board,
                              String title1, String title2, String titlen,
                              RequestListener listener) {
        if (title1 == null || board == null || TextUtils.isEmpty(board)) return;
        BBSParameters param = new BBSParameters();
        param.put("board", board);
        if (TextUtils.isEmpty(title1)) {
            param.put("title1", title1);
        }
        if (title2 != null && TextUtils.isEmpty(title2)) {
            param.put("title2", title2);
        }

        if (titlen != null && TextUtils.isEmpty(titlen)) {
            param.put("titlen", titlen);
        }

        String url = SR_URL + "/threads";
        asyncRequest(url, HTTP_GET, param, listener);
    }

    /**
     * search by author name
     *
     * @param board
     * @param author
     * @param listener
     */
    public void threadByAuthor(String board, String author, RequestListener listener) {
        if (board == null || TextUtils.isEmpty(board)) return;
        if (author == null || TextUtils.isEmpty(author)) return;

        BBSParameters param = new BBSParameters();
        param.put("board", board);
        param.put("author", author);

        String url = SR_URL + "/threads";
        asyncRequest(url, HTTP_GET, param, listener);
    }
}
