package id.ridon.ngobrel.contoh.ui.groupchatcreation;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import id.ridon.ngobrel.contoh.R;

@SuppressLint("ValidFragment")
public class GroupNameDialogFragment extends DialogFragment {
    private EditText editText;
    private final OnGroupNameCreatedListener listener;

    @SuppressLint("ValidFragment")
    public GroupNameDialogFragment(OnGroupNameCreatedListener listener) {
        this.listener = listener;
    }


    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_group_name, container,
                false);
        getDialog().setTitle("Group name");
        this.editText = (EditText) rootView.findViewById(R.id.editText);
        Button buttonOk = (Button) rootView.findViewById(R.id.button3);
        Button buttonCancel = (Button) rootView.findViewById(R.id.button2);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editText.getText().toString().isEmpty()){
                    listener.onGroupNameCreated(editText.getText().toString());
                    dismiss();
                }
            }
        });
        return rootView;
    }

    public interface  OnGroupNameCreatedListener{
        void onGroupNameCreated(String groupName);
    }

}
