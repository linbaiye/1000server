package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.Player;
import org.y1000.message.clientevent.chat.ClientInputTextEvent;
import org.y1000.message.clientevent.chat.ClientRealmChatEvent;
import org.y1000.message.clientevent.chat.ClientChatEvent;

import org.y1000.message.clientevent.chat.ClientWorldShoutEvent;
import org.y1000.realm.event.RealmEvent;
import org.y1000.realm.event.PlayerWhisperEvent;

@Slf4j
final class ChatManagerImpl implements ChatManager {

    private final PlayerManager playerManager;

    private final EntityEventSender eventSender;

    private final CrossRealmEventSender crossRealmEventSender;

    public ChatManagerImpl(PlayerManager playerManager,
                           EntityEventSender eventSender,
                           CrossRealmEventSender crossRealmEventSender) {
        Validate.notNull(playerManager);
        Validate.notNull(eventSender);
        Validate.notNull(crossRealmEventSender);
        this.playerManager = playerManager;
        this.eventSender = eventSender;
        this.crossRealmEventSender = crossRealmEventSender;
    }

    private void handleClientChatEvent(Player player, ClientInputTextEvent event) {
        if (!event.canSend(player)) {
            return;
        }
        if (event instanceof ClientRealmChatEvent realmChatEvent) {
            if (realmChatEvent instanceof ClientWorldShoutEvent)
                player.consumeLife(1000);
            crossRealmEventSender.send(realmChatEvent.toRealmEvent(player));
        } else if (event instanceof ClientChatEvent speakEvent) {
            eventSender.notifyVisiblePlayersAndSelf(player, speakEvent.toPlayerEvent(player));
        }
    }

    @Override
    public void handleClientChat(long from, ClientInputTextEvent clientInputTextEvent) {
        if (clientInputTextEvent == null)
            return;
        playerManager.find(from).ifPresent(player -> handleClientChatEvent(player, clientInputTextEvent));
    }

    private void handlePrivateChat(Player player, PlayerWhisperEvent chatEvent) {
        player.emitEvent(chatEvent.toTextEvent(player));
        if (chatEvent.needConfirm())
            handleCrossRealmChat(chatEvent.createConfirmation());
    }

    @Override
    public void handleCrossRealmChat(RealmEvent realmEvent) {
        if (realmEvent == null) {
            return;
        }
        if (realmEvent instanceof PlayerWhisperEvent whisper) {
            playerManager.allPlayers().stream().filter(p -> whisper.receiverName().equals(p.viewName()))
                    .findFirst().ifPresent(player -> handlePrivateChat(player, whisper));
        }
    }
}
