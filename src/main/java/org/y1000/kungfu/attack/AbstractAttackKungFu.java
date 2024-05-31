package org.y1000.kungfu.attack;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.y1000.kungfu.AbstractKungFu;

@Getter
@SuperBuilder
public abstract class AbstractAttackKungFu extends AbstractKungFu implements AttackKungFu {

    private int bodyDamage;
    private int headDamage;
    private int armDamage;
    private int legDamage;

    private int bodyArmor;
    private int headArmor;
    private int armArmor;
    private int legArmor;

    private int attackSpeed;

    private int recovery;

    private int avoidance;

}
