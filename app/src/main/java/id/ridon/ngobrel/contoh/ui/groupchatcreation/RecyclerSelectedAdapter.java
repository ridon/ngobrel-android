package id.ridon.ngobrel.contoh.ui.groupchatcreation;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import id.ridon.ngobrel.contoh.R;
import id.ridon.ngobrel.contoh.model.Person;

/**
 * Created by asyrof on 04/12/17.
 */


public class RecyclerSelectedAdapter extends RecyclerView.Adapter<SelectedViewHolder> {
    private ArrayList<Person> selectedContacts = new ArrayList<>();
    private final SelectedViewHolder.OnContactClickedListener listener;

    public RecyclerSelectedAdapter(ArrayList<Person> persons, SelectedViewHolder.OnContactClickedListener listener) {
        this.listener = listener;
        selectedContacts = persons;
    }

    @Override
    public SelectedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected, parent, false);
        return new SelectedViewHolder(inflatedView, listener);
    }

    @Override
    public void onBindViewHolder(SelectedViewHolder holder, int position) {
        final Person person = selectedContacts.get(position);
        holder.bindSelected(person);
    }


    @Override
    public int getItemCount() {
        return this.selectedContacts.size();
    }


}
