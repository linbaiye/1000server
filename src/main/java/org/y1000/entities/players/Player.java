package org.y1000.entities.players;

import org.y1000.entities.AttackableActiveEntity;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.ViolentCreature;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.entities.projectile.Projectile;
import org.y1000.kungfu.AssistantKungFu;
import org.y1000.kungfu.KungFuBook;
import org.y1000.item.*;
import org.y1000.kungfu.attack.AttackKungFu;
import org.y1000.kungfu.FootKungFu;
import org.y1000.kungfu.breath.BreathKungFu;
import org.y1000.kungfu.protect.ProtectKungFu;
import org.y1000.message.clientevent.ClientAttackEvent;
import org.y1000.message.clientevent.ClientEvent;
import org.y1000.realm.Realm;
import org.y1000.util.Coordinate;

import java.util.Optional;

public interface Player extends ViolentCreature {

    default boolean isMale() {
        return true;
    }

    void joinReam(Realm realm);

    void joinRealm(Realm realm, Coordinate coordinate);

    Realm getRealm();

    void leaveRealm();

    default Optional<FootKungFu> footKungFu() {
        return Optional.empty();
    }

    default Optional<Weapon> weapon() {
        return Optional.empty();
    }

    AttackKungFu attackKungFu();

    default boolean tradeEnabled() {
        return true;
    }

    Inventory inventory();

    void handleClientEvent(ClientEvent clientEvent);

    void attack(ClientAttackEvent event, AttackableActiveEntity target);

    Optional<ArmorEquipment> hat();

    Optional<ArmorEquipment> chest();

    Optional<Hair> hair();

    Optional<ArmorEquipment> wrist();

    Optional<ArmorEquipment> boot();

    Optional<Clothing> clothing();

    Optional<Trouser> trouser();

    void gainHeadLife(int v);

    void gainArmLife(int v);

    void gainLegLife(int v);

    KungFuBook kungFuBook();

    Optional<ProtectKungFu> protectKungFu();

    Optional<BreathKungFu> breathKungFu();

    Optional<AssistantKungFu> assistantKungFu();

    int age();

    int power();

    int maxPower();

    int innerPower();

    int maxInnerPower();

    int outerPower();

    int maxOuterPower();

    int maxEnergy();

    int energy();

    void consumePower(int amount);

    void consumeInnerPower(int amount);

    void consumeOuterPower(int amount);

    void consumeLife(int amount);

    void gainPower(int v);

    void gainInnerPower(int v);

    void gainOuterPower(int v);

    void gainLife(int v);

    void gainAttackExp(int amount);

    void gainRangedAttackExp(int amount);

    void gainAssistantExp(int amount);

    Armor armor();

    int headPercent();
    int armPercent();
    int legPercent();

    int attackedByAoe(Damage damage, int hit);

    boolean consumeItem(int slotId);

    default boolean canDrag(Player target, int ropeSlot) {
        if (stateEnum() == State.DIE || stateEnum() == State.FROZEN) {
            return false;
        }
        if (target == null || target.stateEnum() != State.DIE) {
            return false;
        }
        if (target.coordinate().directDistance(coordinate()) > 4) {
            return false;
        }
        Item item = inventory().getItem(ropeSlot);
        return item != null && item.name().equals("追魂索");
    }

    void onProjectileReachTarget(Projectile projectile);
}
