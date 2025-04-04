package org.y1000.kungfu.attack;


import org.y1000.entities.AttackableActiveEntity;
import org.y1000.entities.players.Armor;
import org.y1000.entities.players.Damage;
import org.y1000.entities.creatures.State;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.kungfu.KungFu;
import org.y1000.message.clientevent.ClientAttackEvent;

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

    State randomAttackState();

    AttackKungFuType getType();

    void attackAgain(PlayerImpl player);

    void startAttack(PlayerImpl player, ClientAttackEvent event, AttackableActiveEntity target);

    boolean isRanged();

    int headDamage();

    int armDamage();

    int legDamage();

    Damage damage();

    String strikeSound();

    String swingSound();

    Integer computeEffectId();

}
