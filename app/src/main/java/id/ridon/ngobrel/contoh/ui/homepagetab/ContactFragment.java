package id.ridon.ngobrel.contoh.ui.homepagetab;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.qiscus.sdk.Qiscus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import id.ridon.ngobrel.contoh.R;
import id.ridon.ngobrel.contoh.model.Person;
import id.ridon.ngobrel.contoh.repository.AlumnusRepository;
import id.ridon.ngobrel.contoh.repository.RepositoryTransactionListener;
import id.ridon.ngobrel.contoh.ui.privatechatcreation.ChatWithStrangerDialogFragment;
import id.ridon.ngobrel.contoh.ui.privatechatcreation.ContactDialogProfileFragment;
import id.ridon.ngobrel.contoh.ui.privatechatcreation.RecyclerAdapter;
import id.ridon.ngobrel.contoh.ui.privatechatcreation.ViewHolder;
import retrofit2.HttpException;

public class ContactFragment extends Fragment implements RepositoryTransactionListener, ViewHolder.OnContactClickedListener,ChatWithStrangerDialogFragment.onStrangerNameInputtedListener{
    private static final String TAG = "PrivateChatCreationActivity";
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerAdapter mAdapter;
    private ArrayList<Person> alumnusList;
    private AlumnusRepository alumnusRepository;
    private LinearLayout mEmptyRoomVIew;
    private SwipeRefreshLayout swipeContactRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.contact_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        View v = getView();
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerViewAlumni);
        mLinearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        alumnusRepository = new AlumnusRepository();
        alumnusRepository.setListener(this);
        mEmptyRoomVIew = (LinearLayout) v.findViewById(R.id.empty_contact_view);
        swipeContactRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeContactRefreshLayout);
        alumnusList = alumnusRepository.getCachedData();
        mAdapter = new RecyclerAdapter(alumnusList, this);
        mRecyclerView.setAdapter(mAdapter);
        alumnusRepository.loadAll();
        //Log.d("SIZE", String.valueOf(alumnusList.size()));
        swipeContactRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                alumnusRepository.loadAll();

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onLoadAlumnusSucceeded(List<Person> alumnus) {
        if (swipeContactRefreshLayout != null) {
            swipeContactRefreshLayout.setRefreshing(false);
        }
        if (alumnusList.isEmpty()) {
            mEmptyRoomVIew.setVisibility(View.VISIBLE);
        }
        else {
            mEmptyRoomVIew.setVisibility(View.INVISIBLE);
        }
        //Toast.makeText(this.getContext(), String.valueOf(alumnusList.size()), Toast.LENGTH_SHORT).show();
        FragmentActivity fragmentActivity = getActivity();
        if (fragmentActivity != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyDataSetChanged();
                }
            });
        }

    }

    @Override
    public void onContactClicked(final Person user) {
        ContactDialogProfileFragment dialogFragment = new ContactDialogProfileFragment(user);
        dialogFragment.show(getActivity().getFragmentManager(),"ea");
    }

    public void onContactClicked2(final String userEmail) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Confirmation")
                .setMessage("Are you sure to make a conversation with "+userEmail+" ?")
                .setCancelable(true)
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Qiscus.buildChatWith(userEmail)
                                .build(getActivity(), new Qiscus.ChatActivityBuilderListener() {
                                    @Override
                                    public void onSuccess(Intent intent) {
                                        startActivity(intent);
                                        //finish();
                                    }

                                    @Override
                                    public void onError(Throwable throwable) {
                                        if (throwable instanceof HttpException) { //Error response from server
                                            HttpException e = (HttpException) throwable;
                                            try {
                                                String errorMessage = e.response().errorBody().string();
                                                Log.e(TAG, errorMessage);
                                                showError(errorMessage);
                                            } catch (IOException e1) {
                                                e1.printStackTrace();
                                            }
                                        } else if (throwable instanceof IOException) { //Error from network
                                            showError("Can not connect to qiscus server!");
                                        } else { //Unknown error
                                            showError("Unexpected error!");
                                        }
                                    }
                                });
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();

    }

    @Override
    public void onStrangerNameInputted(String email) {
        Qiscus.buildChatWith(email)
                .build(getActivity().getApplicationContext(), new Qiscus.ChatActivityBuilderListener() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivity(intent);
                    }
                    @Override
                    public void onError(Throwable throwable) {
                        if (throwable instanceof HttpException) { //Error response from server
                            HttpException e = (HttpException) throwable;
                            try {
                                String errorMessage = e.response().errorBody().string();
                                //Log.e(TAG, errorMessage);
                                showError(errorMessage);
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        } else if (throwable instanceof IOException) { //Error from network
                            showError("Can not connect to qiscus server!");
                        } else { //Unknown error
                            showError("Unexpected error!");
                        }
                    }
                });
    }

    private void showError(String error) {
        Toast.makeText(getActivity().getApplicationContext(),error,Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);


        MenuItem searchItem = menu.findItem(R.id.action_search);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        final SearchView.OnCloseListener closeListener = new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                int count = getActivity().getSupportFragmentManager().getBackStackEntryCount();
                Toast.makeText(getActivity().getBaseContext(),String.valueOf(count),Toast.LENGTH_SHORT).show();
                if (count != 0) {
                    getActivity().getSupportFragmentManager().popBackStack();}

                return true;
            }
        };

        searchView.setOnCloseListener(closeListener);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override

            public boolean onQueryTextSubmit(String query) {

                query = query.toString().toLowerCase();

                final ArrayList<Person> filteredList = new ArrayList<>();

                for (int i = 0; i < alumnusList.size(); i++) {

                    final String text = alumnusList.get(i).getEmail().toLowerCase();
                    if (text.contains(query)) {

                        filteredList.add(alumnusList.get(i));
                    }
                }
                mAdapter = new RecyclerAdapter(filteredList,ContactFragment.this);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();  // data set changed

                searchView.clearFocus();



                return true;

            }



            @Override

            public boolean onQueryTextChange(String newText) {
                newText = newText.toString().toLowerCase();

                final ArrayList<Person> filteredList = new ArrayList<>();

                for (int i = 0; i < alumnusList.size(); i++) {

                    final String text = alumnusList.get(i).getName().toLowerCase();
                    if (text.contains(newText)) {

                        filteredList.add(alumnusList.get(i));
                    }
                }
                mAdapter = new RecyclerAdapter(filteredList,ContactFragment.this);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();  // data set changed

                return true;

            }

        });
        super.onCreateOptionsMenu(menu, inflater);

    }

}