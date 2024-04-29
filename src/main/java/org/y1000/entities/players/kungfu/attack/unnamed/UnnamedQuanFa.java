package org.y1000.entities.players.kungfu.attack.unnamed;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.y1000.entities.Direction;
import org.y1000.entities.Entity;
import org.y1000.entities.creatures.event.CreatureAttackEvent;
import org.y1000.entities.players.PlayerAttackState;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.entities.players.event.PlayerAttackEvent;
import org.y1000.entities.players.event.PlayerAttackEventResponse;
import org.y1000.entities.players.kungfu.attack.AbstractAttackKungFu;
import org.y1000.entities.players.kungfu.attack.AttackKungFuType;
import org.y1000.message.clientevent.ClientAttackEvent;

@Getter
@SuperBuilder
public final class UnnamedQuanFa extends AbstractAttackKungFu {


    private final long fistLengthMillis = 90 * 5;

    private final long kickLengthMillis = 75 * 6;

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

    @Override
    public void attack(PlayerImpl player, ClientAttackEvent event, Entity target) {
        int distance = player.coordinate().distance(target.coordinate());
        Direction direction = event.direction();
        player.changeDirection(direction);
        player.changeState(new PlayerAttackState(fistLengthMillis, target));
        player.emitEvent(new PlayerAttackEventResponse(player, event.sequence(), true));
        if (distance <= 1) {
            target.hit(player);
        }
        player.emitEvent(new CreatureAttackEvent(player, event.below50(), event.below50() ? 90 : 75));
    }

    @Override
    public AttackKungFuType getType() {
        return AttackKungFuType.QUANFA;
    }
}
