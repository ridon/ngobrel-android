package id.ridon.ngobrel.contoh.util;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;

import com.qiscus.sdk.data.model.QiscusChatRoom;
import com.qiscus.sdk.data.model.QiscusComment;
import com.qiscus.sdk.ui.QiscusChatActivity;
import com.qiscus.sdk.ui.QiscusGroupChatActivity;
import com.qiscus.sdk.util.QiscusAndroidUtil;

/**
 * Created by catur on 1/11/18.
 */

public class ChatRoomNavigator {

    public static ChatRoomActivityBuilder openChatRoom (Context context, QiscusChatRoom qiscusChatRoom) {
        return new ChatRoomActivityBuilder(context, qiscusChatRoom);
    }

    public static ChatRoomActivityBuilder openChatQiscusCommentRoom (Context context, QiscusComment qiscusComment) {
        return new ChatRoomActivityBuilder(context, qiscusComment);
    }

    public static class ChatRoomActivityBuilder {
        private Context context;
        private QiscusComment qiscusComment;
        private QiscusChatRoom qiscusChatRoom;
        private Class parentClass;

        public ChatRoomActivityBuilder(Context context, QiscusComment qiscusComment) {
            this.context = context;
            this.qiscusComment = qiscusComment;
        }

        public ChatRoomActivityBuilder(Context context, QiscusChatRoom qiscusChatRoom) {
            this.context = context;
            this.qiscusChatRoom = qiscusChatRoom;
        }

        public ChatRoomActivityBuilder withParentClass(Class parentClass) {
            this.parentClass = parentClass;
            return this;
        }

        public void start() {
            QiscusAndroidUtil.runOnBackgroundThread(new Runnable() {
                @Override
                public void run() {
                    ChatRoomNavigator.start(ChatRoomActivityBuilder.this);
                }
            });
        }
    }

    private static void start(ChatRoomActivityBuilder builder) {
        if (builder.qiscusChatRoom != null) {
            openChatRoom(builder);
        } else if (builder.qiscusComment != null) {
            openQiscusCommentRoom(builder);
        } else {
            throw new RuntimeException("No matching candidate chatroom");
        }
    }

    private static void openQiscusCommentRoom(final ChatRoomActivityBuilder builder) {

        ChatRoomProvider.getChatRoom((int) builder.qiscusComment.getRoomId(),
                new ChatRoomProvider.Callback<QiscusChatRoom>() {
            @Override
            public void onCall(QiscusChatRoom qiscusChatRoom) {
                openChatRoomActivity(qiscusChatRoom, builder);
            }
        }, new ChatRoomProvider.Callback<Throwable>() {
            @Override
            public void onCall(Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    private static void openChatRoom(final ChatRoomActivityBuilder builder) {
        ChatRoomProvider.getChatRoom((int)builder.qiscusChatRoom.getId(),
                new ChatRoomProvider.Callback<QiscusChatRoom>() {
                    @Override
                    public void onCall(QiscusChatRoom qiscusChatRoom) {
                        openChatRoomActivity(qiscusChatRoom, builder);
                    }
                }, new ChatRoomProvider.Callback<Throwable>() {
                    @Override
                    public void onCall(Throwable call) {
                        call.printStackTrace();
                    }
                });

    }

    private static void openChatRoomActivity(QiscusChatRoom qiscusChatRoom,
                                             ChatRoomActivityBuilder builder) {
        Intent chatIntent;
        if (qiscusChatRoom.isGroup()) {
            chatIntent = QiscusGroupChatActivity.generateIntent(builder.context, qiscusChatRoom);
        } else {
            chatIntent = QiscusChatActivity.generateIntent(builder.context, qiscusChatRoom);
        }

        if (builder.parentClass == null) {
            startChatActivity(builder.context, chatIntent);
        } else {
            startChatActivity(builder.context, chatIntent, builder.parentClass);
        }
    }

    private static void startChatActivity(Context context, Intent chatIntent) {
        try {
            chatIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(chatIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void startChatActivity(Context context, Intent chatIntent, Class parentClass) {
        Intent parentIntent = new Intent(context, parentClass);
        chatIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addNextIntentWithParentStack(parentIntent);
        taskStackBuilder.addNextIntent(chatIntent);
        taskStackBuilder.startActivities();
    }
}
