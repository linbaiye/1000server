package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.players.Player;
import org.y1000.message.I2ClientMessage;
import org.y1000.repository.PlayerRepository;


@Slf4j
final class PlayerManagerImpl extends AbstractPlayerManager {

    public PlayerManagerImpl(RealmPlayerConnectionManager connectionManager,
                             GroundItemManager itemManager,
                             PlayerRepository playerRepository,
                             RealmEventSender crossRealmEventSender,
                             AOIManager aoiManager) {
        super(aoiManager, connectionManager, itemManager, playerRepository, crossRealmEventSender);
    }


    @Override
    protected Logger log() {
        return log;
    }

    @Override
    public void onPlayerDead(Player player, I2ClientMessage deadMessage) {
        sendToVisiblePlayersAndSelf(player, deadMessage);
    }

    @Override
    public void update(long delta) {
        updatePlayersAndProjectiles(delta);
    }
}
