package org.y1000.realm.event;


import org.apache.commons.lang3.NotImplementedException;

public record PrivateChatEvent(String receiverName,
                               String senderName,
                               String content,
                               boolean confirmation) implements RealmEvent {

    public String formatReceiving() {
        if (!confirmation)
            return receiverName + ">：" + content;
        throw new NotImplementedException();
    }

    public String formatConfirmation() {
        if (confirmation)
            return "> " + receiverName + "：" + content;
        throw new NotImplementedException();
    }

    public RealmEvent confirm() {
        return new PrivateChatEvent(senderName, receiverName, content, true);
    }

    public RealmEvent noRecipient() {
        return new PrivateChatEvent(senderName(), "", "玩家不在线。", false);
    }

}
