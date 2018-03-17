package id.ridon.ngobrel.contoh.repository;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import id.ridon.ngobrel.contoh.model.Person;

public class AlumnusRepository {
    private static final String TAG = "AlumnusRepository";
    private Repository cacheRepo;
    private Repository localRepo;
    private Repository remoteRepo;
    private RepositoryTransactionListener listener;
    public CachedData cachedData;

    public AlumnusRepository() {
        cacheRepo = new CacheRepository();
        localRepo = new LocalRepository();
        remoteRepo = new RemoteRepository();
    }

    public ArrayList<Person> getCachedData() {
        return ((CachedData) cacheRepo).alumnus;
    }

    public void setListener(RepositoryTransactionListener listener) {
        this.listener = listener;
    }

    public void loadAll(){
        Log.d(TAG, "loadAll: ");
        CachedData cachedData = (CachedData) cacheRepo;
        /*if (!cachedData.alumnus.isEmpty()){
            Log.d(TAG, "loadAll: load cache");
            this.listener.onLoadAlumnusSucceeded(cachedData.alumnus);
            return;
        }*/
        Log.d(TAG, "loadAll: localrepo");
        localRepo.loadAll(new RepositoryCallback<List<Person>>() {
            @Override
            public void onSucceed(List<Person> value) {
                reloadAll();
                /*if(!value.isEmpty()){
                    cacheRepo.save(value);
                    Log.d(TAG, "loadAll: load local");
                    listener.onLoadAlumnusSucceeded(value);
                }else{
                    reloadAll();
                }*/
            }

            @Override
            public void onFailed() {
                reloadAll();
            }
        });
    }
    public void reloadAll(){
        Log.d(TAG, "reloadAll: ");
        remoteRepo.loadAll(new RepositoryCallback<List<Person>>() {
            @Override
            public void onSucceed(final List<Person> value) {
                CachedData cachedData = (CachedData) cacheRepo;
                cachedData.alumnus.clear();
                cacheRepo.save(value);

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        //localRepo.save(value);
                    }
                });

                Log.d(TAG, "loadAll: load remote");
                listener.onLoadAlumnusSucceeded(value);
            }

            @Override
            public void onFailed() {

            }
        });
    }
}
