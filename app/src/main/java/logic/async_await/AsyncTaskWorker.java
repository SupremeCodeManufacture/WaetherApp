package logic.async_await;

import android.os.AsyncTask;

import java.util.concurrent.Callable;


public class AsyncTaskWorker extends AsyncTask<Void, Void, Object> {

    private Callable<Void> methodToRunNoRs;
    private OnAsyncDoneNoRsListener mNoRsListener;

    private CallableObj mMethodToRunWithRs;
    private OnAsyncDoneRsObjListener mObjRsListener;


    public AsyncTaskWorker(Callable<Void> heavyMethod, OnAsyncDoneNoRsListener listener) {
        this.methodToRunNoRs = heavyMethod;
        this.mNoRsListener = listener;
    }

    public AsyncTaskWorker(CallableObj heavyMethod, OnAsyncDoneRsObjListener listener) {
        this.mMethodToRunWithRs = heavyMethod;
        this.mObjRsListener = listener;
    }

    @Override
    protected Object doInBackground(Void... v) {
        try {
            if (mObjRsListener != null) {
                return mMethodToRunWithRs.call();

            } else {
                methodToRunNoRs.call();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        if (mNoRsListener != null)
            mNoRsListener.onDone();

        if (mObjRsListener != null)
            mObjRsListener.onDone(o);
    }
}
