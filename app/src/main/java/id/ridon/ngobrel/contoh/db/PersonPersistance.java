package id.ridon.ngobrel.contoh.db;

import io.realm.RealmObject;

public class PersonPersistance extends RealmObject{

    private String id;
    private String name;
    private String email;
    private String job;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }
}
