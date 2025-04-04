package org.y1000.entities.players.event;

import org.y1000.entities.AttackableActiveEntity;
import org.y1000.entities.players.Player;
import org.y1000.message.serverevent.PlayerEventVisitor;

import java.util.Objects;

public record PlayerStartTradeEvent(Player starter, int slot, long targetPlayerId) implements PlayerEvent {

    public PlayerStartTradeEvent {
        Objects.requireNonNull(starter, "starter can't be null");
    }

    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {
    }

    @Override
    public AttackableActiveEntity source() {
        return starter;
    }
}
