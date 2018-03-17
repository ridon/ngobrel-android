package id.ridon.ngobrel.contoh.ui.groupchatcreation;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import id.ridon.ngobrel.contoh.R;
import id.ridon.ngobrel.contoh.model.Person;

public class SelectedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "SelectedViewHolder";
    private TextView itemName;
    private com.qiscus.sdk.ui.view.QiscusCircularImageView picture;
    //private ImageView picture;
    private Person selectedContact;
    private final SelectedViewHolder.OnContactClickedListener listener;
    private com.qiscus.sdk.ui.view.QiscusCircularImageView removeContact;

    public SelectedViewHolder(View itemView, SelectedViewHolder.OnContactClickedListener listener) {
        super(itemView);
        itemName = (TextView) itemView.findViewById(R.id.textViewName);
        picture = (com.qiscus.sdk.ui.view.QiscusCircularImageView) itemView.findViewById(R.id.imageViewProfile);
        removeContact = (com.qiscus.sdk.ui.view.QiscusCircularImageView) itemView.findViewById(R.id.remove_contact);
        this.listener = listener;

        removeContact.setOnClickListener(this);
        //itemView.setOnClickListener(this);

    }

    public void bindSelected(Person person) {

        this.selectedContact = person;
        this.itemName.setText(person.getName());
        String avatarUrl = person.getAvatarUrl();
        Picasso.with(this.picture.getContext()).load(avatarUrl).fit().centerCrop().into(picture);
        //Picasso.with(this.picture.getContext()).load("http://lorempixel.com/200/200/people/"+ person.getName()).into(picture);
    }

    @Override
    public void onClick(final View v) {

        //this.checkView.setVisibility(View.INVISIBLE);
        //if (v.getId() == R.id.remove_contact) {
        switch (v.getId()) {
            case R.id.remove_contact:
                this.listener.onSelectionUnselected(this.selectedContact.getEmail());
        }

        //}

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
    }

    public interface OnContactClickedListener {
        void onSelectionUnselected(String userEmail);
    }
}
