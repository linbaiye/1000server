package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.PlayerTextMessage;
import org.y1000.message.PlayerTextEvent;
import org.y1000.message.input.ClientFoundGuildEvent;
import org.y1000.message.input.Login;
import org.y1000.realm.event.RealmTeleportEvent;
import org.y1000.repository.PlayerRepository;
import org.y1000.sdb.MapSdb;

@Slf4j
final class RealmImpl extends AbstractRealm {


    public RealmImpl(int id, RealmMap realmMap,
                     GroundItemManager itemManager,
                     NpcManager npcManager,
                     PlayerManager playerManager,
                     DynamicObjectManager dynamicObjectManager,
                     TeleportManager teleportManager,
                     RealmEventSender crossRealmEventSender,
                     MapSdb mapSdb,
                     PlayerRepository playerRepository) {
        super(id, realmMap, itemManager, npcManager, playerManager, dynamicObjectManager, teleportManager, crossRealmEventSender, mapSdb, playerRepository);
    }

    @Override
    protected Logger log() {
        return log;
    }

    @Override
    public void handleTeleportEvent(RealmTeleportEvent teleportEvent) {
        String s = teleportEvent.checkCost();
        if (s != null) {
            teleportEvent.getConnection().writeAndFlush(PlayerTextMessage.bottom(teleportEvent.player(), s));
            teleportEvent.rejectEvent().ifPresent(e -> getCrossRealmEventHandler().send(e));
        } else {
            playerManager().teleportIn(teleportEvent.player(), this, teleportEvent.toCoordinate(), teleportEvent.getConnection());
            teleportEvent.getCosts().forEach(c -> c.charge(teleportEvent.player()));
        }
    }


    @Override
    void handleGuildCreation(Player source, ClientFoundGuildEvent event) {
        source.emitEvent(PlayerTextEvent.forbidGuildCreation(source));
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
