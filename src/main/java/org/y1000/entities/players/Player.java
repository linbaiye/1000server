package org.y1000.entities.players;

import org.y1000.entities.creatures.ViolentCreature;
import org.y1000.entities.item.Weapon;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.entities.players.kungfu.attack.AttackKungFu;
import org.y1000.entities.players.kungfu.FootKungFu;
import org.y1000.message.clientevent.ClientEvent;
import org.y1000.realm.Realm;

import java.util.Optional;

public interface Player extends ViolentCreature {

    default boolean isMale() {
        return true;
    }

    void joinReam(Realm realm);

    Realm getRealm();

    void leaveRealm();

    void equipWeapon(Weapon weapon);

    void changeAttackKungFu(AttackKungFu attackKungFu);

    default Optional<FootKungFu> footKungFu() {
        return Optional.empty();
    }

    Inventory inventory();

    default Optional<Weapon> weapon() {
        return Optional.empty();
    }

    AttackKungFu attackKungFu();

    void handleEvent(ClientEvent clientEvent);
}
