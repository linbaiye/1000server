package org.y1000.entities.players.fight;

import org.slf4j.Logger;
import org.y1000.entities.Direction;
import org.y1000.entities.PhysicalEntity;
import org.y1000.entities.creatures.CreatureState;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.ChangeStateEvent;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.entities.players.PlayerState;
import org.y1000.entities.players.PlayerStillState;
import org.y1000.entities.players.event.PlayerAttackEvent;
import org.y1000.entities.players.event.PlayerAttackEventResponse;
import org.y1000.message.clientevent.ClientAttackEvent;

import java.util.Optional;

public interface AttackableState extends CreatureState<PlayerImpl> {


    Logger logger();

    default PlayerState rangedCooldownState(PlayerImpl player, PhysicalEntity target) {
        return PlayerBowCooldownState.cooldown(player.getStateMillis(State.COOLDOWN), target, 1);
    }

    default PlayerState rangedAttackState(PlayerImpl player, PhysicalEntity target) {
        return PlayerBowAttackState.bow(player, target);
    }

    default void rangedAttack(PlayerImpl player, PhysicalEntity target, int counter) {
        var direction = player.coordinate().computeDirection(target.coordinate());
        player.changeDirection(direction);
        player.changeState(PlayerBowAttackState.bow(player, target, counter));
        player.emitEvent(PlayerAttackEvent.of(player, target.id()));
    }

    default void attack(PlayerImpl player, PhysicalEntity target) {
        if (!target.attackable()) {
            player.changeState(PlayerStillState.chillOut(player));
            player.emitEvent(ChangeStateEvent.of(player));
            return;
        }
        Direction direction = player.coordinate().computeDirection(target.coordinate());
        boolean rangedAttack = player.attackKungFu().isRanged();
        if (player.cooldown() > 0) {
            var cdState = rangedAttack ? rangedCooldownState(player, target) :
                    new PlayerMeleeCooldownState(player.getStateMillis(State.COOLDOWN), target);
            player.changeState(cdState);
            player.emitEvent(ChangeStateEvent.of(player));
            return;
        }
        var dist = player.coordinate().directDistance(target.coordinate());
        player.changeDirection(direction);
        if (rangedAttack || dist <= 1) {
            State state = player.attackKungFu().randomAttackState();
            var attackState = rangedAttack ? rangedAttackState(player, target) :
                    PlayerMeleeAttackState.attack(target, state, player.getStateMillis(state));
            player.changeState(attackState);
            player.emitEvent(PlayerAttackEvent.of(player, target.id()));
        } else {
            player.changeState(PlayerMeleeAttackReadyState.prepareSwing(player, target));
            player.emitEvent(ChangeStateEvent.of(player));
        }
    }

    default void handleAttackEvent(PlayerImpl player, ClientAttackEvent event) {
        Optional<PhysicalEntity> insight = player.getRealm().findInsight(player, event.entityId());
        if (insight.isEmpty()) {
            player.emitEvent(new PlayerAttackEventResponse(player, event, false));
            return;
        }
        PhysicalEntity target = insight.get();
        if (!target.attackable()) {
            player.emitEvent(new PlayerAttackEventResponse(player, event, false));
            return;
        }
        player.emitEvent(new PlayerAttackEventResponse(player, event, true));
        attack(player, target);
    }
}

