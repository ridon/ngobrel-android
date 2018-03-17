package id.ridon.ngobrel.contoh.model;

import java.io.Serializable;

public class Person implements Serializable{
    private  String id;
    private  String name;
    private  String email;
    private  String job;
    private  String avatarUrl;
    private boolean isSelected = false;
    public Person()  {

    }

    public Person(String id, String name, String email, String job) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.job = job;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }
}
