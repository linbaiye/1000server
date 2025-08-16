package org.y1000.input;

import lombok.Builder;
import lombok.Data;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.PlayerInputHandler;
import org.y1000.kungfu.attack.AttackKungFuType;
import org.y1000.network.gen.ApplyGuildKungFuInputPacket;
import org.y1000.realm.event.ApplyKungFuEvent;


@Data
@Builder
public class ApplyGuildKungFuInput implements SelfHandleInput {

    private final String name;
    private final int speed;
    private final int recovery;
    private final int avoid;
    private final int headDamage;
    private final int armDamage;
    private final int bodyDamage;
    private final int legDamage;
    private final int headArmor;
    private final int armArmor;
    private final int bodyArmor;
    private final int legArmor;
    private final int powerToSwing;
    private final int innerPowerToSwing;
    private final int outerPowerToSwing;
    private final int lifeToSwing;
    private final AttackKungFuType type;

    public static ApplyGuildKungFuInput parse(ApplyGuildKungFuInputPacket packet) {
        return ApplyGuildKungFuInput.builder()
                .name(packet.getName())
                .type(AttackKungFuType.fromValue(packet.getType()))
                .speed(packet.getAttackSpeed())
                .recovery(packet.getRecovery())
                .avoid(packet.getAvoidance())
                .bodyDamage(packet.getBodyDamage())
                .headDamage(packet.getHeadDamage())
                .armDamage(packet.getArmDamage())
                .legDamage(packet.getLegDamage())
                .bodyArmor(packet.getBodyArmor())
                .headArmor(packet.getHeadArmor())
                .armArmor(packet.getArmArmor())
                .legArmor(packet.getLegArmor())
                .lifeToSwing(packet.getLife())
                .powerToSwing(packet.getPower())
                .innerPowerToSwing(packet.getInnerPower())
                .outerPowerToSwing(packet.getOuterPower())
                .build();
    }

    private ApplyKungFuEvent toRealmEvent(Player player) {
        return new ApplyKungFuEvent(player, this);
    }

    @Override
    public void accept(PlayerInputHandler handler) {
        handler.proxyToRealm(this::toRealmEvent);
    }
}
