package org.y1000.kungfu.attack;


import org.y1000.entities.players.*;
import org.y1000.kungfu.KungFu;
import org.y1000.util.Coordinate;

public interface AttackKungFu extends KungFu {

    int bodyDamage();

    int bodyArmor();

    int headArmor();

    int armArmor();

    int legArmor();

    Armor armor();

    int attackSpeed();

    int recovery();

    int avoidance();

    AttackKungFuType getType();

    boolean isRanged();

    int headDamage();

    int armDamage();

    int legDamage();

    Damage damage();

    String strikeSound();

    String swingSound();

    String computeEffectId();

    AttackAction computeAttackAction();

    /**
     *  Check if resource is enough to attack.
     * @return
     */
    String checkResourceToAttack(Player player);


    void consumeAttributes(Player player);

    boolean isWithinAttackRange(Coordinate coordinate1, Coordinate coordinate2);


}
