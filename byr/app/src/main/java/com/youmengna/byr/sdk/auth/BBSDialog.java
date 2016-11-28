package com.youmengna.byr.sdk.auth;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.youmengna.byr.sdk.exception.BBSDialogException;
import com.youmengna.byr.sdk.utils.LogUtil;
import com.youmengna.byr.sdk.exception.BBSAuthException;
import com.youmengna.byr.sdk.utils.Lang;
import com.youmengna.byr.sdk.utils.NetworkHelper;
import com.youmengna.byr.sdk.utils.Utility;


/**
 * Created by ALSO on 2015/3/31.
 */
public class BBSDialog extends Dialog {

    private static final String TAG = BBSDialog.class.getName();



    // private static final int WEBVIEW_CONTAINER_MARGIN_TOP = 25;
    // private static final int WEBVIEW_MARGIN = 10;
    private static int theme = 16973840;
    private Context mContext;
    private RelativeLayout mRootContainer;
    private RelativeLayout mWebViewContainer;
    private ProgressDialog mLoadingDlg;
    private WebView mWebView;
    private boolean mIsDetached = false;
    private String mAuthUrl;
    private BBSAuthListener mListener;
    private BBSAuth mbbs;

    public BBSDialog(Context context, String authUrl, BBSAuthListener listener, BBSAuth mbbs) {
        super(context, theme);
        this.mAuthUrl = authUrl;
        this.mListener = listener;
        this.mContext = context;
        this.mbbs = mbbs;
    }

    public void OnBackPressed() {
        super.onBackPressed();
        if (this.mListener != null) {
            this.mListener.onCancel();
        }
    }

    public void dismiss() {
        if (!this.mIsDetached) {
            if ((this.mLoadingDlg != null) && (this.mLoadingDlg.isShowing())) {
                this.mLoadingDlg.dismiss();
            }
            super.dismiss();
        }
    }

    public void onAttachedToWindow() {
        this.mIsDetached = false;
        super.onAttachedToWindow();
    }

    public void onDetachedFromWindow() {
        if (this.mWebView != null) {
            this.mWebViewContainer.removeView(this.mWebView);
            this.mWebView.stopLoading();
            this.mWebView.removeAllViews();
            this.mWebView.destroy();
            this.mWebView = null;
        }

        this.mIsDetached = true;
        super.onDetachedFromWindow();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initWindow();

        initLoadingDlg();

        initWebView();

    }

    private void initWindow() {
        requestWindowFeature(1); // enable extend window
        getWindow().setFeatureDrawableAlpha(0, 0);
        getWindow().setSoftInputMode(16); // 0x10 adjust resize

        this.mRootContainer = new RelativeLayout(getContext());
        this.mRootContainer.setBackgroundColor(0);
        addContentView(this.mRootContainer, new ViewGroup.LayoutParams(-1, -1));
    }

    private void initLoadingDlg() {
        this.mLoadingDlg = new ProgressDialog(getContext());

        this.mLoadingDlg.requestWindowFeature(1);

        this.mLoadingDlg.setMessage(Lang.getString(this.mContext, 1));
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    private void initWebView() {
        this.mWebViewContainer = new RelativeLayout(getContext());
        this.mWebView = new WebView(getContext());
        this.mWebView.getSettings().setJavaScriptEnabled(true);

        this.mWebView.getSettings().setSavePassword(false);

        this.mWebView.requestFocus();
        this.mWebView.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
        this.mWebView.setVisibility(View.INVISIBLE);
        this.mWebView.setWebViewClient(new BBSWebViewClient());

        NetworkHelper.clearCookies(this.mContext, this.mAuthUrl);
        this.mWebView.loadUrl(this.mAuthUrl);

        RelativeLayout.LayoutParams webViewContainerLayout = new RelativeLayout.LayoutParams(-1, -1);

        RelativeLayout.LayoutParams webviewLayout = new RelativeLayout.LayoutParams(-1, -1);

        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        float density = dm.density;
        int margin = (int) (10.0F * density);
        //webviewLayout.setMargins(margin, margin, margin, margin);
        //Drawable background = ResourceManager.getNinePatchDrawable(this.mContext, 1);

        // @Deprecated
        //this.mWebViewContainer.setBackgroundDrawable(background);

        this.mWebViewContainer.addView(this.mWebView, webviewLayout);
//		this.mWebViewContainer.setGravity(17);

        webViewContainerLayout.setMargins(0, (int) (50.0F * dm.density), 0, 0);
        this.mRootContainer.addView(this.mWebViewContainer, webViewContainerLayout);
    }

    /*
     * func: handleRedirectUrl(String url)
     * des: handle the redirect url, generally the redirect url will contain access token
     * 		(include expire time) OR error CODE
     */
    private void handleRedirectUrl(String url) {
        Bundle values = Utility.parseUrl(url);

        // detect error info
        //String errorType = values.getString("error");
        //String errorCode = values.getString("error_code");
        String errorDescription = values.getString("error");

        // get the token information successfully
        if (errorDescription == null) {
            this.mListener.onComplete(values);
        } else {
            this.mListener.onException(new BBSAuthException(errorDescription));
        }
    }

    private class BBSWebViewClient extends WebViewClient {
        private boolean isCallBacked = false;

        private BBSWebViewClient() {
        }

        // Give the host application a chance to take over the control when a new url is about to be loaded in the
        // current WebView. If WebViewClient is not provided, by default WebView will ask Activity Manager to choose
        // the proper handler for the url. If WebViewClient is provided, return true means the host application
        // handles the url, while return false means the current WebView handles the url.
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            LogUtil.i("BBSDialog", "load URL: " + url);
            if (url.startsWith("sms:")) {
                Intent sendIntent = new Intent("android.intent.action.VIEW");
                sendIntent.putExtra("address", url.replace("sms:", ""));
                sendIntent.setType("vnd.android-dir/mms-sms");
                BBSDialog.this.getContext().startActivity(sendIntent);
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            LogUtil.d("BBSDialog", "onReceivedError: errorCode = " + errorCode
                    + ", description = " + description + ", failingUrl = "
                    + failingUrl);
            super.onReceivedError(view, errorCode, description, failingUrl);

            if (BBSDialog.this.mListener != null) {
                BBSDialog.this.mListener.onException(new BBSDialogException(description,
                        errorCode, failingUrl));
            }
            BBSDialog.this.dismiss();
        }

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            LogUtil.d("BBSDialog", "onPageStarted URL: " + url);
            // detect whether the page url is the call back url
            // if so, get the token OR error information from it
            if ((url.startsWith(BBSDialog.this.mbbs.getAuthInfo()
                    .getRedirect_uri())) && (!this.isCallBacked)) {
                this.isCallBacked = true;

                BBSDialog.this.handleRedirectUrl(url);
                view.stopLoading();// do not forget it
                BBSDialog.this.dismiss();
                return;
            }

            super.onPageStarted(view, url, favicon);

            if ((!BBSDialog.this.mIsDetached)
                    && (BBSDialog.this.mLoadingDlg != null)
                    && (!BBSDialog.this.mLoadingDlg.isShowing()))
                BBSDialog.this.mLoadingDlg.show();
        }

        public void onPageFinished(WebView view, String url) {
            LogUtil.d("BBSDialog", "onPageFinished URL: " + url);
            super.onPageFinished(view, url);
            if ((!BBSDialog.this.mIsDetached) && (BBSDialog.this.mLoadingDlg != null)) {
                // dismiss loading dialog when the page has laoded
                BBSDialog.this.mLoadingDlg.dismiss();
            }
            if (BBSDialog.this.mWebView != null)
                BBSDialog.this.mWebView.setVisibility(View.VISIBLE);
        }
    }
}
