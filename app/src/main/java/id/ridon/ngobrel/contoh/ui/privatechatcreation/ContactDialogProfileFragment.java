package id.ridon.ngobrel.contoh.ui.privatechatcreation;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qiscus.sdk.Qiscus;
import com.squareup.picasso.Picasso;

import id.ridon.ngobrel.contoh.R;
import id.ridon.ngobrel.contoh.model.Person;

@SuppressLint("ValidFragment")
public class ContactDialogProfileFragment extends DialogFragment implements View.OnClickListener {
    private static final String PERSON_KEY = "PERSON_KEY";
    TextView contactName, contactEmail;
    ImageView contactAvatar;
    RelativeLayout startChat;
    // TODO: Rename and change types of parameters
    private Person inputContact;

    @SuppressLint("ValidFragment")
    public ContactDialogProfileFragment(Person user) {
        this.inputContact = user;
    }


    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contact_profile, container,
                false);
        contactName = rootView.findViewById(R.id.contact_display_name);
        contactEmail = rootView.findViewById(R.id.contact_user_email);
        contactName.setText(inputContact.getName());
        contactEmail.setText(inputContact.getEmail());
        contactAvatar = rootView.findViewById(R.id.contact_picture);
        String avatarUrl = inputContact.getAvatarUrl();
        Picasso.with(this.contactAvatar.getContext()).load(avatarUrl).fit().centerCrop().into(contactAvatar);

        startChat = rootView.findViewById(R.id.startChat);
        startChat.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View view) {
        startSingleChat();
    }

    private void startSingleChat() {
        final String userEmail = inputContact.getEmail();
        Qiscus.buildChatWith(userEmail)
                .build(getActivity().getBaseContext(), new Qiscus.ChatActivityBuilderListener() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivity(intent);
                        getDialog().dismiss();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });


    }

}