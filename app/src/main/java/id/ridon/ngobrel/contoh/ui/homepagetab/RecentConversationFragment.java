package id.ridon.ngobrel.contoh.ui.homepagetab;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.qiscus.sdk.Qiscus;
import com.qiscus.sdk.data.model.QiscusChatRoom;
import com.qiscus.sdk.data.model.QiscusComment;
import com.qiscus.sdk.data.remote.QiscusApi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import id.ridon.ngobrel.contoh.R;
import id.ridon.ngobrel.contoh.SampleApp;
import id.ridon.ngobrel.contoh.model.Room;
import id.ridon.ngobrel.contoh.ui.login.LoginActivity;
import id.ridon.ngobrel.contoh.ui.privatechatcreation.PrivateChatCreationActivity;
import id.ridon.ngobrel.contoh.util.EndlessRecyclerViewScrollListener;
import id.ridon.ngobrel.contoh.util.RealTimeChatroomHandler;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RecentConversationFragment extends Fragment implements RealTimeChatroomHandler.Listener {
    private static final String TAG = "RecentConversationsActi";
    private FloatingActionButton fabCreateNewConversation;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<Room> rooms = new ArrayList<>();
    private RecentConversationFragmentRecyclerAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout emptyRoomView;
    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.recent_conversation_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View v = getView();
        recyclerView        = v.findViewById(R.id.recyclerRecentConversation);
        linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        swipeRefreshLayout  = v.findViewById(R.id.swipeRefreshLayout);
        fabCreateNewConversation = v.findViewById(R.id.buttonCreateNewConversation);
        emptyRoomView   = v.findViewById(R.id.empty_room_view);
        adapter         = new  RecentConversationFragmentRecyclerAdapter(rooms);

        recyclerView.setLayoutManager(linearLayoutManager);

        fabCreateNewConversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PrivateChatCreationActivity.class);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                addNewConversation(++page);
            }
        };


        recyclerView.addOnScrollListener(scrollListener);
        //reloadRecentConversation();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reloadRecentConversation();
            }
        });
    }

    private void addNewConversation(final int page) {
        QiscusApi.getInstance().getChatRooms(page, 20, true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<QiscusChatRoom>>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted: ");
                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onNext(List<QiscusChatRoom> qiscusChatRooms) {
                        if (page==1) {
                            rooms.clear();
                        }
                        Log.d(TAG, "onNext: size" + qiscusChatRooms.size());
                        int roomCount = qiscusChatRooms.size();
                        if (roomCount > 0) {
                            emptyRoomView.setVisibility(View.GONE);
                        }
                        for (int i = 0; i < qiscusChatRooms.size(); i++) {
                            QiscusChatRoom currentChatRoom = qiscusChatRooms.get(i);
                            Room room = new Room((int) currentChatRoom.getId(), qiscusChatRooms.get(i).getName());
                            room.setLatestConversation(currentChatRoom.getLastComment().getMessage());
                            room.setOnlineImage(currentChatRoom.getAvatarUrl());
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                            SimpleDateFormat dateFormatToday = new SimpleDateFormat("hh:mm a");
                            Date messageDate = currentChatRoom.getLastComment().getTime();
                            String finalDateFormat = "";
                            if (DateUtils.isToday(messageDate.getTime())) {
                                finalDateFormat = dateFormatToday.format(currentChatRoom.getLastComment().getTime());
                            }
                            else {
                                finalDateFormat = dateFormat.format(currentChatRoom.getLastComment().getTime());
                            }
                            room.setLastMessageTime(finalDateFormat);
                            room.setUnreadCounter(currentChatRoom.getUnreadCount());
                            rooms.add(room);
                            /*if (!rooms.contains(room)) {
                                rooms.add(room);
                            }
                            else {
                                rooms.set(rooms.indexOf(room),room);
                            }*/
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!Qiscus.hasSetupUser()) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        } else {
            Log.d(TAG, "onResume: ");
            reloadRecentConversation();
        }
    }


    @Override
    public void onStart(){
        SampleApp.getInstance().getChatroomHandler().setListener(this);
        super.onStart();

    }
    @Override
    public void onPause() {
        super.onPause();
        SampleApp.getInstance().getChatroomHandler().removeListener();
    }
    public void reloadRecentConversation() {

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(true);
        }
        scrollListener.resetState();
        addNewConversation(1);
    }

    @Override
    public void onReceiveComment(QiscusComment comment) {
        int roomId= (int) comment.getRoomId();
        boolean isNewRoom = true;

        for(int i=0; i<rooms.size(); i++) {
          Room room = rooms.get(i);
          if ( room.getId() == roomId) {
            String finalDateFormat;
            int unread = room.getUnreadCounter();

            room.setUnreadCounter(unread+1);
            room.setLatestConversation(comment.getMessage());
            isNewRoom = false;
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat dateFormatToday = new SimpleDateFormat("hh:mm a");
            if (DateUtils.isToday(comment.getTime().getTime())) {
                finalDateFormat = dateFormatToday.format(comment.getTime());
            } else {
                finalDateFormat = dateFormat.format(comment.getTime());
            }
            room.setLastMessageTime(finalDateFormat);
            room.setOnlineImage(room.getOnlineImage());

            rooms.remove(i);
            adapter.notifyDataSetChanged();
            rooms.add(0,room);
            adapter.notifyDataSetChanged();
          }
        }

        if (isNewRoom) {
            Room room = new Room(roomId, comment.getRoomName());
            room.setLatestConversation(comment.getMessage());
            room.setOnlineImage(comment.getRoomAvatar());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat dateFormatToday = new SimpleDateFormat("hh:mm a");
            Date messageDate = comment.getTime();
            String finalDateFormat = "";
            if (DateUtils.isToday(messageDate.getTime())) {
                finalDateFormat = dateFormatToday.format(messageDate);
            }
            else {
                finalDateFormat = dateFormat.format(messageDate);
            }
            room.setLastMessageTime(finalDateFormat);
            room.setUnreadCounter(1);
            rooms.add(0,room);
            adapter.notifyDataSetChanged();
        }
    }
}