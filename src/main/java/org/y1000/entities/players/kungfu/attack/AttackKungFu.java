package org.y1000.entities.players.kungfu.attack;


import org.y1000.entities.Entity;
import org.y1000.entities.creatures.State;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.entities.players.kungfu.LevelKungFu;
import org.y1000.message.clientevent.ClientAttackEvent;

public interface AttackKungFu extends LevelKungFu {

    int getBodyDamage();

    int getBodyArmor();

    int getAttackSpeed();

    int getRecovery();

    State randomAttackState();

    int attackActionMillis(State state);

    AttackKungFuType getType();
}
