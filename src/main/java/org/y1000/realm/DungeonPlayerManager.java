package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.players.Player;
import org.y1000.message.I2ClientMessage;
import org.y1000.repository.PlayerRepository;

import java.util.function.Consumer;

@Slf4j
final class DungeonPlayerManager extends AbstractPlayerManager {

    private final EntityTimerManager<Player> deadPlayerTimers;

    private static final int deadMillis = 8000;

    private Consumer<? super Player> teleportDeadPlayer;

    public DungeonPlayerManager(AOIManager aoiManager,
                                RealmPlayerConnectionManager connectionManager,
                                GroundItemManager itemManager,
                                PlayerRepository playerRepository,
                                RealmEventSender crossRealmEventSender) {
        super(aoiManager, connectionManager, itemManager, playerRepository, crossRealmEventSender);
        this.deadPlayerTimers = new EntityTimerManager<>();
    }

    @Override
    protected Logger log() {
        return log;
    }

    public void setDeadPlayerTeleportor(Consumer<? super Player> teleportDeadPlayer) {
        this.teleportDeadPlayer = teleportDeadPlayer;
    }

    @Override
    public void onPlayerDead(Player player, I2ClientMessage message) {
        deadPlayerTimers.add(player, deadMillis);
        sendToVisiblePlayersAndSelf(player, message);
    }

    @Override
    public void update(long delta) {
        var timedOutDeadPlayers = deadPlayerTimers.update(delta);
        timedOutDeadPlayers.forEach(teleportDeadPlayer);
        updatePlayersAndProjectiles(delta);
    }
}
