package id.ridon.ngobrel.contoh.ui.groupchatcreation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.qiscus.sdk.Qiscus;
import com.qiscus.sdk.data.local.QiscusCacheManager;
import com.qiscus.sdk.data.model.QiscusChatRoom;
import com.qiscus.sdk.data.remote.QiscusApi;
import com.qiscus.sdk.ui.QiscusGroupChatActivity;
import com.qiscus.sdk.util.QiscusImageUtil;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import id.ridon.ngobrel.contoh.R;
import id.ridon.ngobrel.contoh.model.Person;
import id.ridon.ngobrel.contoh.util.FileUtil;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class GroupInfoFragment extends Fragment implements View.OnClickListener,ViewHolder.OnContactClickedListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static String CONTACT_KEY = "CONTACT_KEY";
    private static String PERSON_KEY = "PERSON_KEY";
    private static String selectMore = "select at least one";
    private static String groupNameFormat = "Please input group name";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1000;
    private final int GET_GALLERY_IMAGE_REQUEST_CODE = 1001;
    com.qiscus.sdk.ui.view.QiscusCircularImageView uploadIcon;
    private MyEmailListener emailListener;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerAdapter mAdapter;
    private ProgressDialog progressDialog;
    private ArrayList<String> contacts = new ArrayList<>();
    private ArrayList<Person> personList;
    // TODO: Rename and change types of parameters
    private String avatarUrl = "";
    private OnFragmentInteractionListener mListener;
    private Toast mToast;
    private FloatingActionButton nextFab;
    private EditText groupNameView;
    private View mProgressView;

    public GroupInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GroupInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GroupInfoFragment newInstance(ArrayList<String> contactsList, ArrayList<Person> personList) {
        GroupInfoFragment fragment = new GroupInfoFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(CONTACT_KEY, contactsList);
        fragment.setArguments(bundle);

        bundle.putSerializable(PERSON_KEY, personList);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        contacts = (ArrayList<String>) getArguments().getSerializable(
                CONTACT_KEY);
        personList = (ArrayList<Person>) getArguments().getSerializable(
                PERSON_KEY);
        android.support.v7.app.ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Add Group Info");
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");

        View view = inflater.inflate(R.layout.fragment_group_info, container, false);
        nextFab = (FloatingActionButton) view.findViewById(R.id.nextFragmentFloatingButton);
        nextFab.setOnClickListener(this);
        mProgressView = view.findViewById(R.id.upload_progress);
        uploadIcon = (com.qiscus.sdk.ui.view.QiscusCircularImageView) view.findViewById(R.id.upload_icon);
        uploadIcon.setOnClickListener(this);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewSelected);
        mLinearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        groupNameView = (EditText) view.findViewById(R.id.group_name_input);
        mAdapter = new RecyclerAdapter(personList, this,true);
        mRecyclerView.setAdapter(mAdapter);
        return view;//  inflater.inflate(R.layout.fragment_group_info, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        if (context instanceof MyEmailListener) {
            emailListener = (MyEmailListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement MyEmailListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {



        switch (view.getId()) {
            case R.id.upload_icon:
                PopupMenu popup = new PopupMenu(getActivity(), uploadIcon);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.menu_upload, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.choose_picture) {
                            openGallery();
                        } else if (item.getItemId() == R.id.take_picture) {
                            openCamera();
                        }

                        return true;
                    }
                });

                popup.show();
                break;

            /*case R.id.nextFloatingButton:
                proceedCreateGroup();
                break;*/

            default:
              break;

        }


    }

    public void proceedCreateGroup() {
        String groupName = groupNameView.getText().toString();
        boolean groupNameInputted = groupName.trim().length() > 0;
        if (groupNameInputted && selectedContactIsMoreThanOne()) {
            createGroupChat(groupName);


            //GroupNameDialogFragment dialogFragment = new GroupNameDialogFragment(this);
            //dialogFragment.show(getFragmentManager(),"show_group_name");
        } else {
            String warningText = (groupNameInputted) ? selectMore : groupNameFormat;
            showToast(warningText);
        }
    }

    private boolean selectedContactIsMoreThanOne() {
        return this.contacts.size() > 0;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);


            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            View view = getActivity().findViewById(R.id.nextFloatingButton);
            view.setVisibility(show ? View.GONE : View.VISIBLE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            View view = getActivity().findViewById(R.id.nextFloatingButton);
            view.setVisibility(show ? View.GONE : View.VISIBLE);

        }
    }

    @SuppressLint("LongLogTag")
    private void createGroupChat(String groupName) {
        progressDialog.show();
        Qiscus.buildGroupChatRoom(groupName, contacts)
                .withAvatar(avatarUrl)
                .build(new Qiscus.ChatBuilderListener() {
                    @Override
                    public void onSuccess(QiscusChatRoom qiscusChatRoom) {
                        progressDialog.dismiss();
                        //startActivity(QiscusGroupChatActivity.generateIntent(getActivity(), qiscusChatRoom));
                        Intent intent = new Intent(QiscusGroupChatActivity.generateIntent(getActivity(), qiscusChatRoom));
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        getActivity().finish();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        progressDialog.dismiss();
                        throwable.printStackTrace();
                    }
                });
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = QiscusImageUtil.createImageFile();
            } catch (IOException ex) {
                showToast("Failed to write temporary picture!");
            }

            if (photoFile != null) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                } else {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
                            FileProvider.getUriForFile(getActivity(), Qiscus.getProviderAuthorities(), photoFile));
                }
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, GET_GALLERY_IMAGE_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_GALLERY_IMAGE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri selectedImage = data.getData();
                String imagePath = FileUtil.getRealPathFromUri(getContext(), selectedImage);

                processImage(imagePath);
            }
        } else if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                Uri processedPhoto = (Uri.parse(QiscusCacheManager.getInstance().getLastImagePath()));
                String imagePath = FileUtil.getRealPathFromUri(getContext(), processedPhoto);


                processImage(imagePath);
            }

        }
    }

    private void processImage(String imagePath) {
        File theFile = new File(imagePath);
        showProgress(true);
        sendFile(theFile, new Callback() {
            @Override
            public void onSuccessGetUri(String uri) {
                avatarUrl = uri;
                ImageView groupPictureHolder = (ImageView) getView().findViewById(R.id.group_avatar);

                Picasso.with(groupPictureHolder.getContext()).load(avatarUrl).fit().centerCrop().into(groupPictureHolder);
                showProgress(false);
            }

            @Override
            public void onFailiedGetUri(Throwable throwable) {
                showProgress(false);
                showToast("An error occured please try again");
            }
        });
    }

    public void sendFile(File file, final Callback callback) {
        QiscusApi.getInstance().uploadFile(file, new QiscusApi.ProgressListener() {
            @Override
            public void onProgress(long total) {

            }
        })
                .map(new Func1<Uri, String>() {
                    @Override
                    public String call(Uri uri) {
                        return uri.toString();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String uri) {
                        callback.onSuccessGetUri(uri);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        callback.onFailiedGetUri(throwable);
                    }
                });
    }

    public void showToast(String message) {
        if (mToast != null) mToast.cancel();
        mToast = Toast.makeText(getActivity(), message, Toast.LENGTH_LONG);
        mToast.show();
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onContactSelected(String userEmail) {
        if (!contacts.contains(userEmail)) {
            contacts.add(userEmail);
            this.emailListener.processPerson(userEmail,true);
        }

    }

    @SuppressLint("LongLogTag")
    @Override
    public void onContactUnselected(String userEmail) {
        if(contacts.contains(userEmail)) {
            contacts.remove(userEmail);
            this.emailListener.processPerson(userEmail,false);
        }
    }

    public interface Callback {
        void onSuccessGetUri(String uri);

        void onFailiedGetUri(Throwable throwable);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public interface MyEmailListener{
        void processPerson(String email,boolean selected);
    }

    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater){
        menu.clear();
    }
}


