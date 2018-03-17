package id.ridon.ngobrel.contoh.util;

import android.util.Log;

import com.qiscus.sdk.data.model.QiscusComment;

/**
 * Created on : March 24, 2017
 * Author     : zetbaitsu
 * Name       : Zetra
 * GitHub     : https://github.com/zetbaitsu
 */
public class RealTimeChatroomHandler {
    private Listener listener;


    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void removeListener() {
        listener = null;
    }

    public void updateChatrooms(QiscusComment qiscusComment) {
        triggerListener(qiscusComment);
    }


    private void triggerListener(QiscusComment comment) {
        Log.d("TAG","YEAH");
        if (listener != null) {
            listener.onReceiveComment(comment);
        }

    }

    public interface Listener {
        void onReceiveComment(QiscusComment comment);


    }
}
