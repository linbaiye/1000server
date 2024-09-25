package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.PlayerAttackEvent;
import org.y1000.guild.GuildStone;
import org.y1000.message.clientevent.ClientAttackEvent;
import org.y1000.message.clientevent.ClientFoundGuildEvent;
import org.y1000.network.event.ConnectionEstablishedEvent;
import org.y1000.realm.event.PlayerDataEvent;
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
        addEntityManager(guildManager);
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

    private void attackGuildStone(GuildStone guildStone, long playerId, ClientAttackEvent attackEvent) {
        playerManager().find(playerId).ifPresent(player -> player.attack(attackEvent, guildStone));
    }

    @Override
    void handleClientEvent(PlayerDataEvent dataEvent) {
        if (dataEvent.data() instanceof ClientAttackEvent attackEvent) {
            guildManager.find(attackEvent.entityId(), GuildStone.class)
                    .ifPresentOrElse(guildStone -> attackGuildStone(guildStone, dataEvent.playerId(), attackEvent),
                            () -> playerManager().onClientEvent(dataEvent, npcManager()));
        } else {
            playerManager().onClientEvent(dataEvent, npcManager());
        }
    }

    @Override
    public void init() {
        doInit();
        guildManager.init();
    }

    @Override
    public void shutdown() {
        playerManager().shutdown();
        guildManager.shutdown();
    }
}
