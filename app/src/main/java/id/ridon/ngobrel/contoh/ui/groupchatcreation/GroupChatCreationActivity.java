package id.ridon.ngobrel.contoh.ui.groupchatcreation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.qiscus.sdk.Qiscus;
import com.qiscus.sdk.data.model.QiscusChatRoom;
import com.qiscus.sdk.ui.QiscusGroupChatActivity;

import java.util.ArrayList;
import java.util.List;

import id.ridon.ngobrel.contoh.R;
import id.ridon.ngobrel.contoh.model.Person;
import id.ridon.ngobrel.contoh.repository.AlumnusRepository;
import id.ridon.ngobrel.contoh.repository.RepositoryTransactionListener;
import id.ridon.ngobrel.contoh.ui.privatechatcreation.PrivateChatCreationActivity;

public class GroupChatCreationActivity extends AppCompatActivity
        implements RepositoryTransactionListener, ViewHolder.OnContactClickedListener, View.OnClickListener,
            GroupNameDialogFragment.OnGroupNameCreatedListener,
            GroupInfoFragment.OnFragmentInteractionListener,
            GroupInfoFragment.MyEmailListener,SelectedViewHolder.OnContactClickedListener {

    private static final String TAG = "GroupChatCreationActivity";
    private RecyclerView mRecyclerView,mRecyclerViewSelected;
    private LinearLayoutManager mLinearLayoutManager,mLinearLayoutManagerSelected;
    private RecyclerAdapter mAdapter;
    private RecyclerSelectedAdapter mSelectedAdapter;
    private ArrayList<Person> alumnusList;
    private ArrayList<Person> selectedList = new ArrayList<>();
    private AlumnusRepository alumnusRepository;
    private ArrayList<String> contacts = new ArrayList<>();
    private FloatingActionButton nextFab;
    private ProgressDialog progressDialog;
    private SearchView searchView;
    private View underline;
    private TextView contactText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_alumni_list);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setVisibility(View.GONE);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        this.setTitle(getResources().getString(R.string.select_participants));

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        nextFab = findViewById(R.id.nextFloatingButton);
        mRecyclerView = findViewById(R.id.recyclerViewAlumni);
        mRecyclerViewSelected = findViewById(R.id.recyclerViewSelected);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManagerSelected = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerViewSelected.setLayoutManager(mLinearLayoutManagerSelected);
        alumnusRepository = new AlumnusRepository();
        alumnusRepository.setListener(this);
        progressDialog = new ProgressDialog(this);
        underline = findViewById(R.id.underline);
        underline.setVisibility(View.VISIBLE);
        contactText = findViewById(R.id.contacts_text);
        contactText.setVisibility(View.VISIBLE);
        mRecyclerViewSelected.setVisibility(View.VISIBLE);
        progressDialog.setMessage("Please wait...");
        ArrayList<Person> alumnusListTemp = alumnusRepository.getCachedData();
        for (Person person : alumnusListTemp) {
            if (person.isSelected())
                person.setSelected(false);
        }
        alumnusList = new ArrayList<Person>(alumnusListTemp);
        nextFab.setVisibility(View.VISIBLE);
        nextFab.setOnClickListener(this);
        //alumnusRepository.loadAll();
        mAdapter = new RecyclerAdapter(alumnusList, GroupChatCreationActivity.this);
        mRecyclerView.setAdapter(mAdapter);

        mSelectedAdapter = new RecyclerSelectedAdapter( selectedList, GroupChatCreationActivity.this);
        mRecyclerViewSelected.setAdapter(mSelectedAdapter);
    }

    @SuppressLint("LongLogTag")
    private void createGroupChat(String groupName) {
        progressDialog.show();
        Qiscus.buildGroupChatRoom(groupName,contacts).build(new Qiscus.ChatBuilderListener() {
            @Override
            public void onSuccess(QiscusChatRoom qiscusChatRoom) {
                progressDialog.dismiss();
                startActivity(QiscusGroupChatActivity.generateIntent(GroupChatCreationActivity.this, qiscusChatRoom));
                finish();
            }

            @Override
            public void onError(Throwable throwable) {
                progressDialog.dismiss();
                throwable.printStackTrace();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //if  (!isFragmentOn()) {
        //    alumnusRepository.loadAll();
        //}
    }

    private boolean isFragmentOn() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frameLayout);
        return !(currentFragment == null || !currentFragment.isVisible());

    }

    @Override
    public void onLoadAlumnusSucceeded(final List<Person> alumnus) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter = new RecyclerAdapter((ArrayList<Person>) alumnus, GroupChatCreationActivity.this);
                mRecyclerView.setAdapter(mAdapter);
            }
        });
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onContactSelected(String userEmail) {
        if (!contacts.contains(userEmail)) {
            setPersonSelected(userEmail, true);
        }

    }

    private void setPersonSelected(String userEmail, boolean selected) {
        for (Person person : alumnusList) {
            if (person.getEmail().toLowerCase().equals(userEmail.toLowerCase())) {
                person.setSelected(selected);
                if (selected) {
                  selectedList.add(person);
                }
                else {
                  selectedList.remove(person);
                }
                mSelectedAdapter.notifyDataSetChanged();
            }
        }
        if (selected) {
            contacts.add(userEmail);
        }
        else {
            contacts.remove(userEmail);
        }
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onContactUnselected(String userEmail) {
        if(contacts.contains(userEmail)) {
            setPersonSelected(userEmail, false);
        }

    }

    @Override
    public void onClick(View view) {
        if (isFragmentOn())
            {
                GroupInfoFragment currentFragment = (GroupInfoFragment) getSupportFragmentManager().findFragmentById(R.id.frameLayout);
                currentFragment.proceedCreateGroup();
            }
        else
            {
                searchView.onActionViewCollapsed();
                if (selectedContactIsMoreThanOne()){
                    selectedList.clear();
                    for (Person person: alumnusList){
                        for (String  email: contacts)
                        {
                            if (email.equals(person.getEmail())) {
                                selectedList.add(person);
                            }
                        }

                    }
                    Fragment fr =  GroupInfoFragment.newInstance(contacts,selectedList);
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.frameLayout, fr)
                            .addToBackStack( "tag" )
                            .commit();

                    //GroupNameDialogFragment dialogFragment = new GroupNameDialogFragment(this);
                    //dialogFragment.show(getFragmentManager(),"show_group_name");
                }else{
                    Toast.makeText(this, "select at least one", Toast.LENGTH_SHORT).show();
                }
            }

    }

    private boolean selectedContactIsMoreThanOne(){
        return this.contacts.size() > 0;
    }

    @Override
    public void onGroupNameCreated(String groupName) {
        createGroupChat(groupName);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.action_search)
        {

        }
        else {
            onReturn();
        }


        return true;
    }

    private void onReturn() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frameLayout);
        if (currentFragment == null || !currentFragment.isVisible() ) {

            startActivity(new Intent(this, PrivateChatCreationActivity.class));
            finish();
            overridePendingTransition(0,1);
        }
        else {
//            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//            fragmentTransaction.remove(currentFragment);
//
//            getSupportFragmentManager().popBackStack();
//            fragmentTransaction.commit();
            Toast.makeText(this,"remove fragment",Toast.LENGTH_SHORT);
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(currentFragment)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        onReturn();
    }


    @Override
    public void processPerson(String email, boolean selected) {
        setPersonSelected(email, selected);
        mAdapter = new RecyclerAdapter(alumnusList,GroupChatCreationActivity.this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSelectionUnselected(String userEmail) {
        processPerson(userEmail,false);
    }

    @Override

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override

            public boolean onQueryTextSubmit(String query) {

                query = query.toString().toLowerCase();

                final ArrayList<Person> filteredList = new ArrayList<>();

                for (int i = 0; i < alumnusList.size(); i++) {

                    final String text = alumnusList.get(i).getName().toLowerCase();
                    if (text.contains(query)) {

                        filteredList.add(alumnusList.get(i));
                    }
                }
                mAdapter = new RecyclerAdapter(filteredList,GroupChatCreationActivity.this);
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

                    final String text = alumnusList.get(i).getEmail().toLowerCase();
                    if (text.contains(newText)) {

                        filteredList.add(alumnusList.get(i));
                    }
                }
                mAdapter = new RecyclerAdapter(filteredList,GroupChatCreationActivity.this);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();  // data set changed

                return true;

            }

        });

        return super.onCreateOptionsMenu(menu);

    }
}
