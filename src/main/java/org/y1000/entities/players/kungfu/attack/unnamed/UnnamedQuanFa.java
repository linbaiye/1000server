package org.y1000.entities.players.kungfu.attack.unnamed;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.y1000.entities.Direction;
import org.y1000.entities.Entity;
import org.y1000.entities.creatures.event.CreatureAttackEvent;
import org.y1000.entities.players.PlayerAttackState;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.entities.players.event.PlayerAttackEventResponse;
import org.y1000.entities.players.kungfu.attack.AbstractAttackKungFu;
import org.y1000.entities.players.kungfu.attack.AttackKungFuType;
import org.y1000.message.clientevent.ClientAttackEvent;
import org.y1000.message.serverevent.EntityEvent;

import java.util.function.Function;

@Getter
@SuperBuilder
public final class UnnamedQuanFa extends AbstractAttackKungFu {


    private final int fistLengthMillis = 90 * 5;

    private final int kickLengthMillis = 75 * 6;

    @Override
    public String name() {
        return "无名拳法";
    }

    public static UnnamedQuanFa start() {
         return UnnamedQuanFa.builder()
                .bodyDamage(163)
                .level(0.0f)
                .bodyArmor(32)
                .build();
    }


    private void attack(PlayerImpl player, Entity target, boolean below50, Function<PlayerImpl, EntityEvent> eventFunction) {
        int distance = player.coordinate().distance(target.coordinate());
        Direction direction = player.coordinate().computeDirection(target.coordinate());
        player.changeDirection(direction);
        player.changeState(new PlayerAttackState(fistLengthMillis, target, 600));
        player.emitEvent(eventFunction.apply(player));
        if (distance <= 1) {
            target.hit(player);
        }
        player.emitEvent(new CreatureAttackEvent(player, below50, below50 ? 90 : 75));
    }


    @Override
    public void attack(PlayerImpl player, ClientAttackEvent event, Entity target) {
        attack(player, target, event.below50(), p -> new PlayerAttackEventResponse(player, event.sequence(), true));
    }

    @Override
    public void attack(PlayerImpl player, Entity target) {

    }

    @Override
    public AttackKungFuType getType() {
        return AttackKungFuType.QUANFA;
    }
}
