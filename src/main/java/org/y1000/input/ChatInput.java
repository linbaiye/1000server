package org.y1000.input;

import org.apache.commons.lang3.StringUtils;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.PlayerInputHandler;
import org.y1000.entities.players.PlayerPrivateChatEvent;
import org.y1000.entities.players.event.PlayerEvent;
import org.y1000.entities.players.event.PlayerSayEvent;
import org.y1000.entities.players.event.PlayerShoutEvent;
import org.y1000.entities.players.event.PlayerTextMessage;

import java.util.Optional;

/**
 * 玩家聊天输入。
 */
public class ChatInput implements SelfHandleInput {
    private final String text;

    public ChatInput(String text) {
        this.text = text;
    }

    private Optional<PlayerEvent> handleShout(Player player) {
        if (text.length() < 2)
            return Optional.empty();
        if (player.currentLife() < 5000) {
            return Optional.of(PlayerTextMessage.systip(player, "活力需大于50。"));
        }
        char level = text.charAt(1);
        int l = 0;
        if (level >= '0' && level <= '9') {
            l = level - '0';
        }
        return Optional.of(PlayerShoutEvent.test(player,  player.viewName() + "：" + text.substring(1), l));
    }

    private Optional<PlayerEvent> handlePrivateChat(Player player) {
        String[] segments = text.split(" ");
        if (segments.length < 3)
            return Optional.empty();
        return Optional.of(PlayerPrivateChatEvent.of(player, segments[1], segments[2]));
    }


    public Optional<PlayerEvent> toPlayerEvent(Player player) {
        if (text.startsWith("!")) {
            return handleShout(player);
        } else if (text.startsWith("@纸条")) {
            return handlePrivateChat(player);
        } else {
            return Optional.of(PlayerSayEvent.say(player, text));
        }
    }

    @Override
    public void accept(PlayerInputHandler handler) {
        if (!StringUtils.isEmpty(text))
            handler.chat(this);
    }
}
