package id.ridon.ngobrel.contoh.repository;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    @GET("/api/contacts?show_all=true")
    Call<JsonObject> getContacts();

}
