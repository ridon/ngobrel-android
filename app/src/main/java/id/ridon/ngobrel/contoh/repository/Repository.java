package id.ridon.ngobrel.contoh.repository;

import java.util.List;

import id.ridon.ngobrel.contoh.model.Person;

public interface Repository {
    void loadAll(RepositoryCallback<List<Person>> callback);
    void save(List<Person> persons);
    void save(Person person);
}
