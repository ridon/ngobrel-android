package id.ridon.ngobrel.contoh.repository;

import java.util.List;

import id.ridon.ngobrel.contoh.model.Person;

/**
 * Created by omayib on 22/09/17.
 */

public interface RepositoryTransactionListener {
    void onLoadAlumnusSucceeded(List<Person> alumnus);
}
