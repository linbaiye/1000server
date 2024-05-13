package org.y1000.entities.players.kungfu.attack;


import org.y1000.entities.Entity;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.entities.players.kungfu.LevelKungFu;
import org.y1000.message.clientevent.ClientAttackEvent;

public interface AttackKungFu extends LevelKungFu {

    int getBodyDamage();

    int getBodyArmor();

    int getAttackSpeed();

    int getRecovery();

    void attack(PlayerImpl player, ClientAttackEvent event, Entity target);

    void attack(PlayerImpl player, Entity target);

    int millisPerSprite(boolean below50);

    AttackKungFuType getType();

}
