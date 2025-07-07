package org.y1000.entities.players;

import org.y1000.entities.creatures.ViolentCreature;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.entities.players.event.PlayerEvent;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.entities.projectile.Projectile;
import org.y1000.guild.GuildMembership;
import org.y1000.kungfu.AssistantKungFu;
import org.y1000.kungfu.KungFuBook;
import org.y1000.item.*;
import org.y1000.kungfu.attack.AttackKungFu;
import org.y1000.kungfu.FootKungFu;
import org.y1000.kungfu.breath.BreathKungFu;
import org.y1000.kungfu.protect.ProtectKungFu;
import org.y1000.message.PlayerEventListener;
import org.y1000.message.input.SelfHandleInput;
import org.y1000.realm.Realm;
import org.y1000.util.Coordinate;

import java.util.*;

public interface Player extends ViolentCreature {

    default boolean isMale() {
        return true;
    }
    void joinRealm(Realm realm, PlayerEventListener messageListener);

    void joinRealm(Realm realm, Coordinate coordinate, PlayerEventListener messageListener);

    Realm getRealm();

    void leaveRealm();

    boolean isLeftGame();

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

    void handleInput(SelfHandleInput input);

    void attack(Npc npc);

    Optional<ArmorEquipment> hat();

    Optional<ArmorEquipment> chest();

    Optional<SexualEquipment> hair();

    Optional<ArmorEquipment> wrist();

    Optional<ArmorEquipment> boot();

    Optional<SexualEquipment> clothing();

    Optional<SexualEquipment> trouser();

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
        return false;
//        if (oldStateEnum() == OldPlayerStateEnum.DIE || oldStateEnum() == OldPlayerStateEnum.Turn ||
//                target.equals(this)) {
//            return false;
//        }
//        if (target.oldStateEnum() != OldPlayerStateEnum.DIE) {
//            return false;
//        }
//        if (target.coordinate().directDistance(coordinate()) > 4) {
//            return false;
//        }
//        Item item = inventory().getItem(ropeSlot);
//        return item != null && item.name().equals("追魂索");
    }

    void onProjectileReachTarget(Projectile projectile);

    PlayerExperiencedAgedAttribute innerPowerAttribute();

    PlayerExperiencedAgedAttribute outerPowerAttribute();

    PlayerExperiencedAgedAttribute powerAttribute();

    PlayerLife headLife();
    PlayerLife armLife();

    PlayerLife legLife();

    YinYang yinyang();

    int revivalExp();

    int team();

    Optional<GuildMembership> guildMembership();

    void joinGuild(GuildMembership membership);

    void quitGuild();

    void cancelBuff();

    default List<Equipment> getEquipments() {
        List<Equipment> ret = new ArrayList<>();
        weapon().ifPresent(ret::add);
        hat().ifPresent(ret::add);
        hair().ifPresent(ret::add);
        chest().ifPresent(ret::add);
        clothing().ifPresent(ret::add);
        boot().ifPresent(ret::add);
        trouser().ifPresent(ret::add);
        wrist().ifPresent(ret::add);
        return ret;
    }

    void sendEvent(PlayerEvent event);

    PlayerStateEnum stateEnum();

    default boolean isDead() {
        return stateEnum() == PlayerStateEnum.Die;
    }

    int accuracy();

    int attacked(Damage damage, int accuracy);
}

