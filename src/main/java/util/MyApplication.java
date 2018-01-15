package util;

import android.app.Application;

import java.util.Locale;

/**
 * Created by Administrator on 2018/1/12.
 */

public class MyApplication extends Application{

    private static MyApplication instance=null;


    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
    }

    public String getLanguage() {
        Locale curLocale = getResources().getConfiguration().locale;
        return curLocale.getLanguage() + "-" + curLocale.getCountry();
    }

    public synchronized static MyApplication getIntance() {
        return instance;
    }
}
