package org.y1000.entities.players.event;

import org.y1000.entities.PhysicalEntity;
import org.y1000.entities.players.Player;
import org.y1000.kungfu.AssistantKungFu;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.util.Coordinate;

import java.util.Set;

public class PlayerAttackAoeEvent implements PlayerEvent {

    private final Player player;

    private final Set<Coordinate> effectedCoordinates;

    public PlayerAttackAoeEvent(Player source,
                                Set<Coordinate> effectedCoordinates) {
        this.player = source;
        this.effectedCoordinates = effectedCoordinates;
    }

    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {
        playerEventHandler.visit(this);
    }


    public void affect(Set<PhysicalEntity> visibleCreatures)  {
        visibleCreatures.stream()
                .filter(e -> effectedCoordinates.contains(e.coordinate()))
                .forEach(entity -> entity.attackedBy(player));
    }

    @Override
    public PhysicalEntity source() {
        return player;
    }

    public static PlayerAttackAoeEvent melee(Player player, AssistantKungFu assistantKungFu) {
        return new PlayerAttackAoeEvent(player, assistantKungFu.affectedCoordinates(player));
    }
}

