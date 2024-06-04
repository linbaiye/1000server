package org.y1000.kungfu.attack;


import org.y1000.entities.PhysicalEntity;
import org.y1000.entities.creatures.State;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.kungfu.KungFu;
import org.y1000.message.clientevent.ClientAttackEvent;

public interface AttackKungFu extends KungFu {

    int getBodyDamage();

    int getBodyArmor();

    int getAttackSpeed();

    int getRecovery();

    State randomAttackState();

    AttackKungFuType getType();

    void attackAgain(PlayerImpl player);

    void startAttack(PlayerImpl player, ClientAttackEvent event, PhysicalEntity target);


    boolean isRanged();
}
