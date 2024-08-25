package org.y1000.realm.event;


import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.Player;
import org.y1000.message.PlayerTextEvent;

public record PrivateChatEvent(String receiverName,
                               String senderName,
                               String content,
                               Type type) implements RealmEvent {

    public static PrivateChatEvent send(String receiverName, String senderName, String content) {
        return new PrivateChatEvent(receiverName, senderName, content, Type.SEND);
    }

    private String formatReceiving() {
        if (type == Type.SEND) {
            return senderName + ">：" + content;
        } else if (type == Type.CONFIRM) {
            return ">" + senderName + "：" + content;
        } else if (type == Type.REJECT) {
            return receiverName + "拒绝接受纸条。";
        } else if (type == Type.NOT_FOUND) {
            return "玩家不在线。";
        }
        throw new IllegalStateException();
    }

    public boolean needConfirm() {
        return type == Type.SEND;
    }

    public PlayerTextEvent toTextEvent(Player player) {
        Validate.notNull(player);
        return PlayerTextEvent.privateChat(player, formatReceiving());
    }

    public PrivateChatEvent createConfirmation() {
        if (type == Type.SEND)
            return new PrivateChatEvent(senderName, receiverName, content, Type.CONFIRM);
        throw new IllegalStateException();
    }

    public PrivateChatEvent noRecipient() {
        if (type == Type.SEND)
            return new PrivateChatEvent(senderName(), "", "玩家不在线。", Type.NOT_FOUND);
        throw new IllegalStateException();
    }

    private enum Type {
        SEND,
        CONFIRM,
        REJECT,
        NOT_FOUND,
    }
}
