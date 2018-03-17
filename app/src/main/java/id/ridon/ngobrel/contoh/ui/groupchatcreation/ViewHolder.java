package id.ridon.ngobrel.contoh.ui.groupchatcreation;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import id.ridon.ngobrel.contoh.R;
import id.ridon.ngobrel.contoh.model.SelectableContact;

public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "ViewHolder";
    private TextView itemName;
    private TextView itemJob;
    private com.qiscus.sdk.ui.view.QiscusCircularImageView picture;
    private SelectableContact selectedContact;
    public CheckBox checkBox;
    public com.qiscus.sdk.ui.view.QiscusCircularImageView checkView;
    private final ViewHolder.OnContactClickedListener listener;

    public ViewHolder(View itemView, ViewHolder.OnContactClickedListener listener) {
        super(itemView);
        checkBox = itemView.findViewById(R.id.checkbox);
        itemName = itemView.findViewById(R.id.textViewName);
        itemJob = itemView.findViewById(R.id.textViewJob);
        picture = itemView.findViewById(R.id.imageViewProfile);
        checkView = itemView.findViewById(R.id.imageViewCheck);
        this.listener = listener;

        itemView.setOnClickListener(this);
        checkBox.setOnCheckedChangeListener(this);
    }

    public void bindAlumni(SelectableContact person) {
        this.checkBox.setChecked(person.isSelected());
        if (person.isSelected()) {
            this.checkView.setVisibility(View.VISIBLE);
        }
        else
        {
            this.checkView.setVisibility(View.INVISIBLE);
        }
        this.selectedContact = person;
        this.itemName.setText(person.getName());
        this.itemJob.setText(person.getJob());
        String avatarUrl = person.getAvatarUrl();
        Picasso.with(this.picture.getContext()).load(avatarUrl).fit().centerCrop().into(picture);
        //Picasso.with(this.picture.getContext()).load("http://lorempixel.com/200/200/people/"+ person.getName()).into(picture);
    }

    @Override
    public void onClick(final View v) {
        this.checkBox.setChecked(!this.selectedContact.isSelected());
        Log.d("CHECK", String.valueOf(this.checkBox.isChecked()));
        this.selectedContact.setSelected(this.checkBox.isChecked());

        if (this.checkBox.isChecked()) {
            this.checkView.setVisibility(View.VISIBLE);
            this.listener.onContactSelected(this.selectedContact.getEmail());
        } else {
            this.checkView.setVisibility(View.INVISIBLE);
            this.listener.onContactUnselected(this.selectedContact.getEmail());
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
    }

    public interface OnContactClickedListener {
        void onContactSelected(String userEmail);

        void onContactUnselected(String userEmail);
    }
}
