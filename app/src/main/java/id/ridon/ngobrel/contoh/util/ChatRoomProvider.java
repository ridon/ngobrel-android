package id.ridon.ngobrel.contoh.util;

import com.qiscus.sdk.Qiscus;
import com.qiscus.sdk.data.model.QiscusChatRoom;
import com.qiscus.sdk.data.remote.QiscusApi;
import com.qiscus.sdk.util.QiscusRxExecutor;

/**
 * Created by catur on 1/11/18.
 */

public class ChatRoomProvider {

    public static void getChatRoom(int id, final Callback<QiscusChatRoom> onSuccess,
                                   final Callback<Throwable> onFailure) {
        QiscusChatRoom savedChatRoom = Qiscus.getDataStore().getChatRoom(id);

        if (savedChatRoom != null) {
            onSuccess.onCall(savedChatRoom);
        } else {
            //fetching API when we dont have any qiscus chat room in qiscus database
            QiscusRxExecutor.execute(QiscusApi
                            .getInstance().getChatRoom(id),
                    new QiscusRxExecutor.Listener<QiscusChatRoom>() {
                        @Override
                        public void onSuccess(QiscusChatRoom qiscusChatRoom) {
                            Qiscus.getDataStore().addOrUpdate(qiscusChatRoom);
                            onSuccess.onCall(qiscusChatRoom);
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            throwable.printStackTrace();
                            onFailure.onCall(throwable);
                        }
                    });
        }
    }

    public interface Callback<T> {
        void onCall(T call);
    }
}
