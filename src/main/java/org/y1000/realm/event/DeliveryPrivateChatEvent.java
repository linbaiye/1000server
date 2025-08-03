package org.y1000.realm.event;


import lombok.Getter;

public class DeliveryPrivateChatEvent implements RealmEvent {
    private final long id;
    private final String text;
    @Getter
    private final String toPlayerName;
    private final String fromPlayerName;

    public DeliveryPrivateChatEvent(long id, String text,
                                    String toPlayerName,
                                    String fromPlayerName) {
        this.id = id;
        this.text = text;
        this.toPlayerName = toPlayerName;
        this.fromPlayerName = fromPlayerName;
    }

    public long fromPlayerId() {
        return id;
    }

    public String formatDeliveredContent() {
        return fromPlayerName + ">：" + text ;
    }

    public String formatReplyContent() {
        return ">" + toPlayerName + "：" + text;
    }

    @Override
    public void accept(RealmEventHandler handler) {
        handler.deliverPrivateChat(this);
    }
}
