package http;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Administrator on 2018/1/12.
 */

public interface HttpService {

    @POST("{url}")
    Observable<ResponseBody> login(
            @Path("url") String url,
            @Body RequestBody jsonStr,
            @Header("Language") String language
            );

}
