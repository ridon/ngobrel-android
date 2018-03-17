package id.ridon.ngobrel.contoh.repository;

import java.util.List;

import id.ridon.ngobrel.contoh.model.Person;

public class CacheRepository implements Repository, CachedData {


    @Override
    public void loadAll(RepositoryCallback<List<Person>> callback) {
        callback.onSucceed(alumnus);
    }

    @Override
    public void save(List<Person> persons) {
        alumnus.addAll(persons);
    }

    @Override
    public void save(Person person) {

    }
}
