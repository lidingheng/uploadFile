package http;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/1/12.
 */

public class BaseInterceptor implements Interceptor{

    private Map<String, String> headers;

    public BaseInterceptor(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        if (headers != null && headers.size() > 0) {
            Set<String> keySet = headers.keySet();
            for (String headKey : keySet)
                builder.addHeader(headKey,headers.get(headKey)).build();
        }
        return chain.proceed(builder.build());
    }
}
