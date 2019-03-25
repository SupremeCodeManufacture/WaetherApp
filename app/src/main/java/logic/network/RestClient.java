package logic.network;

import com.supreme.manufacture.weather.BuildConfig;

import java.util.concurrent.TimeUnit;

import data.GenericConstants;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {

    private static SrvRestApi REST_CLIENT;

    static {
        setupRestClient();
    }

    public static SrvRestApi get() {
        return REST_CLIENT;
    }

    private static void setupRestClient() {
        OkHttpClient okHttpClient;

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(GenericConstants.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                    .addInterceptor(httpLoggingInterceptor)
                    .build();

        } else {
            okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(GenericConstants.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                    .build();
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GenericConstants.SERVER_DEF_ENDPOINT)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        REST_CLIENT = retrofit.create(SrvRestApi.class);
    }
}
