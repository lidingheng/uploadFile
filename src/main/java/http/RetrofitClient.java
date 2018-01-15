package http;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import util.L;

/**
 * Created by Administrator on 2018/1/12.
 */

public class RetrofitClient {

    private static Context mContext;
    private static final String baseurl=AppConfig.BASE_URL;
    private File httpCacheDirectory;
    private Cache mCache;
    private static OkHttpClient okHttpClient;
    private HttpLoggingInterceptor loggingInterceptor=null;
    private static final int DEFAULT_TIMEOUT = 20;
    private static Retrofit retrofit;
    private HttpService apiService;

    //限定为private 避免了在外部实例化
    private RetrofitClient(){};

    private RetrofitClient(Context mContext){
        this(mContext,baseurl,null);
    }

    private RetrofitClient(Context mContext,String url){

        this(mContext,baseurl,null);
    }

    private RetrofitClient(Context mContext, String url, Map<String,String> headers){

        if(TextUtils.isEmpty(url)){
            url=baseurl;
        }

        if(httpCacheDirectory==null){
            httpCacheDirectory=new File(mContext.getCacheDir(),"baoli_cache");
        }

        try {
            if (mCache==null){
                mCache=new Cache(httpCacheDirectory,10 * 1024 * 1024);
            }

        }catch (Exception e){
            Log.e("OKhttp","Could not create http cache",e);
        }

        okHttpClient=new OkHttpClient.Builder()
                .cookieJar(new NovateCookieManger(mContext))
                .cache(mCache)
                .addInterceptor(loggingInterceptor())
                .addInterceptor(new BaseInterceptor(headers))
                .addInterceptor(new CacheInterceptor(mContext))
                .addNetworkInterceptor(new CacheInterceptor(mContext))
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT,TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(8,60,TimeUnit.SECONDS))
                .build();

        retrofit=new Retrofit.Builder()
                .client(okHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(url)
                .build();
    }

    public static RetrofitClient getIntance(Context context){
        if (mContext==null){
            mContext=context;
        }
        return LazyHolder.retrofitClient1;
    }

    /*private static RetrofitClient retrofitClient=null;

    //静态工厂方法 懒汉式单例类,在第一次调用的时候实例化自己
    public static RetrofitClient getInstance1(){
        if(retrofitClient==null){
            retrofitClient=new RetrofitClient();
        }
        return retrofitClient;
    }

    *//**
     * 存在问题 没有考虑线程安全问题,它是线程不安全的,并发环境下很可能出现多个RetrofitClient实例
     *
     * 要实现线程安全有一下三种方法
     *//*



    //在getIntance方法上加同步
    public static synchronized RetrofitClient getIntance2(){
        if(retrofitClient==null){
            retrofitClient=new RetrofitClient();
        }
        return retrofitClient;
    }


    //双重检查锁定
    public static RetrofitClient getIntance3(){
        if (retrofitClient==null){
            synchronized (RetrofitClient.class){
                if (retrofitClient==null){
                    retrofitClient=new RetrofitClient();
                }
            }
        }
        return retrofitClient;
    }*/


    //静态内部类 这种比上面都好些,既实现线程安全,又避免了同步带来的性能影响
    private static class LazyHolder{
        private static final RetrofitClient retrofitClient1=new RetrofitClient(mContext);
    }

    public static final RetrofitClient getIntance4(){
        return LazyHolder.retrofitClient1;
    }




    //饿汉式单例  类创建的同时就已经创建好一个静态的实例,所以饿汉式是天生线程安全的
    /*private RetrofitClient retrofitClient=new RetrofitClient();
    public static RetrofitClient getIntance(){
        return retrofitClient;
    }*/

    private HttpLoggingInterceptor loggingInterceptor(){
        if(loggingInterceptor==null){
            loggingInterceptor=new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    L.e("TAG","--OkHttp---->>"+message);
                }
            });
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        }
        return loggingInterceptor;
    }

    public RetrofitClient createBaseApi(){
        apiService=create(HttpService.class);
        return this;
    }


    private RequestBody createJSON(Map<String,Object> maps){
        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"),new Gson().toJson(maps));
    }

    Observable.Transformer schedulersTransformer() {
        return new Observable.Transformer() {
            @Override
            public Object call(Object observable) {
                return ((Observable) observable).subscribeOn(rx.schedulers.Schedulers.io())
                        .unsubscribeOn(rx.schedulers.Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public <T> Observable.Transformer<BaseResponse<T>, T> transformer() {

        return new Observable.Transformer() {

            @Override
            public Object call(Object observable) {
                return ((Observable) observable).onErrorResumeNext(new HttpResponseFunc<T>());
//                return ((Observable) observable).map(new HandleFuc<T>()).onErrorResumeNext(new HttpResponseFunc<T>());
            }
        };
    }

    private static class HttpResponseFunc<T> implements Func1<Throwable, Observable<T>> {
        @Override
        public Observable<T> call(Throwable t) {
            return Observable.error(ExceptionHandle.handleException(t));
        }
    }



    public <T> T create(final Class<T> service){
        if(service==null){
            throw new RuntimeException("Api service is null!");
        }
        return retrofit.create(service);
    }


    public Subscription login(String url, Map<String,Object> maps, String lanuage, BaseSubscriber<ResponseBody> subscriber){
        return (Subscription) apiService.login(url,createJSON(maps),lanuage)
                .compose(schedulersTransformer())
                .compose(transformer())
                .subscribe(subscriber);
    }


}
