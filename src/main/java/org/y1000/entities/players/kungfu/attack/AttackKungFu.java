package org.y1000.entities.players.kungfu.attack;


import org.y1000.entities.players.kungfu.LevelKungFu;

public interface AttackKungFu extends LevelKungFu {

    int getBodyDamage();

    int getBodyArmor();

    AttackKungFuType getType();

}
