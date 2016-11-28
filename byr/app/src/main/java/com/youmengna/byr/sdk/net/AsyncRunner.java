package com.youmengna.byr.sdk.net;

import android.os.AsyncTask;

import com.youmengna.byr.sdk.exception.BBSException;
import com.youmengna.byr.sdk.exception.BBSHttpException;
import com.youmengna.byr.sdk.utils.LogUtil;

import org.json.JSONException;


/**
 * Created by ALSO on 2015/3/31.
 */
public class AsyncRunner {
    public static String request(String url, BBSParameters params, String httpMethod)
            throws BBSException, BBSHttpException {
        return HttpManager.openUrl(url, httpMethod, params);
    }

    public static void requestAsync(String url, BBSParameters params,
                                    String httpMethod, RequestListener listener) {
        LogUtil.i("ASYNC", url);
        new RequestRunner(url, params, httpMethod, listener).execute(new Void[1]);
    }

    private static class AsyncTaskResult<T> {
        private T result;
        private BBSException error;

        public AsyncTaskResult(T result) {
            this.result = result;
        }

        public AsyncTaskResult(BBSException error) {
            this.error = error;
        }

        public T getResult() {
            return this.result;
        }

        public BBSException getError() {
            return this.error;
        }
    }

    private static class RequestRunner extends AsyncTask<Void, Void, AsyncTaskResult<String>> {
        private final String mUrl;
        private final BBSParameters mParams;
        private final String mHttpMethod;
        private final RequestListener mListener;

        public RequestRunner(String url, BBSParameters params, String httpMethod, RequestListener listener) {
            this.mUrl = url;
            this.mParams = params;
            this.mHttpMethod = httpMethod;
            this.mListener = listener;
        }


        @SuppressWarnings({"rawtypes", "unchecked"})
        protected AsyncTaskResult<String> doInBackground(Void[] params) {
            try {
                String result = HttpManager.openUrl(this.mUrl, this.mHttpMethod, this.mParams);
                return new AsyncTaskResult(result);
            } catch (BBSException e) {
                return new AsyncTaskResult(e);
            }
        }

        protected void onPreExecute() {
        }

        protected void onPostExecute(AsyncTaskResult<String> result) {
            BBSException exception = result.getError();
            if (exception != null) {
                this.mListener.onException(exception);
            } else {
                try {
                    this.mListener.onComplete((String) result.getResult());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }// private static calss
}
