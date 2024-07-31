package org.y1000.entities.players.event;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.ActiveEntity;
import org.y1000.entities.AttackableActiveEntity;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.event.EntitySoundEvent;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.entities.objects.DynamicObject;
import org.y1000.entities.objects.KillableDynamicObject;
import org.y1000.entities.players.Damage;
import org.y1000.entities.players.Player;
import org.y1000.kungfu.AssistantKungFu;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.util.Coordinate;

import java.util.Set;

public class PlayerAttackAoeEvent implements PlayerEvent {

    private final Player player;
    private final AttackableActiveEntity mainTarget;

    private final Damage aoeDamage;

    private final Set<Coordinate> effectedCoordinates;

    private final boolean withSound;

    private PlayerAttackAoeEvent(Player source,
                                 AttackableActiveEntity target,
                                 Damage aoeDamage,
                                 Set<Coordinate> effectedCoordinates, boolean withSound) {
        this.player = source;
        this.mainTarget = target;
        this.aoeDamage = aoeDamage;
        this.effectedCoordinates = effectedCoordinates;
        this.withSound = withSound;
    }

    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {
        playerEventHandler.visit(this);
    }

    private int handleAttack(AttackableActiveEntity entity, Damage damage) {
        int exp = 0;
        if (entity instanceof Npc npc) {
            exp = npc.attackedByAoe(player(), player().hit(), damage);
        } else if (entity instanceof Player targetPlayer) {
            exp = targetPlayer.attackedByAoe(damage, player().hit());
        } else if (entity instanceof KillableDynamicObject dynamicObject) {
            dynamicObject.attackedByAoe(damage);
        }
        return exp;
    }


    private boolean affected(AttackableActiveEntity entity) {
        if (entity.equals(mainTarget)) {
            return false;
        }
        if (entity instanceof DynamicObject dynamicObject) {
            return dynamicObject.occupyingCoordinates().stream().anyMatch(effectedCoordinates::contains);
        }
        return effectedCoordinates.contains(entity.coordinate());
    }


    public void affect(Set<AttackableActiveEntity> visibleCreatures)  {
        int targetExp = handleAttack(mainTarget, player.damage());
        long count = visibleCreatures.stream()
                .filter(this::affected)
                .map(e -> handleAttack(e, aoeDamage))
                .filter(exp -> exp > 0)
                .count();
        if (count > 0 && targetExp > 0) {
            player().gainAssistantExp(targetExp);
        }
        if (withSound)
            player.emitEvent(new EntitySoundEvent(player, targetExp > 0 || count > 0? player().attackKungFu().strikeSound() : player().attackKungFu().swingSound()));
    }

    @Override
    public ActiveEntity source() {
        return player;
    }

    public static PlayerAttackAoeEvent melee(Player player, AttackableActiveEntity target, AssistantKungFu assistantKungFu) {
        Validate.isTrue(player.coordinate().directDistance(target.coordinate()) <= 1);
        return new PlayerAttackAoeEvent(player, target, assistantKungFu.computeDamage(player.damage()), assistantKungFu.affectedCoordinates(player), true);
    }

    public static PlayerAttackAoeEvent ranged(Player player, AttackableActiveEntity target,
                                              Direction direction, Damage damage, AssistantKungFu assistantKungFu) {
        return new PlayerAttackAoeEvent(player, target, assistantKungFu.computeDamage(damage),
                assistantKungFu.affectedCoordinates(target.coordinate(), direction), false);
    }
}

