package logic.network;

import data.model.DataRs;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SrvRestApi {

    @GET("forecast.json")
    Call<DataRs> getData(@Query("key") String key, @Query("q") String q, @Query("lang") String lang, @Query("days") String days);

}