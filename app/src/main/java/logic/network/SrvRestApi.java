package logic.network;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface SrvRestApi {

    @GET("api/v1/{reqPath}")
    Call<ResponseBody> getSalonsReq(@Path(value = "reqPath", encoded = true) String reqPath, @QueryMap(encoded = true) Map<String, String> params, @Query("services[]") List<String> filters);

    @GET("api/v1/{reqPath}")
    Call<ResponseBody> getReq(@Path(value = "reqPath", encoded = true) String reqPath);

    @FormUrlEncoded
    @Headers({"Accept: application/json", "Content-Type: application/x-www-form-urlencoded"})
    @POST("api/v1/{reqPath}")
    Call<ResponseBody> postReqNew(@Path(value = "reqPath", encoded = true) String reqPath, @FieldMap Map<String, String> params);
}