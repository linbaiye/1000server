package org.y1000.entities.players;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.PhysicalEntity;
import org.y1000.entities.Projectile;
import org.y1000.entities.attribute.Damage;
import org.y1000.entities.creatures.*;
import org.y1000.entities.GroundedItem;
import org.y1000.entities.players.event.PlayerUnequipEvent;
import org.y1000.item.*;
import org.y1000.entities.players.event.CharacterChangeWeaponEvent;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.entities.players.kungfu.KungFuBook;
import org.y1000.entities.players.kungfu.attack.AttackKungFu;
import org.y1000.entities.players.kungfu.attack.AttackKungFuType;
import org.y1000.message.clientevent.*;
import org.y1000.message.serverevent.*;
import org.y1000.entities.Direction;
import org.y1000.entities.players.kungfu.FootKungFu;
import org.y1000.message.*;
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

    private final Inventory inventory;

    private final KungFuBook kungFuBook;

    private final boolean male;

    private final Map<EquipmentType, Equipment> equippedEquipments;

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
                      Trouser trouser,
                      Clothing clothing,
                      FootKungFu footKungfu
                      ) {
        super(id, coordinate, Direction.DOWN, name, STATE_MILLIS);
        Objects.requireNonNull(kungFuBook, "kungFuBook can't be null.");
        Objects.requireNonNull(attackKungFu, "attackKungFu can't be null.");
        Objects.requireNonNull(inventory, "inventory can't be null.");
        this.inventory = inventory;
        this.attackKungFu = attackKungFu;
        this.kungFuBook = kungFuBook;
        this.male = male;
        this.footKungfu = footKungfu;
        this.equippedEquipments = new HashMap<>();
        equippedEquipments.put(EquipmentType.HAT, hat);
        equippedEquipments.put(EquipmentType.WEAPON, weapon);
        equippedEquipments.put(EquipmentType.BOOT, boot);
        equippedEquipments.put(EquipmentType.CHEST, chest);
        equippedEquipments.put(EquipmentType.CLOTHING, clothing);
        equippedEquipments.put(EquipmentType.WRIST, wrist);
        equippedEquipments.put(EquipmentType.TROUSER, trouser);
        equippedEquipments.put(EquipmentType.HAIR, hair);
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
        int slot = inventory.add(item);
        if (slot > 0) {
            log.info("Picked grounded item {}.", groundedItem);
            emitEvent(new PlayerPickedItemEvent(this, slot, inventory.getItem(slot), groundedItem));
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


    private void unequip(EquipmentType type) {
        if (inventory.isFull()) {
            emitEvent(PlayerTextEvent.inventoryFull(this));
            return;
        }
        Equipment equipped = equippedEquipments.remove(type);
        if (equipped == null) {
            log.error("Trying to unequip from non equipped slot {}", type);
            return;
        }
        State st = null;
        if (equipped instanceof Weapon weapon) {
            cooldownAttack();
            if (weapon.kungFuType() != AttackKungFuType.QUANFA) {
                attackKungFu = kungFuBook.findBasic(AttackKungFuType.QUANFA);
                state().attackKungFuTypeChanged(this);
                st = stateEnum();
                log.debug("Changed to state {}.", st);
            }
        }
        emitEvent(new PlayerUnequipEvent(this, equipped.equipmentType(), st, attackKungFu.level()));
        int slot = inventory.add(equipped);
        emitEvent(new UpdateInventorySlotEvent(this, slot, equipped));
    }

    private void handleInventorySlotDoubleClick(int slotId) {
        Item item = inventory.getItem(slotId);
        if (item == null) {
            return;
        }
        if (item instanceof AbstractEquipment equipment) {
            equip(slotId, equipment);
        }
    }


    @Override
    public void handleEvent(ClientEvent clientEvent) {
        if (clientEvent instanceof ClientDoubleClickSlotEvent doubleClickSlotEvent) {
            handleInventorySlotDoubleClick(doubleClickSlotEvent.sourceSlot());
        } else if (clientEvent instanceof ClientInventoryEvent inventoryEvent) {
            inventory.handleClientEvent(this, inventoryEvent, this);
        } else if (clientEvent instanceof ClientPickItemEvent pickItemEvent) {
            realm.findInsight(this, pickItemEvent.id()).ifPresent(this::handlePickItem);
        } else if (clientEvent instanceof ClientUnequipEvent unequipEvent) {
            log.debug("Received unequip type {}.", unequipEvent.type());
            unequip(unequipEvent.type());
        } else {
            eventQueue.add(clientEvent);
        }
    }

    private <T extends Equipment> Optional<T> getEquipment(EquipmentType type, Class<T> clazz) {
        Equipment equipment = equippedEquipments.get(type);
        return equipment != null && equipment.getClass().isAssignableFrom(clazz) ?
                Optional.of(clazz.cast(equipment)) : Optional.empty();
    }

    @Override
    public Optional<Hat> hat() {
        return getEquipment(EquipmentType.HAT, Hat.class);
    }

    @Override
    public Optional<Chest> chest() {
        return getEquipment(EquipmentType.CHEST, Chest.class);
    }

    @Override
    public Optional<Hair> hair() {
        return getEquipment(EquipmentType.HAIR, Hair.class);
    }

    @Override
    public Optional<Wrist> wrist() {
        return getEquipment(EquipmentType.WRIST, Wrist.class);
    }

    @Override
    public Optional<Boot> boot() {
        return getEquipment(EquipmentType.BOOT, Boot.class);
    }

    @Override
    public Optional<Clothing> clothing() {
        return getEquipment(EquipmentType.CLOTHING, Clothing.class);
    }

    @Override
    public Optional<Trouser> trouser() {
        return getEquipment(EquipmentType.TROUSER, Trouser.class);
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

    private void equipWeaponFromSlot(int slot, Weapon weaponToEquip) {
        inventory.remove(slot);
        weapon().ifPresent(weapon -> inventory.put(slot, weapon));
        equippedEquipments.put(EquipmentType.WEAPON, weaponToEquip);
        if (attackKungFu.getType() == weaponToEquip.kungFuType()) {
            emitEvent(new CharacterChangeWeaponEvent(this, slot, inventory.getItem(slot), weaponToEquip.name()));
            return;
        }
        this.attackKungFu = kungFuBook.findBasic(weaponToEquip.kungFuType());
        cooldownAttack();
        state().attackKungFuTypeChanged(this);
        emitEvent(new CharacterChangeWeaponEvent(this, slot, inventory.getItem(slot), weaponToEquip.name(), this.attackKungFu));
    }

    private void equip(int slotId, AbstractEquipment equipmentInSlot) {
        if (equipmentInSlot.equipmentType() == EquipmentType.WEAPON) {
            equipWeaponFromSlot(slotId, (Weapon) equipmentInSlot);
            return;
        }
        inventory.remove(slotId);
        Equipment currentEquipped = equippedEquipments.put(equipmentInSlot.equipmentType(), equipmentInSlot);
        emitEvent(new PlayerEquipEvent(this, equipmentInSlot.name()));
        if (currentEquipped != null) {
            inventory.put(slotId, currentEquipped);
        }
        emitEvent(new UpdateInventorySlotEvent(this, slotId, currentEquipped));
    }

    @Override
    public int attackSpeed() {
        int kungfuSpeed = attackKungFu != null ? attackKungFu.getAttackSpeed() : 0;
        return kungfuSpeed + 70;
    }

    public Optional<Weapon> weapon() {
        return getEquipment(EquipmentType.WEAPON, Weapon.class);
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
