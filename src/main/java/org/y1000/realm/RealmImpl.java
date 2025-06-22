package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.RemoveEntityEvent;
import org.y1000.entities.players.Player;
import org.y1000.message.PlayerTextEvent;
import org.y1000.message.input.ClientFoundGuildEvent;
import org.y1000.message.input.Login;
import org.y1000.network.event.ConnectionEstablishedEvent;
import org.y1000.realm.event.PlayerDataEvent;
import org.y1000.realm.event.RealmTeleportEvent;
import org.y1000.repository.PlayerRepository;
import org.y1000.sdb.MapSdb;

import java.util.Optional;

@Slf4j
final class RealmImpl extends AbstractRealm {


    public RealmImpl(int id, RealmMap realmMap,
                     RealmEntityEventSender eventSender,
                     GroundItemManager itemManager,
                     NpcManager npcManager,
                     PlayerManager playerManager,
                     DynamicObjectManager dynamicObjectManager,
                     TeleportManager teleportManager,
                     CrossRealmEventSender crossRealmEventSender,
                     MapSdb mapSdb,
                     ChatManager chatManager,
                     PlayerRepository playerRepository) {
        super(id, realmMap, eventSender, itemManager, npcManager, playerManager, dynamicObjectManager, teleportManager, crossRealmEventSender, mapSdb, chatManager, playerRepository);
    }

    @Override
    protected Logger log() {
        return log;
    }

    @Override
    void handleTeleportEvent(RealmTeleportEvent teleportEvent) {
        acceptIfAffordableElseReject(teleportEvent);
    }

    @Override
    void handleConnectionEvent(ConnectionEstablishedEvent connectedEvent) {
        getEventSender().add(connectedEvent.player(), connectedEvent.connection());
        getPlayerManager().onPlayerConnected(connectedEvent.player(), this);
    }

    @Override
    void handleGuildCreation(Player source, ClientFoundGuildEvent event) {
        source.emitEvent(PlayerTextEvent.forbidGuildCreation(source));
    }

    @Override
    void handleClientEvent(PlayerDataEvent dataEvent) {
        playerManager().onClientEvent(dataEvent, npcManager());
    }

    @Override
    protected void handleLogin(Login login) {
        acceptLogin(login);
    }

    @Override
    public void shutdown() {
        playerManager().shutdown();
    }

    @Override
    public void init() {
        doInit();
    }

    @Override
    public String toString() {
        return "RealmImpl {id = " + id() + "}";
    }
}
