package org.y1000.realm.event;


public record DeliveryPrivateChatResultEvent(boolean delivered, DeliveryPrivateChatEvent source) implements RealmEvent {
    @Override
    public void accept(RealmEventHandler handler) {
        handler.deliverPrivateChatResult(source().fromPlayerId(), delivered ? source.formatReplyContent() : "玩家不在线。");
    }

    public static DeliveryPrivateChatResultEvent notFound(DeliveryPrivateChatEvent source) {
        return new DeliveryPrivateChatResultEvent(false, source);
    }

    public static DeliveryPrivateChatResultEvent delivered(DeliveryPrivateChatEvent source) {
        return new DeliveryPrivateChatResultEvent(true, source);
    }
}
