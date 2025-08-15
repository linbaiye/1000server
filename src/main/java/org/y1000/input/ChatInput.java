package org.y1000.input;

import org.apache.commons.lang3.StringUtils;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.PlayerInputHandler;
import org.y1000.entities.players.PlayerPrivateChatEvent;
import org.y1000.entities.players.event.PlayerEvent;
import org.y1000.entities.players.event.PlayerSayEvent;
import org.y1000.entities.players.event.PlayerShoutEvent;
import org.y1000.entities.players.event.PlayerTextMessage;
import org.y1000.realm.event.ApplyGuildKungFuCommandEvent;
import org.y1000.realm.event.GrantGuildKungFuEvent;
import org.y1000.realm.event.RealmEvent;

import java.util.Optional;
import java.util.Set;

/**
 * 玩家聊天输入。
 */
public class ChatInput implements SelfHandleInput {
    private final String text;

    public ChatInput(String text) {
        this.text = text;
    }

    private static final String CreateGuildKungFu = "@申请门武";
    private static final String GrantGuildKungFu = "@传授门武";

    private final Set<String> RealmEventPrefixes = Set.of(CreateGuildKungFu, GrantGuildKungFu);

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

    public Optional<RealmEvent> toRealmEvent(Player player) {
        String[] split = text.split(" ");
        if (!RealmEventPrefixes.contains(split[0])) {
            return Optional.empty();
        }
        if (CreateGuildKungFu.equals(split[0]))
            return Optional.of(new ApplyGuildKungFuCommandEvent(player));
        else if (GrantGuildKungFu.equals(split[0])) {
            return split.length == 2 ? Optional.of(new GrantGuildKungFuEvent(player, split[1])) :
                    Optional.empty();
        }
        return Optional.empty();
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
            handler.handleChat(this);
    }
}
