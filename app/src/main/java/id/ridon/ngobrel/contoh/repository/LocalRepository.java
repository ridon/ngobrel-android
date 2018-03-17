package id.ridon.ngobrel.contoh.repository;

import java.util.ArrayList;
import java.util.List;

import id.ridon.ngobrel.contoh.model.Person;
import id.ridon.ngobrel.contoh.db.PersonPersistance;
import io.realm.Realm;
import io.realm.RealmResults;

public class LocalRepository implements Repository {
    private static final String TAG = "LocalRepository";

    public Realm realm;

    public LocalRepository() {
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void loadAll(RepositoryCallback<List<Person>> callback) {
        RealmResults<PersonPersistance> personPersistances = realm.where(PersonPersistance.class).findAll();
        ArrayList<Person> alumnus = new ArrayList<>();
        for (int i = 0; i < personPersistances.size(); i++) {
            PersonPersistance item = personPersistances.get(i);
            alumnus.add(new Person(item.getId(),item.getName(),item.getEmail(),item.getJob()));
        }
        callback.onSucceed(alumnus);
    }

    @Override
    public void save(final List<Person> persons) {
        realm.beginTransaction();
        for (Person p :
                persons) {
            PersonPersistance personPersistance;
            personPersistance = realm.where(PersonPersistance.class).equalTo("id", p.getId()).findFirst();
            if (personPersistance == null) {
                personPersistance = realm.createObject(PersonPersistance.class);
            }

            personPersistance.setEmail(p.getEmail());
            personPersistance.setId(p.getId());
            personPersistance.setJob(p.getJob());
            personPersistance.setName(p.getName());
        }
        realm.commitTransaction();
    }

    @Override
    public void save(Person person) {

    }
}
