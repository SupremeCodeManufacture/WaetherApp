package logic.network;

import data.model.DataRs;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SrvRestApi {

    @GET("current")
    Call<DataRs> getData(
            @Query("access_key") String key,
            @Query("query") String q,
            @Query("units") String units);
}