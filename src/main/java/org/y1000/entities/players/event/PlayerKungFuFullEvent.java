package org.y1000.entities.players.event;

import org.y1000.entities.Entity;
import org.y1000.entities.players.Player;
import org.y1000.kungfu.KungFu;
import org.y1000.message.PlayerTextEvent;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.realm.event.BroadcastEvent;

public final class PlayerKungFuFullEvent implements BroadcastEvent, PlayerEvent {
    private final String kungFuName;
    private final Player player;

    public PlayerKungFuFullEvent(Player source, KungFu kungFu) {
        this.kungFuName = kungFu.name();
        this.player = source;
    }

    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {

    }

    @Override
    public void send(Player player) {
        player.emitEvent(PlayerTextEvent.kungfuFull(player, kungFuName));
    }

    @Override
    public Entity source() {
        return player;
    }
}
