package org.y1000.realm;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.Player;
import org.y1000.message.PlayerTextEvent;
import org.y1000.message.clientevent.chat.ClientChatEvent;
import org.y1000.message.clientevent.chat.ClientRealmChatEvent;
import org.y1000.message.clientevent.chat.ClientSpeakEvent;
import org.y1000.realm.event.RealmEvent;
import org.y1000.realm.event.PrivateChatEvent;

class ChatManagerImpl implements ChatManager {

    private final PlayerManager playerManager;

    private final EntityEventSender eventSender;

    private final CrossRealmEventHandler crossRealmEventHandler;

    public ChatManagerImpl(PlayerManager playerManager,
                           EntityEventSender eventSender,
                           CrossRealmEventHandler crossRealmEventHandler) {
        Validate.notNull(playerManager);
        Validate.notNull(eventSender);
        Validate.notNull(crossRealmEventHandler);
        this.playerManager = playerManager;
        this.eventSender = eventSender;
        this.crossRealmEventHandler = crossRealmEventHandler;
    }

    private void handleClientChatEvent(Player player, ClientChatEvent event) {
        if (!event.canSend(player)) {
            return;
        }
        if (event instanceof ClientRealmChatEvent realmChatEvent) {
            crossRealmEventHandler.handle(realmChatEvent.toRealmEvent(player));
        } else if (event instanceof ClientSpeakEvent speakEvent) {
            eventSender.notifyVisiblePlayersAndSelf(player, speakEvent.toPlayerEvent(player));
        }
    }

    @Override
    public void handleClientChat(long from, ClientChatEvent clientChatEvent) {
        if (clientChatEvent == null)
            return;
        playerManager.find(from).ifPresent(player -> handleClientChatEvent(player, clientChatEvent));
    }

    @Override
    public void handleCrossRealmChat(RealmEvent realmEvent) {
        if (realmEvent == null) {
            return;
        }
        if (realmEvent instanceof PrivateChatEvent privateMessageEvent) {
            playerManager.allPlayers().stream().filter(p -> privateMessageEvent.receiverName().equals(p.viewName()))
                    .findFirst().ifPresent(player -> player.emitEvent(PlayerTextEvent.privateChat(player, privateMessageEvent.content())));
        }
    }
}
