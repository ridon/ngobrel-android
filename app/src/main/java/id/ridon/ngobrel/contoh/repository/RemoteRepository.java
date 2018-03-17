package id.ridon.ngobrel.contoh.repository;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.qiscus.sdk.Qiscus;

import java.util.ArrayList;
import java.util.List;

import id.ridon.ngobrel.contoh.model.Person;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RemoteRepository  implements Repository {
    private static final String TAG = "RemoteRepository";
    public RemoteRepository() {

    }
    ArrayList<Person> alumnus = new ArrayList<>();

    @Override
    public void loadAll(final RepositoryCallback<List<Person>> callback) {
        RestClient.getInstance()
                .getApi()
                .getContacts()
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            if (response.isSuccessful()) {
                                JsonObject body = response.body();

                                alumnus = parseAlumnus(body);

                                Log.d("BODY","YES WE ARE PARSING ALUMNUS");
                                callback.onSucceed(alumnus);
                            } else {

                            }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {

                    }
                });

        /*
        alumnus.add(new Person(UUID.randomUUID().toString(),"Haris","Haris@email.com","Android Programmer"));
        alumnus.add(new Person(UUID.randomUUID().toString(),"Anang","Anang@email.com","Web Programmer"));
        alumnus.add(new Person(UUID.randomUUID().toString(),"Satya","Satya@email.com","Backend Programmer"));
        alumnus.add(new Person(UUID.randomUUID().toString(),"Henri","Henri@email.com","Backend Programmer"));
        alumnus.add(new Person(UUID.randomUUID().toString(),"Desi","Desi@email.com","Pengacara"));
        alumnus.add(new Person(UUID.randomUUID().toString(),"Sutris","Sutris@email.com","Desktop Programmer"));
        alumnus.add(new Person(UUID.randomUUID().toString(),"Dina","Dina@email.com","Android Programmer"));
        alumnus.add(new Person(UUID.randomUUID().toString(),"Winda","Winda@email.com","Android Programmer"));
        */

    }

    private ArrayList<Person> parseAlumnus(JsonObject body) {
        ArrayList<Person> people = new ArrayList<>();
        if (!Qiscus.hasSetupUser()) {
            return people;
        }
        String currentUsername = Qiscus.getQiscusAccount().getUsername();
        JsonArray userArray = body.get("results").getAsJsonObject().get("users").getAsJsonArray();

        for(JsonElement element: userArray){
            JsonObject personElement = element.getAsJsonObject();
            String username = personElement.get("username").getAsString();
            if (!currentUsername.equals(username)) {
                Person person = new Person();
                person.setId(personElement.get("id").getAsString());
                person.setName(personElement.get("name").getAsString());
                person.setEmail(personElement.get("email").getAsString());
                person.setJob(personElement.get("name").getAsString());
                person.setAvatarUrl(personElement.get("avatar_url").getAsString());
                people.add(person);
            }
        }

        return people;
    }

    @Override
    public void save(List<Person> persons) {

    }

    @Override
    public void save(Person person) {

    }

    private void loadContent() {

    }
}
