package org.y1000.entities.players;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.*;
import org.y1000.entities.attribute.Damage;
import org.y1000.entities.creatures.*;
import org.y1000.entities.players.event.*;
import org.y1000.entities.players.fight.PlayerAttackState;
import org.y1000.entities.players.fight.PlayerCooldownState;
import org.y1000.entities.players.fight.PlayerWaitDistanceState;
import org.y1000.item.*;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.kungfu.AssistantKungFu;
import org.y1000.kungfu.KungFu;
import org.y1000.kungfu.KungFuBook;
import org.y1000.kungfu.attack.AttackKungFu;
import org.y1000.kungfu.attack.AttackKungFuType;
import org.y1000.kungfu.breath.BreathKungFu;
import org.y1000.kungfu.protect.ProtectKungFu;
import org.y1000.message.clientevent.*;
import org.y1000.message.serverevent.*;
import org.y1000.kungfu.FootKungFu;
import org.y1000.message.*;
import org.y1000.realm.Realm;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.*;

@Slf4j
public final class PlayerImpl extends AbstractViolentCreature<PlayerImpl, PlayerState> implements Player {

    private Realm realm;

    private AttackKungFu attackKungFu;

    private FootKungFu footKungfu;

    private ProtectKungFu protectKungFu;

    private BreathKungFu breathKungFu;

    private AssistantKungFu assistantKungFu;

    private final Inventory inventory;

    private final KungFuBook kungFuBook;

    private final boolean male;

    private final Map<EquipmentType, Equipment> equippedEquipments;

    public static final int INNATE_ATTACKSPEED = 70;

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
        put(State.THROW, AttackKungFuType.THROW.above50Millis());
        put(State.SWORD2H, AttackKungFuType.SWORD.above50Millis());
        put(State.SWORD, AttackKungFuType.SWORD.below50Millis());
        put(State.BLADE2H, AttackKungFuType.BLADE.above50Millis());
        put(State.BLADE, AttackKungFuType.BLADE.below50Millis());
        put(State.AXE, AttackKungFuType.AXE.below50Millis());
        put(State.SPEAR, AttackKungFuType.SPEAR.below50Millis());
        put(State.SIT, 750);
        put(State.STANDUP, 750);
        put(State.DIE, 1500);
        put(State.HELLO, 750);
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
                      FootKungFu footKungfu,
                      ProtectKungFu protectKungFu,
                      BreathKungFu breathKungFu
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
        initProtectKungFu(protectKungFu);
        initBreathKungFu(breathKungFu);
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
    }

    private void initProtectKungFu(ProtectKungFu protectKungFu) {
        if (protectKungFu != null && breathKungFu != null) {
            throw new IllegalStateException("BreathKungFu is not null.");
        }
        this.protectKungFu = protectKungFu;
    }

    private void initBreathKungFu(BreathKungFu breathKungFu) {
        if (protectKungFu != null && breathKungFu != null) {
            throw new IllegalStateException("BreathKungFu is not null.");
        }
        this.breathKungFu = breathKungFu;
    }

    @Override
    public boolean isMale() {
        return male;
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

    public boolean hasFightingEntity() {
        return getFightingEntity() != null;
    }

    public void disableFootKungFuQuietly() {
        if (footKungfu != null) {
            emitEvent(PlayerToggleKungFuEvent.disableQuietly(this, footKungfu));
        }
        footKungfu = null;
    }

    public void disableBreathKungFuQuietly() {
        if (breathKungFu != null) {
            emitEvent(PlayerToggleKungFuEvent.disableQuietly(this, breathKungFu));
        }
        breathKungFu = null;
    }


    private void unequip(EquipmentType type) {
        log.debug("Received unequip type {}.", type);
        if (inventory.isFull()) {
            emitEvent(PlayerTextEvent.inventoryFull(this));
            return;
        }
        Equipment equipped = equippedEquipments.remove(type);
        if (equipped == null) {
            log.error("Trying to unequip from non equipped slot {}", type);
            return;
        }
        if (equipped instanceof Weapon weapon && weapon.kungFuType() != AttackKungFuType.QUANFA) {
            changeAttackKungFu(kungFuBook.findUnnamedAttack(AttackKungFuType.QUANFA));
        }
        emitEvent(new PlayerUnequipEvent(this, equipped.equipmentType()));
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

    private void handleClickAttackKungFu(AttackKungFu newAttack) {
        if (this.attackKungFu.name().equals(newAttack.name())) {
            return;
        }
        if (this.attackKungFu.getType() == newAttack.getType()) {
            this.attackKungFu = newAttack;
            cooldownAttack();
            emitEvent(PlayerToggleKungFuEvent.enable(this, this.attackKungFu));
            return;
        }
        int slot = inventory.findWeaponSlot(newAttack.getType());
        if (slot == 0) {
            emitEvent(PlayerTextEvent.noWeapon(this));
            return;
        }
        equipWeaponFromSlot(slot, (Weapon) inventory.getItem(slot));
        changeAttackKungFu(newAttack);
    }


    private void toggleProtectionKungFu(ProtectKungFu newProtection) {
        disableBreathKungFuQuietly();
        if (protectKungFu != null && protectKungFu.name().equals(newProtection.name())) {
            emitEvent(PlayerToggleKungFuEvent.disable(this, protectKungFu));
            protectKungFu = null;
            return;
        }
        protectKungFu = newProtection;
        emitEvent(PlayerToggleKungFuEvent.enable(this, protectKungFu));
    }

    private void toggleBreathingKungFu(BreathKungFu newBreath) {
        if (breathKungFu != null) {
            if (breathKungFu.name().equals(newBreath.name())) {
                this.breathKungFu = null;
                emitEvent(PlayerToggleKungFuEvent.disable(this, newBreath));
            } else {
                this.breathKungFu = newBreath;
                emitEvent(PlayerToggleKungFuEvent.enable(this, newBreath));
            }
            return;
        }
        if (state().canSitDown()) {
            sitDown(true);
        }
        if (stateEnum() == State.SIT) {
            if (this.protectKungFu != null) {
                emitEvent(PlayerToggleKungFuEvent.disableQuietly(this, protectKungFu));
                this.protectKungFu = null;
            }
            this.breathKungFu = newBreath;
            emitEvent(PlayerToggleKungFuEvent.enable(this, newBreath));
        }
    }

    private void toggleFootKungFu(FootKungFu newKungFu) {
        if (!state().canUseFootKungFu()) {
            return;
        }
        clearFightingEntity();
        if (this.footKungfu != null ){
            if (newKungFu.name().equals(this.footKungfu.name())) {
                log.debug("Disable bufa.");
                emitEvent(PlayerToggleKungFuEvent.disable(this, this.footKungfu));
                this.footKungfu = null;
            } else {
                this.footKungfu = newKungFu;
                emitEvent(PlayerToggleKungFuEvent.enable(this, this.footKungfu));
            }
            return;
        }
        if (stateEnum() == State.SIT) {
            changeState(new PlayerStandUpState(this));
            emitEvent(new PlayerStandUpEvent(this, true));
        } else if (stateEnum() != State.WALK && stateEnum() != State.ENFIGHT_WALK) {
            log.debug("Enable bufa.");
            changeState(PlayerStillState.idle(this));
            emitEvent(new SetPositionEvent(this, direction(), coordinate()));
        }
        disableBreathKungFuQuietly();
        this.footKungfu = newKungFu;
        emitEvent(new PlayerToggleKungFuEvent(this, this.footKungfu));
    }

    private void toggleAssistantKungFu(AssistantKungFu newAssistant) {
        if (this.assistantKungFu != null &&
                this.assistantKungFu.name().equals(newAssistant.name())) {
            emitEvent(PlayerToggleKungFuEvent.disable(this, this.assistantKungFu));
            this.assistantKungFu = null;
        } else {
            this.assistantKungFu = newAssistant;
            emitEvent(PlayerToggleKungFuEvent.enable(this, this.assistantKungFu));
        }
    }


    private void useKungFu(KungFu kungFu) {
        if (stateEnum() == State.DIE) {
            return;
        }
        if (kungFu instanceof FootKungFu newKungFu) {
            toggleFootKungFu(newKungFu);
        } else if (kungFu instanceof ProtectKungFu newProtectKungFu) {
            toggleProtectionKungFu(newProtectKungFu);
        } else if (kungFu instanceof BreathKungFu newBreath) {
            toggleBreathingKungFu(newBreath);
        } else if (kungFu instanceof AssistantKungFu newAssistant) {
            toggleAssistantKungFu(newAssistant);
        } else if (kungFu instanceof AttackKungFu newAttack) {
            handleClickAttackKungFu(newAttack);
        }
    }


    private void sitDown(boolean includeSelf) {
        if (!state().canSitDown()) {
            log.debug("Cant sit down in state {}.", state().stateEnum());
            return;
        }
        if (footKungfu != null) {
            emitEvent(PlayerToggleKungFuEvent.disableQuietly(this, footKungfu));
            footKungfu = null;
        }
        clearFightingEntity();
        changeState(PlayerSitDownState.sit(this));
        emitEvent(new PlayerSitDownEvent(this, includeSelf));
    }

    private void standUp() {
        if (state().canStandUp()) {
            breathKungFu = null;
            changeState(new PlayerStandUpState(this));
            emitEvent(new PlayerStandUpEvent(this));
        }
    }

    private void startAttack(ClientAttackEvent event) {
        if (stateEnum() == State.DIE) {
            return;
        }
        realm.findInsight(this, event.entityId())
                .ifPresent(target -> attackKungFu.startAttack(this,event, target));
    }

    private void move(ClientMovementEvent event) {
        if (state() instanceof MovableState movableState) {
            movableState.move(this, event);
        }
    }

    @Override
    public void handleClientEvent(ClientEvent clientEvent) {
        if (clientEvent instanceof ClientDoubleClickSlotEvent doubleClickSlotEvent) {
            handleInventorySlotDoubleClick(doubleClickSlotEvent.sourceSlot());
        } else if (clientEvent instanceof ClientInventoryEvent inventoryEvent) {
            inventory.handleClientEvent(this, inventoryEvent, this::emitEvent);
        } else if (clientEvent instanceof ClientPickItemEvent pickItemEvent) {
            realm.findInsight(this, pickItemEvent.id()).ifPresent(this::handlePickItem);
        } else if (clientEvent instanceof ClientUnequipEvent unequipEvent) {
            unequip(unequipEvent.type());
        } else if (clientEvent instanceof ClientToggleKungFuEvent useKungFuEvent) {
            kungFuBook().findKungFu(useKungFuEvent.tab(), useKungFuEvent.slot()).ifPresent(this::useKungFu);
        } else if (clientEvent instanceof ClientSitDownEvent) {
            sitDown(false);
        } else if (clientEvent instanceof ClientStandUpEvent) {
            standUp();
        } else if (clientEvent instanceof ClientAttackEvent attackEvent) {
            startAttack(attackEvent);
        } else if (clientEvent instanceof ClientMovementEvent movementEvent) {
            move(movementEvent);
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
        changeState(PlayerStillState.idle(this));
        changeDirection(Direction.DOWN);
        emitEvent(new JoinedRealmEvent(this, coordinate(), inventory));
    }

    private void attackedBy(int hit) {
        handleAttacked(this, hit, (afterHurt) -> PlayerHurtState.hurt(this, afterHurt));
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
        clearFightingEntity();
    }

    private void changeAttackKungFu(AttackKungFu newKungFu) {
        this.attackKungFu = newKungFu;
        cooldownAttack();
        if (state() instanceof PlayerAttackState) {
            changeState(new PlayerCooldownState(cooldown()));
            emitEvent(new PlayerCooldownEvent(this));
        }
        emitEvent(PlayerToggleKungFuEvent.enable(this, attackKungFu));
    }

    private void equipWeaponFromSlot(int slot, Weapon weaponToEquip) {
        inventory.remove(slot);
        emitEvent(UpdateInventorySlotEvent.remove(this, slot));
        weapon().ifPresent(equippedWeapon -> {
            inventory.put(slot, equippedWeapon);
            emitEvent(new UpdateInventorySlotEvent(this, slot, equippedWeapon));
            log.debug("Put equipped weapon {} back to inventory.", equippedWeapon.name());
        });
        equippedEquipments.put(EquipmentType.WEAPON, weaponToEquip);
        emitEvent(new PlayerEquipEvent(this, weaponToEquip.name()));
        log.debug("Equipped weapon {}.", weaponToEquip.name());
    }

    private void equip(int slotId, AbstractEquipment equipmentInSlot) {
        if (equipmentInSlot.equipmentType() == EquipmentType.WEAPON) {
            Weapon weaponInSlot = (Weapon) equipmentInSlot;
            equipWeaponFromSlot(slotId, weaponInSlot);
            if (attackKungFu.getType() != weaponInSlot.kungFuType()) {
                changeAttackKungFu(kungFuBook.findUnnamedAttack(weaponInSlot.kungFuType()));
            }
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
        return INNATE_ATTACKSPEED + attackKungFu.getAttackSpeed();
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

    public int recovery() {
        int kfr = attackKungFu != null ? attackKungFu.getRecovery() : 0;
        return 50 + kfr;
    }

    @Override
    public KungFuBook kungFuBook() {
        return kungFuBook;
    }

    @Override
    public Optional<ProtectKungFu> protectKungFu() {
        return Optional.ofNullable(protectKungFu);
    }

    @Override
    public Optional<BreathKungFu> breathKungFu() {
        return Optional.ofNullable(breathKungFu);
    }

    @Override
    public Optional<AssistantKungFu> assistantKungFu() {
        return Optional.ofNullable(assistantKungFu);
    }

    @Override
    public Inventory inventory() {
        return inventory;
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

    @Override
    public void onEvent(EntityEvent entityEvent) {
        if (!entityEvent.source().equals(getFightingEntity())) {
            return;
        }
        if (!canAttack(entityEvent.source())) {
            clearFightingEntity();
        }
        if (state() instanceof PlayerWaitDistanceState waitDistanceState) {
            waitDistanceState.onTargetEvent(this);
        }
    }
}
