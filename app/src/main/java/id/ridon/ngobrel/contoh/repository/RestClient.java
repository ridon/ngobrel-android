package id.ridon.ngobrel.contoh.repository;
import android.support.annotation.NonNull;


import id.ridon.ngobrel.contoh.util.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {
    private String url= Configuration.BASE_URL;
    private static RestClient ourInstance;

    private RestClient() {
    }

    public static RestClient getInstance() {
        if (ourInstance == null) ourInstance = new RestClient();
        return ourInstance;
    }

    public ApiService getApi(String baseUrl) {
        return getRetrofit(baseUrl).create(ApiService.class);
    }

    public ApiService getApi() {
        return getApi(url);
    }

    @NonNull
    private Retrofit getRetrofit(String baseUrl) {
        return new Retrofit
                .Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

}