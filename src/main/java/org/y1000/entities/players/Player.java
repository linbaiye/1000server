package org.y1000.entities.players;

import org.y1000.entities.creatures.ViolentCreature;
import org.y1000.entities.GroundedItem;
import org.y1000.item.Chest;
import org.y1000.item.Hat;
import org.y1000.item.Item;
import org.y1000.item.Weapon;
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

    default Optional<FootKungFu> footKungFu() {
        return Optional.empty();
    }

    void pickItem(Item item, GroundedItem groundedItem);

    default Optional<Weapon> weapon() {
        return Optional.empty();
    }

    AttackKungFu attackKungFu();

    void handleEvent(ClientEvent clientEvent);

    Optional<Hat> hat();

    Optional<Chest> chest();
}
