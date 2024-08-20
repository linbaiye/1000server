package org.y1000.message;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.Player;
import org.y1000.event.CrossRealmEvent;
import org.y1000.message.clientevent.ClientTextEvent;
import org.y1000.realm.event.RealmEvent;
import org.y1000.realm.event.RealmEventType;

public record ClientDirectMessageEvent(String receiver, String content) implements ClientTextEvent,
        RealmEvent {

    public ClientDirectMessageEvent {
        Validate.isTrue(receiver != null);
        Validate.isTrue(content != null);
    }

    @Override
    public void handle(Player player) {
        player.emitEvent(new CrossRealmEvent(player, this));
    }

    @Override
    public RealmEventType realmEventType() {
        return RealmEventType.BROADCAST;
    }

    public static boolean isFormatCorrect(String text) {
        if (StringUtils.isEmpty(text) || !text.startsWith("@纸条 ")) {
            return false;
        }
        String[] split = text.split(" ");
        return split.length >= 2 && StringUtils.isNotEmpty(split[1]);
    }

    public static ClientDirectMessageEvent parse(String text) {
        Validate.isTrue(isFormatCorrect(text));
        String[] split = text.split(" ");
        String replace = text.replace("@纸条 " + split[1], "");
        return new ClientDirectMessageEvent(split[1], replace.startsWith(" ") ? replace.substring(1) : replace);
    }
}
