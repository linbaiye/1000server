package org.y1000.entities.players.kungfu.attack.basic;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.y1000.entities.players.kungfu.attack.AbstractAttackKungFu;
import org.y1000.entities.players.kungfu.attack.AttackKungFuType;

@Getter
@SuperBuilder
public class UnnamedQuanFa extends AbstractAttackKungFu {

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
    public AttackKungFuType getType() {
        return AttackKungFuType.QUANFA;
    }
}
