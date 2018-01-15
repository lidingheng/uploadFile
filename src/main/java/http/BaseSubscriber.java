package http;

import android.content.Context;
import android.util.Log;

import rx.Subscriber;
import util.NetworkUtil;

/**
 * BaseSubscriber
 * Created by Tamic on 2016-08-03.
 */
public abstract class BaseSubscriber<T> extends Subscriber<T> {

    private Context context;
    private boolean isNeedCahe;

    public BaseSubscriber(Context context) {
        this.context = context;
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        Log.e("TAG", e.getMessage());

        if(e instanceof ExceptionHandle.ResponeThrowable){
            onError((ExceptionHandle.ResponeThrowable)e);
        } else {
            onError(new ExceptionHandle.ResponeThrowable(e, ExceptionHandle.ERROR.UNKNOWN));
        }
    }


    @Override
    public void onStart() {
        super.onStart();

        if (!NetworkUtil.isNetworkAvailable(context)) {
//            Tt.showShort(context,R.string.check_internet);
            onCompleted();
        }

    }

    @Override
    public void onCompleted() {

    }


    public abstract void onError(ExceptionHandle.ResponeThrowable e);

}
