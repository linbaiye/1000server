package org.y1000.entities.players;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.PhysicalEntity;
import org.y1000.entities.Projectile;
import org.y1000.entities.attribute.Damage;
import org.y1000.entities.creatures.*;
import org.y1000.entities.GroundedItem;
import org.y1000.item.*;
import org.y1000.entities.players.event.CharacterChangeWeaponEvent;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.entities.players.kungfu.KungFuBook;
import org.y1000.entities.players.kungfu.attack.AttackKungFu;
import org.y1000.entities.players.kungfu.attack.AttackKungFuType;
import org.y1000.message.clientevent.*;
import org.y1000.message.serverevent.JoinedRealmEvent;
import org.y1000.entities.Direction;
import org.y1000.entities.players.kungfu.FootKungFu;
import org.y1000.message.*;
import org.y1000.message.serverevent.PlayerLeftEvent;
import org.y1000.message.serverevent.PlayerPickedItemEvent;
import org.y1000.realm.Realm;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
public final class PlayerImpl extends AbstractViolentCreature<PlayerImpl, PlayerState> implements Player, EventEmiter {

    private Realm realm;

    private final Queue<ClientEvent> eventQueue;

    private AttackKungFu attackKungFu;

    private FootKungFu footKungfu;

    private Weapon weapon;

    private final Inventory inventory;

    private final KungFuBook kungFuBook;

    private Chest chest;

    private Hat hat;

    private Hair hair;

    private Wrist wrist;

    private Boot boot;

    private final boolean male;

    private static final Map<State, Integer> STATE_MILLIS = new HashMap<>() {{
        put(State.IDLE, 2200);
        put(State.WALK, 840);
        put(State.RUN, 420);
        put(State.FLY, 360);
        put(State.COOLDOWN, 1400);
        put(State.FIST, AttackKungFuType.QUANFA.below50Millis());
        put(State.KICK, AttackKungFuType.QUANFA.above50Millis());
        put(State.HURT, 280);
        put(State.ENFIGHT_WALK, 840);
        put(State.BOW, AttackKungFuType.BOW.above50Millis());
        put(State.SWORD2H, AttackKungFuType.SWORD.above50Millis());
        put(State.SWORD, AttackKungFuType.SWORD.below50Millis());
        put(State.BLADE2H, AttackKungFuType.BLADE.above50Millis());
        put(State.BLADE, AttackKungFuType.BLADE.below50Millis());
        put(State.AXE, AttackKungFuType.AXE.below50Millis());
    }};

    @Builder
    public PlayerImpl(long id,
                      Coordinate coordinate,
                      String name,
                      Inventory inventory,
                      Weapon weapon,
                      AttackKungFu attackKungFu,
                      KungFuBook kungFuBook,
                      boolean male,
                      Hat hat,
                      Chest chest,
                      Hair hair,
                      Wrist wrist,
                      Boot boot,
                      FootKungFu footKungfu
                      ) {
        super(id, coordinate, Direction.DOWN, name, STATE_MILLIS);
        Objects.requireNonNull(kungFuBook, "kungFuBook can't be null.");
        Objects.requireNonNull(attackKungFu, "attackKungFu can't be null.");
        Objects.requireNonNull(inventory, "inventory can't be null.");
        this.inventory = inventory;
        this.attackKungFu = attackKungFu;
        this.weapon = weapon;
        this.kungFuBook = kungFuBook;
        this.male = male;
        this.chest = chest;
        this.hat = hat;
        this.hair = hair;
        this.wrist = wrist;
        this.boot = boot;
        this.footKungfu = footKungfu;
        changeState(new PlayerStillState(getStateMillis(State.IDLE)));
        eventQueue = new ConcurrentLinkedQueue<>();
    }

    @Override
    public boolean isMale() {
        return male;
    }

    public Optional<ClientEvent> takeClientEvent() {
        return Optional.ofNullable(eventQueue.poll());
    }

    RealmMap realmMap() {
        return realm.map();
    }

    @Override
    public Optional<FootKungFu> footKungFu() {
        return Optional.ofNullable(footKungfu);
    }

    @Override
    public void pickItem(Item item, GroundedItem groundedItem){
        int slot = inventory.pick(item);
        if (slot > 0) {
            log.info("Picked grounded item {}.", groundedItem);
            emitEvent(new PlayerPickedItemEvent(this, slot, inventory.get(slot), groundedItem));
        }
    }


    @Override
    public AttackKungFu attackKungFu() {
        return attackKungFu;
    }

    private void handlePickItem(PhysicalEntity entity) {
        if (!(entity instanceof GroundedItem groundedItem)) {
            return;
        }
        if (!groundedItem.canPickAt(coordinate())) {
            log.warn("Tried pick item {} from {}.", groundedItem, coordinate());
            emitEvent(PlayerTextEvent.tooFarAway(this));
            return;
        }
        if (inventory.canPick(groundedItem)) {
            emitEvent(new GetGroundItemEvent(this, groundedItem));
        }
    }


    @Override
    public void handleEvent(ClientEvent clientEvent) {
        if (clientEvent instanceof ClientInventoryEvent inventoryEvent) {
            inventory.handleClientEvent(this, inventoryEvent, this);
        } else if (clientEvent instanceof ClientPickItemEvent pickItemEvent) {
            realm.findInsight(this, pickItemEvent.id()).ifPresent(this::handlePickItem);
        } else {
            eventQueue.add(clientEvent);
        }
    }

    @Override
    public Optional<Hat> hat() {
        return Optional.ofNullable(hat);
    }

    @Override
    public Optional<Chest> chest() {
        return Optional.ofNullable(chest);
    }

    @Override
    public Optional<Hair> hair() {
        return Optional.ofNullable(hair);
    }

    @Override
    public Optional<Wrist> wrist() {
        return Optional.ofNullable(wrist);
    }

    @Override
    public Optional<Boot> boot() {
        return Optional.ofNullable(boot);
    }


    @Override
    public void changeState(PlayerState newState) {
        log().debug("Change state from {} to {}.", state(), newState);
        super.changeState(newState);
    }

    @Override
    public void joinReam(Realm realm) {
        if (this.realm != null) {
            leaveRealm();
        }
        this.realm = realm;
        realmMap().occupy(this);
        changeState(new PlayerStillState(getStateMillis(State.IDLE)));
        changeDirection(Direction.DOWN);
        emitEvent(new JoinedRealmEvent(this, coordinate(), inventory));
    }

    private void attackedBy(int hit) {
        if (handleAttacked(this, hit, () -> PlayerHurtState.hurt(this))) {
            eventQueue.clear();
        }
    }

    @Override
    public void attackedBy(ViolentCreature attacker) {
        attackedBy(attacker.hit());
    }

    @Override
    public void attackedBy(Projectile projectile) {
        attackedBy(projectile.getHit());
    }

    @Override
    public Realm getRealm() {
        return this.realm;
    }

    @Override
    public void leaveRealm() {
        if (realm != null) {
            realmMap().free(this);
        }
        changeState(PlayerFrozenState.Instance);
        emitEvent(new PlayerLeftEvent(this));
    }

    @Override
    public void equipWeapon(Weapon weapon) {
        if (weapon == null) {
            throw new IllegalStateException();
        }
        if (stateEnum() == State.DIE) {
            return;
        }
        int slot = inventory.remove(weapon);
        if (this.weapon != null) {
            inventory.put(slot, this.weapon);
        }
        log.debug("Use weapon {} to replace weapon {}.", weapon.name(), this.weapon != null ? this.weapon.name() : "");
        this.weapon = weapon;
        if (attackKungFu.getType() == weapon.kungFuType()) {
            emitEvent(new CharacterChangeWeaponEvent(this, slot, inventory.getItem(slot), this.weapon.name()));
            return;
        }
        this.attackKungFu = kungFuBook.findBasic(weapon.kungFuType());
        cooldownAttack();
        state().attackKungFuTypeChanged(this);
        emitEvent(new CharacterChangeWeaponEvent(this, slot, inventory.getItem(slot), this.weapon.name(), this.attackKungFu));
    }


    @Override
    public int attackSpeed() {
        int kungfuSpeed = attackKungFu != null ? attackKungFu.getAttackSpeed() : 0;
        return kungfuSpeed + 70;
    }

    public Optional<Weapon> weapon() {
        return Optional.ofNullable(weapon);
    }


    @Override
    public PlayerInterpolation captureInterpolation() {
        return PlayerInterpolation.FromPlayer(this, state().elapsedMillis());
    }


    @Override
    public void update(int delta) {
        cooldown(delta);
        state().update(this, delta);
    }

    public void clearEventQueue() {
        eventQueue.clear();
    }

    public int recovery() {
        int kfr = attackKungFu != null ? attackKungFu.getRecovery() : 0;
        return 50 + kfr;
    }

    @Override
    public int hit() {
        return 0;
    }

    @Override
    public Damage damage() {
        return null;
    }

    @Override
    public String toString() {
        return "PlayerImpl{" +
                "id=" + id() +
                ", coordinate=" + coordinate() +
                ", direction=" + direction() +
                ", state=" + stateEnum() +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerImpl player = (PlayerImpl) o;
        return id() == player.id();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id());
    }

    @Override
    protected Logger log() {
        return log;
    }
}
