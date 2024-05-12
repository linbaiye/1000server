package org.y1000.entities.players.kungfu.attack;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.y1000.entities.players.kungfu.AbstractLevelKungFu;

@Getter
@SuperBuilder
public abstract class AbstractAttackKungFu extends AbstractLevelKungFu implements AttackKungFu {

    private int bodyDamage;

    private int bodyArmor;

    private int attackSpeed;

}
