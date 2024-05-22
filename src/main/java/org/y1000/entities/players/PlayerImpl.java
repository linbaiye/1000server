package org.y1000.entities.players;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.Projectile;
import org.y1000.entities.attribute.Damage;
import org.y1000.entities.creatures.*;
import org.y1000.entities.item.Item;
import org.y1000.entities.item.Weapon;
import org.y1000.entities.players.event.ChangeWeaponEvent;
import org.y1000.entities.players.event.InventorySlotSwappedEvent;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.entities.players.kungfu.KungFuBook;
import org.y1000.entities.players.kungfu.attack.AttackKungFu;
import org.y1000.entities.players.kungfu.attack.AttackKungFuType;
import org.y1000.message.clientevent.ClientClickedInventorySlotEvent;
import org.y1000.message.clientevent.ClientInventoryEvent;
import org.y1000.message.clientevent.ClientSwapInventoryEvent;
import org.y1000.message.serverevent.JoinedRealmEvent;
import org.y1000.entities.Direction;
import org.y1000.entities.players.kungfu.FootKungFu;
import org.y1000.message.*;
import org.y1000.message.clientevent.ClientEvent;
import org.y1000.message.serverevent.PlayerLeftEvent;
import org.y1000.realm.Realm;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
public final class PlayerImpl extends AbstractViolentCreature<PlayerImpl, PlayerState> implements Player {

    private Realm realm;

    private final Queue<ClientEvent> eventQueue;

    private AttackKungFu attackKungFu;

    private FootKungFu footKungfu;

    private Weapon weapon;

    private final Inventory inventory;


    private final KungFuBook kungFuBook;

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
    }};

    @Builder
    public PlayerImpl(long id,
                      Coordinate coordinate,
                      String name,
                      Inventory inventory,
                      Weapon weapon,
                      AttackKungFu attackKungFu,
                      KungFuBook kungFuBook) {
        super(id, coordinate, Direction.DOWN, name, STATE_MILLIS);
        this.inventory = inventory;
        this.attackKungFu = attackKungFu;
        this.weapon = weapon;
        this.kungFuBook = kungFuBook;
        changeState(new PlayerStillState(getStateMillis(State.IDLE)));
        eventQueue = new ConcurrentLinkedQueue<>();
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
    public AttackKungFu attackKungFu() {
        return attackKungFu;
    }

    private void handleInventoryEvent(ClientInventoryEvent inventoryEvent) {
        if (!state().canHandle(inventoryEvent)) {
            return;
        }
        if (inventoryEvent instanceof ClientSwapInventoryEvent swapInventoryEvent &&
                inventory.swap(swapInventoryEvent.sourceSlot(), swapInventoryEvent.destinationSlot())) {
            emitEvent(new InventorySlotSwappedEvent(this, swapInventoryEvent.sourceSlot(), swapInventoryEvent.destinationSlot()));
        } else if (inventoryEvent instanceof ClientClickedInventorySlotEvent slotEvent) {
            Item item = inventory.getItem(slotEvent.sourceSlot());
        }
    }

    @Override
    public void handleEvent(ClientEvent clientEvent) {
        if (clientEvent instanceof ClientInventoryEvent inventoryEvent) {
            handleInventoryEvent(inventoryEvent);
        } else {
            eventQueue.add(clientEvent);
        }
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
        emitEvent(new JoinedRealmEvent(this, coordinate()));
    }

    private void attackedBy(int hit) {
        if (handleAttacked(this, hit, () -> PlayerHurtState.hurt(this))) {
            eventQueue.clear();
        }
    }

    @Override
    public Inventory inventory() {
        return inventory;
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
        if (stateEnum() == State.DIE) {
            return;
        }
        int slot = inventory.remove(weapon);
        if (this.weapon != null) {
            inventory.put(slot, this.weapon);
        }
        this.weapon = weapon;
        if (attackKungFu.getType() != weapon.kungFuType()) {
            this.attackKungFu = kungFuBook.findBasic(weapon.kungFuType());
        }
        emitEvent(new ChangeWeaponEvent(this, slot));
    }

    @Override
    public void changeAttackKungFu(AttackKungFu attackKungFu) {

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
    public String name() {
        return "";
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
