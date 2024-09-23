package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.players.Player;
import org.y1000.message.clientevent.ClientFoundGuildEvent;
import org.y1000.network.event.ConnectionEstablishedEvent;
import org.y1000.realm.event.RealmTeleportEvent;
import org.y1000.sdb.MapSdb;

@Slf4j
class GuildableRealm extends AbstractRealm {
    private final GuildManager guildManager;
    public GuildableRealm(int id, RealmMap realmMap,
                          RealmEntityEventSender eventSender,
                          GroundItemManager itemManager,
                          NpcManager npcManager,
                          PlayerManager playerManager,
                          DynamicObjectManager dynamicObjectManager,
                          TeleportManager teleportManager,
                          CrossRealmEventSender crossRealmEventSender,
                          MapSdb mapSdb,
                          ChatManager chatManager,
                          GuildManager guildManager) {
        super(id, realmMap, eventSender, itemManager, npcManager, playerManager, dynamicObjectManager, teleportManager, crossRealmEventSender, mapSdb, chatManager);
        this.guildManager = guildManager;
    }

    @Override
    Logger log() {
        return log;
    }

    @Override
    void handleTeleportEvent(RealmTeleportEvent teleportEvent) {
        acceptTeleport(teleportEvent);
    }

    @Override
    void handleConnectionEvent(ConnectionEstablishedEvent connectedEvent) {
        getEventSender().add(connectedEvent.player(), connectedEvent.connection());
        getPlayerManager().onPlayerConnected(connectedEvent.player(), this);
    }

    @Override
    void handleGuidCreation(Player source, ClientFoundGuildEvent event) {
        guildManager.foundGuild(source, event.coordinate(), event.name(), event.inventorySlot());
    }

    @Override
    public void update() {
        doUpdateEntities();
    }

    @Override
    public void init() {
        doInit();
        guildManager.init();
    }
}
