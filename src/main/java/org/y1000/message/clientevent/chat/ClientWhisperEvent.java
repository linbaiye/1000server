package org.y1000.message.clientevent.chat;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.Player;
import org.y1000.realm.event.RealmEvent;
import org.y1000.realm.event.PlayerWhisperEvent;
public record ClientWhisperEvent(String receiver, String content) implements ClientRealmChatEvent {

    public ClientWhisperEvent {
        Validate.isTrue(receiver != null);
        Validate.isTrue(content != null);
    }

    public static boolean isFormatCorrect(String text) {
        if (StringUtils.isEmpty(text) || !text.startsWith("@纸条 ")) {
            return false;
        }
        String[] split = text.split(" ");
        return split.length >= 2 && StringUtils.isNotEmpty(split[1]);
    }

    public static ClientWhisperEvent parse(String text) {
        Validate.isTrue(isFormatCorrect(text));
        String[] split = text.split(" ");
        String replace = text.replace("@纸条 " + split[1], "");
        return new ClientWhisperEvent(split[1], replace.startsWith(" ") ? replace.substring(1) : replace);
    }

    @Override
    public boolean canSend(Player player) {
        return player != null;
    }

    @Override
    public RealmEvent toRealmEvent(Player player) {
        Validate.notNull(player);
        return PlayerWhisperEvent.send(receiver, player.viewName(), content);
    }
}
