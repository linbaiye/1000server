package org.y1000.entities.players.event;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.AttackableEntity;
import org.y1000.entities.Entity;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.entities.players.Player;
import org.y1000.kungfu.AssistantKungFu;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.util.Coordinate;

import java.util.Objects;
import java.util.Set;

public class PlayerAttackAoeEvent implements PlayerEvent {

    private final Player player;
    private final AttackableEntity mainTarget;

    private final Set<Coordinate> effectedCoordinates;

    public PlayerAttackAoeEvent(Player source,
                                AttackableEntity target,
                                Set<Coordinate> effectedCoordinates) {
        this.player = source;
        this.mainTarget = target;
        this.effectedCoordinates = effectedCoordinates;
    }

    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {
        playerEventHandler.visit(this);
    }

    private int handleAttack(AttackableEntity entity) {
        int exp = 0;
        if (entity instanceof Npc npc) {
            exp = npc.attackedByAoe(player(), player().hit(), player().damage());
        } else if (entity instanceof Player targetPlayer) {
            exp = targetPlayer.attackedByAoe(player().damage(), player().hit());
        }
        return exp;
    }

    public void affect(Set<AttackableEntity> visibleCreatures)  {
        int targetExp = handleAttack(mainTarget);
        long count = visibleCreatures.stream()
                .filter(e -> effectedCoordinates.contains(e.coordinate()))
                .map(this::handleAttack)
                .filter(exp -> exp > 0)
                .count();
        if (count > 0 && targetExp > 0) {
            player().gainAssistantExp(targetExp);
        }
    }

    @Override
    public Entity source() {
        return player;
    }

    public static PlayerAttackAoeEvent melee(Player player, AttackableEntity target, AssistantKungFu assistantKungFu) {
        Validate.isTrue(player.coordinate().directDistance(target.coordinate()) <= 1);
        return new PlayerAttackAoeEvent(player, target, assistantKungFu.affectedCoordinates(player));
    }
}

