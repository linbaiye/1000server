package org.y1000.entities.players.kungfu.attack.unnamed;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.Direction;
import org.y1000.entities.Entity;
import org.y1000.entities.creatures.State;
import org.y1000.entities.players.PlayerAttackState;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.entities.players.event.PlayerAttackEvent;
import org.y1000.entities.players.event.PlayerAttackEventResponse;
import org.y1000.entities.players.kungfu.attack.AbstractAttackKungFu;
import org.y1000.entities.players.kungfu.attack.AttackKungFuType;
import org.y1000.message.clientevent.ClientAttackEvent;
import org.y1000.message.serverevent.EntityEvent;
import org.y1000.realm.RealmImpl;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Getter
@SuperBuilder
public final class UnnamedQuanFa extends AbstractAttackKungFu {

    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    @Override
    public String name() {
        return "无名拳法";
    }


    public static UnnamedQuanFa start() {
         return UnnamedQuanFa.builder()
                .bodyDamage(163)
                .level(100)
                .attackSpeed(40)
                .bodyArmor(32)
                .build();
    }

    private void attack(PlayerImpl player, Entity target, EntityEvent event, State attackState) {
        int distance = player.coordinate().distance(target.coordinate());
        Direction direction = player.coordinate().computeDirection(target.coordinate());
        player.changeDirection(direction);
        if (distance <= 1 && target.attackable()) {
            var length = player.attackSpeed() * RealmImpl.STEP_MILLIS;
            var cooldown = attackState == State.FIST ? length - getType().below50Millis(): length - getType().above50Millis();
            target.attackedBy(player);
            player.cooldownAttack();
            player.changeState(PlayerAttackState.attack(target, attackState, length, cooldown));
            player.emitEvent(event);
        } else {
            log.debug("Too far to attack.");
            player.emitEvent(event);
        }
    }

    @Override
    public void attack(PlayerImpl player, ClientAttackEvent event, Entity target) {
        attack(player, target, new PlayerAttackEventResponse(player, event, true), event.attackState());
    }

    private State randomState() {
        return level() < 50 || RANDOM.nextInt() % 2 == 1 ? State.FIST : State.KICK;
    }

    @Override
    public void attack(PlayerImpl player, Entity target) {
        var st = randomState();
        attack(player, target, new PlayerAttackEvent(player, st), st);
    }

    @Override
    public AttackKungFuType getType() {
        return AttackKungFuType.QUANFA;
    }
}
