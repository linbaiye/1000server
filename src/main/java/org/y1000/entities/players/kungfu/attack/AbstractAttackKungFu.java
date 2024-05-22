package org.y1000.entities.players.kungfu.attack;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.y1000.entities.players.kungfu.AbstractLevelKungFu;

@Getter
@SuperBuilder
public abstract class AbstractAttackKungFu extends AbstractLevelKungFu implements AttackKungFu {

    private final String name;

    private int bodyDamage;

    private int bodyArmor;

    private int attackSpeed;

    private int recovery;

    public String name() {
        return this.name;
    }
}
