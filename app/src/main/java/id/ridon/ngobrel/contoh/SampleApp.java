package id.ridon.ngobrel.contoh;

import android.app.Application;
import android.content.Context;

import com.qiscus.sdk.Qiscus;
import com.qiscus.sdk.data.model.NotificationClickListener;
import com.qiscus.sdk.data.model.QiscusComment;
import com.qiscus.sdk.event.QiscusCommentReceivedEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import id.ridon.ngobrel.contoh.ui.homepagetab.HomePageTabActivity;
import id.ridon.ngobrel.contoh.util.ChatRoomNavigator;
import id.ridon.ngobrel.contoh.util.Configuration;
import id.ridon.ngobrel.contoh.util.RealTimeChatroomHandler;
import io.realm.Realm;

public class SampleApp extends Application {
    private RealTimeChatroomHandler chatroomHandler;
    private static SampleApp INSTANCE;

    public static SampleApp getInstance() {
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        Qiscus.init(this, Configuration.QISCUS_APP_ID);
        chatroomHandler = new RealTimeChatroomHandler();

        Qiscus.getChatConfig()
                .setStatusBarColor(R.color.colorPrimaryDark)
                .setAppBarColor(R.color.colorPrimary)
                .setLeftBubbleColor(R.color.emojiSafeYellow)
                .setRightBubbleColor(R.color.colorPrimary)
                .setRightBubbleTextColor(R.color.qiscus_white)
                .setRightBubbleTimeColor(R.color.qiscus_white)
                .setReadIconColor(R.color.colorAccent)
                .setEmptyRoomImageResource((R.drawable.ic_room_empty))
                .setNotificationBigIcon(R.drawable.ic_logo_ngobrel_white_no_background)
                .setNotificationClickListener(new NotificationClickListener() {
                    @Override
                    public void onClick(Context context, QiscusComment qiscusComment) {
                        ChatRoomNavigator
                                .openChatQiscusCommentRoom(context, qiscusComment)
                                .withParentClass(HomePageTabActivity.class)
                                .start();
                    }
                })
                .setEnableAddLocation(true)
                .setEmptyRoomTitleColor(R.color.orangeIcon)
                .setAccentColor(R.color.colorAccent)
                .setEnableEndToEndEncryption(true)
                .getDeleteCommentConfig().setEnableDeleteComment(true);

        Realm.init(this);

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Subscribe
    public void onReceivedComment(QiscusCommentReceivedEvent event) {
        chatroomHandler.updateChatrooms(event.getQiscusComment());
    }

    public RealTimeChatroomHandler getChatroomHandler() {
        return chatroomHandler;
    }
}
