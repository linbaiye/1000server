package org.y1000.realm.event;


public record PrivateChatEvent(String receiverName,
                               String senderName,
                               String content) implements RealmEvent {
    public String send() {

    }

    @Override
    public RealmEventType realmEventType() {
        return RealmEventType.BROADCAST;
    }
}
