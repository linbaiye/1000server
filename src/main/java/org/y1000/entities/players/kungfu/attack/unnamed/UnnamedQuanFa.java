package org.y1000.entities.players.kungfu.attack.unnamed;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.y1000.entities.Direction;
import org.y1000.entities.Entity;
import org.y1000.entities.players.PlayerAttackState;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.entities.players.event.PlayerAttackEvent;
import org.y1000.entities.players.event.PlayerAttackEventResponse;
import org.y1000.entities.players.kungfu.attack.AbstractAttackKungFu;
import org.y1000.entities.players.kungfu.attack.AttackKungFuType;
import org.y1000.message.clientevent.ClientAttackEvent;
import org.y1000.message.serverevent.EntityEvent;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@SuperBuilder
public final class UnnamedQuanFa extends AbstractAttackKungFu {


    private final int fistLengthMillis = 90 * 5;

    private final int kickLengthMillis = 75 * 6;

    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

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

    private void attack(PlayerImpl player, Entity target, EntityEvent event, boolean below50) {
        int distance = player.coordinate().distance(target.coordinate());
        Direction direction = player.coordinate().computeDirection(target.coordinate());
        player.changeDirection(direction);
        if (distance <= 1 && target.canBeHit()) {
            player.changeState(new PlayerAttackState(fistLengthMillis, target, 500, below50));
            player.emitEvent(event);
        } else {
            player.emitEvent(event);
        }
    }

    @Override
    public void attack(PlayerImpl player, ClientAttackEvent event, Entity target) {
        attack(player, target, new PlayerAttackEventResponse(player, event, true, millisPerSprite(event.below50())), event.below50());
    }

    private boolean diceBelow50() {
        return level() < 50 || RANDOM.nextInt() % 2 == 1;
    }

    public int millisPerSprite(boolean below50) {
        return below50 ? 90 : 75;
    }

    @Override
    public void attack(PlayerImpl player, Entity target) {
        boolean b = diceBelow50();
        attack(player, target, new PlayerAttackEvent(player, millisPerSprite(b), b), b);
    }

    @Override
    public AttackKungFuType getType() {
        return AttackKungFuType.FIST;
    }
}
