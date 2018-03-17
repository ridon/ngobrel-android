package id.ridon.ngobrel.contoh.ui.homepagetab;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import id.ridon.ngobrel.contoh.R;
import id.ridon.ngobrel.contoh.model.Room;

public class RecentConversationFragmentRecyclerAdapter extends RecyclerView.Adapter<id.ridon.ngobrel.contoh.ui.homepagetab.RecentConversationFragmentHolder> {
    private ArrayList<Room> rooms;

    public RecentConversationFragmentRecyclerAdapter(ArrayList<Room> rooms) {
        this.rooms = rooms;
    }

    @Override
    public id.ridon.ngobrel.contoh.ui.homepagetab.RecentConversationFragmentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recent_conversation, parent, false);
        return new id.ridon.ngobrel.contoh.ui.homepagetab.RecentConversationFragmentHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(id.ridon.ngobrel.contoh.ui.homepagetab.RecentConversationFragmentHolder holder, int position) {
        Room room = rooms.get(position);
        holder.bindRecentConversation(room);
    }

    @Override
    public int getItemCount() {
        return this.rooms.size();
    }
}
