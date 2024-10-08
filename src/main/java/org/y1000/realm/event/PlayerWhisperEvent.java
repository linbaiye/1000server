package org.y1000.realm.event;


import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.Player;
import org.y1000.message.PlayerTextEvent;

public record PlayerWhisperEvent(String receiverName,
                                 String senderName,
                                 String content,
                                 Type type) implements RealmEvent {

    public static PlayerWhisperEvent send(String receiverName, String senderName, String content) {
        return new PlayerWhisperEvent(receiverName, senderName, content, Type.SEND);
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
        return PlayerTextEvent.whisper(player, formatReceiving());
    }

    public PlayerWhisperEvent createConfirmation() {
        if (type == Type.SEND)
            return new PlayerWhisperEvent(senderName, receiverName, content, Type.CONFIRM);
        throw new IllegalStateException();
    }

    public PlayerWhisperEvent noRecipient() {
        if (type == Type.SEND)
            return new PlayerWhisperEvent(senderName(), "", "玩家不在线。", Type.NOT_FOUND);
        throw new IllegalStateException();
    }

    private enum Type {
        SEND,
        CONFIRM,
        REJECT,
        NOT_FOUND,
    }
}
