package id.ridon.ngobrel.contoh.ui.privatechatcreation;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import id.ridon.ngobrel.contoh.R;
import id.ridon.ngobrel.contoh.model.Person;

public class RecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {
    private final ArrayList<Person> persons;
    private final ViewHolder.OnContactClickedListener listener;

    public RecyclerAdapter(ArrayList<Person> persons, ViewHolder.OnContactClickedListener listener) {
        this.persons = persons;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alumni, parent, false);
        return new ViewHolder(inflatedView, listener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Person person = persons.get(position);
        holder.bindAlumni(person);
    }

    @Override
    public int getItemCount() {
        return this.persons.size();
    }


}