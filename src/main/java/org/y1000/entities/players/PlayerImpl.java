package org.y1000.entities.players;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.*;
import org.y1000.entities.attribute.Damage;
import org.y1000.entities.creatures.*;
import org.y1000.entities.creatures.event.CreatureDieEvent;
import org.y1000.entities.creatures.event.CreatureHurtEvent;
import org.y1000.entities.creatures.event.CreatureSoundEvent;
import org.y1000.entities.players.event.*;
import org.y1000.entities.players.fight.PlayerAttackState;
import org.y1000.entities.players.fight.PlayerCooldownState;
import org.y1000.entities.players.fight.PlayerWaitDistanceState;
import org.y1000.exp.Experience;
import org.y1000.item.*;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.kungfu.*;
import org.y1000.kungfu.attack.AttackKungFu;
import org.y1000.kungfu.attack.AttackKungFuType;
import org.y1000.kungfu.breath.BreathKungFu;
import org.y1000.kungfu.protect.ProtectKungFu;
import org.y1000.message.clientevent.*;
import org.y1000.message.serverevent.*;
import org.y1000.message.*;
import org.y1000.realm.Realm;
import org.y1000.realm.RealmMap;
import org.y1000.util.Action;
import org.y1000.util.Coordinate;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

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
    public static final int INNATE_RECOVERY = 50;
    public static final int INNATE_AVOIDANCE = 25;

    private int currentLife;

    private int age;

    private int innerPower;

    private int outerPower;

    private int power;

    private int maxPower;

    private int maxInnerPower;

    private int maxOuterPower;

    private int energy;

    private int maxEnergy;

    private int currentLeg;
    private int currentHead;

    private int currentArm;

    private final int innateAvoidance;

    private final int innateAttackSpeed;

    private final int innateRecovery;

    private final int innateLife;

    private final int innateHit;

    private int revival;

    private final Damage innateDamage;

    private int regenerateTimer;

    private static class PlayerTimer {
        private int seconds;
    }


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
                      BreathKungFu breathKungFu,
                      int innateAvoidance,
                      int innateAttackSpeed,
                      int innateRecovery,
                      Damage innateDamage,
                      int innateLife,
                      int power,
                      int maxPower,
                      int innerPower,
                      int outerPower,
                      int maxInnerPower,
                      int maxOuterPower,
                      int age,
                      int innateHit,
                      int energy,
                      int maxEnergy,
                      int revival) {
        super(id, coordinate, Direction.DOWN, name, STATE_MILLIS);
        Objects.requireNonNull(kungFuBook, "kungFuBook can't be null.");
        Objects.requireNonNull(attackKungFu, "attackKungFu can't be null.");
        Objects.requireNonNull(inventory, "inventory can't be null.");
        this.inventory = inventory;
        this.attackKungFu = attackKungFu;
        this.kungFuBook = kungFuBook;
        this.male = male;
        this.footKungfu = footKungfu;
        this.innateAvoidance = innateAvoidance;
        this.innateRecovery = innateRecovery;
        this.innateAttackSpeed = innateAttackSpeed;
        this.power = power;
        this.maxPower = maxPower == 0 ? power : maxPower;
        this.innerPower = innerPower;
        this.maxInnerPower = maxInnerPower == 0 ? innerPower : maxInnerPower;
        this.outerPower = outerPower;
        this.maxOuterPower = maxOuterPower == 0 ? outerPower : maxOuterPower;
        this.innateLife = innateLife;
        this.age = age;
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
        currentLife = this.innateLife + this.age;
        this.innateDamage = innateDamage;
        this.innateHit = innateHit == 0 ? 75 : innateHit;
        this.energy = energy;
        this.maxEnergy = maxEnergy != 0 ? maxEnergy : energy;
        this.revival = revival != 0 ? revival : 100;
        currentArm = currentLife();
        currentLeg = currentLife();
        currentHead = currentLife();
        changeState(new PlayerStillState(getStateMillis(State.IDLE)));
    }

    private void resetTimer() {
        regenerateTimer = 9 * 1000;
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

    public void disableFootKungFuNoTip() {
        if (footKungfu != null) {
            emitEvent(PlayerToggleKungFuEvent.disableNoTip(this, footKungfu));
        }
        footKungfu = null;
    }

    public void disableBreathKungNoTip() {
        if (breathKungFu != null) {
            emitEvent(PlayerToggleKungFuEvent.disableNoTip(this, breathKungFu));
        }
        breathKungFu = null;
    }

    private void disableProtectionNoTip() {
        if (protectKungFu != null) {
            emitEvent(PlayerToggleKungFuEvent.disableNoTip(this, protectKungFu));
            emitEvent(new CreatureSoundEvent(this, protectKungFu.disableSound()));
            this.protectKungFu = null;
        }
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
            changeAttackKungFu(newAttack);
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
        disableBreathKungNoTip();
        if (protectKungFu != null && protectKungFu.name().equals(newProtection.name())) {
            emitEvent(PlayerToggleKungFuEvent.disable(this, protectKungFu));
            emitEvent(new CreatureSoundEvent(this, protectKungFu.disableSound()));
            protectKungFu = null;
            return;
        }
        protectKungFu = newProtection;
        protectKungFu.resetTimer();
        emitEvent(PlayerToggleKungFuEvent.enable(this, protectKungFu));
        emitEvent(new CreatureSoundEvent(this, protectKungFu.enableSound()));
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
                emitEvent(PlayerToggleKungFuEvent.disableNoTip(this, protectKungFu));
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
        disableBreathKungNoTip();
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
            emitEvent(PlayerToggleKungFuEvent.disableNoTip(this, footKungfu));
            footKungfu = null;
        }
        clearFightingEntity();
        changeState(PlayerSitDownState.sit(this));
        emitEvent(new PlayerSitDownEvent(this, includeSelf));
    }

    private void standUp(boolean includeSelf) {
        if (state().canStandUp()) {
            disableBreathKungNoTip();
            changeState(new PlayerStandUpState(this));
            emitEvent(new PlayerStandUpEvent(this, includeSelf));
        }
    }

    private void startAttack(ClientAttackEvent event) {
        realm.findInsight(this, event.entityId())
                .ifPresent(target -> attackKungFu.startAttack(this,event, target));
    }

    private void move(ClientMovementEvent event) {
        if (state() instanceof MovableState movableState) {
            movableState.move(this, event);
        } else if (state() instanceof AbstractPlayerMoveState moveState) {
            moveState.onMoveEvent(event);
        }
    }

    @Override
    public void handleClientEvent(ClientEvent clientEvent) {
        if (stateEnum() == State.DIE) {
            return;
        }
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
            standUp(false);
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
        //log().debug("Change state from {} to {}.", state(), newState);
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


    private void onKilled() {
        disableFootKungFuNoTip();
        changeState(PlayerDeadState.die(this));
        emitEvent(new CreatureDieEvent(this));
    }

    private void gainProtectionExp(int bodyDamage) {
        if (protectKungFu == null) {
            return;
        }
        var exp = Experience.DEFAULT_EXP - damagedLifeToExp(bodyDamage);
        if (protectKungFu.gainExp(exp)) {
            emitEvent(new PlayerGainExpEvent(this, protectKungFu.name(), protectKungFu.level()));
        }
    }

    @Override
    public boolean attackedBy(ViolentCreature attacker) {
        if (!state().attackable() || randomAvoidance(attacker.hit())) {
            return false;
        }
        cooldownRecovery();
        int bodyDamage = attacker.damage().bodyDamage() - bodyArmor();
        bodyDamage = bodyDamage > 0 ? bodyDamage : 1;
        currentLife = Math.max(currentLife - bodyDamage, 0);
        if (currentLife() > 0) {
            state().moveToHurtCoordinate(this);
            State afterHurtState = state().decideAfterHurtState();
            changeState(PlayerHurtState.hurt(this, afterHurtState));
            emitEvent(new CreatureHurtEvent(this, afterHurtState));
            gainProtectionExp(bodyDamage);
        } else {
            onKilled();
        }
        return true;
    }

    @Override
    public void attackedBy(Projectile projectile) {
        attackedBy(projectile.getShooter());
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
        boolean needCooldownState = newKungFu.getType() != this.attackKungFu.getType();
        this.attackKungFu = newKungFu;
        cooldownAttack();
        if (needCooldownState && state() instanceof PlayerAttackState) {
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
        return innateAttackSpeed + attackKungFu.attackSpeed();
    }

    public Optional<Weapon> weapon() {
        return getEquipment(EquipmentType.WEAPON, Weapon.class);
    }

    @Override
    public PlayerInterpolation captureInterpolation() {
        return PlayerInterpolation.FromPlayer(this, state().elapsedMillis());
    }


    private void regenerate(int delta) {
        regenerateTimer -= delta;
        if (regenerateTimer > 0) {
            return;
        }
        resetTimer();
        /*if (stateEnum() == State.DIE) {
            regenRate = 300;
        } else if (stateEnum() == State.SIT) {
            regenRate = 150;
        }
        case FFeatureState of
        wfs_normal   : n := 80;
        wfs_care     : n := 10;
        wfs_sitdown  : n := 150;
        wfs_die      : n := 300;
         else n :=50;
        end;
        n := n + n * AttribData.crevival div 10000;*/
        var regen = state().regenerate();
        int regenValue = regen + (regen * revival) / 10000;
        gainLife(regenValue);
        gainPower(regenValue);
        gainOuterPower(regenValue);
        gainInnerPower(regenValue);
        emitEvent(new PlayerAttributeEvent(this));
    }

    public void gainPower(int v) {
        if (v > 0) {
            power = Math.min(maxPower, power + v);
        }
    }

    public void gainInnerPower(int v) {
        if (v > 0) {
            innerPower = Math.min(maxInnerPower, innerPower + v);
        }
    }

    public void gainOuterPower(int v) {
        if (v > 0) {
            outerPower = Math.min(maxOuterPower, outerPower + v);
        }
    }

    public void gainLife(int v) {
        if (v > 0) {
            currentLife = Math.min(maxLife(), currentLife + v);
        }
    }


    private boolean updateKungFuAndCheck(PeriodicalKungFu kungFu,
                                         int delta,
                                         Action disableAction) {
        if (kungFu == null) {
            return false;
        }
        var ret = kungFu.updateResources(this, delta);
        if (ret && !kungFu.canKeep(this)) {
            disableAction.invoke();
        }
        return ret;
    }

    private void updateKungFu(int delta) {
        var resourceUpdated = updateKungFuAndCheck(protectKungFu, delta, this::disableProtectionNoTip);
        resourceUpdated = resourceUpdated || updateKungFuAndCheck(footKungfu, delta, this::disableFootKungFuNoTip);
        if (resourceUpdated) {
            emitEvent(new PlayerAttributeEvent(this));
            return;
        }
        if (breathKungFu == null) {
            return;
        }
        breathKungFu.update(this, delta, this::emitEvent);
        if (!breathKungFu.canRegenerateResources(this)) {
            standUp(true);
        }
    }

    @Override
    public void update(int delta) {
        cooldown(delta);
        regenerate(delta);
        updateKungFu(delta);
        state().update(this, delta);
    }

    public int recovery() {
        int kfr = attackKungFu != null ? attackKungFu.recovery() : 0;
        return innateRecovery + kfr;
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
    public int age() {
        return age;
    }

    @Override
    public int power() {
        return power;
    }

    @Override
    public int maxPower() {
        return maxPower;
    }

    @Override
    public int innerPower() {
        return innerPower;
    }

    @Override
    public int maxInnerPower() {
        return maxInnerPower;
    }

    @Override
    public int outerPower() {
        return outerPower;
    }

    @Override
    public int maxOuterPower() {
        return maxOuterPower;
    }

    @Override
    public int maxEnergy() {
        return maxEnergy;
    }

    @Override
    public int energy() {
        return energy;
    }

    @Override
    public int armLife() {
        return currentArm;
    }

    @Override
    public void consumePower(int amount) {
        if (amount > 0)
            power = Math.max(power - amount, 0);
    }

    @Override
    public void consumeInnerPower(int amount) {
        if (amount > 0)
            innerPower = Math.max(innerPower - amount, 0);
    }

    @Override
    public void consumeOuterPower(int amount) {
        if (amount > 0)
            outerPower = Math.max(outerPower - amount, 0);
    }

    @Override
    public void consumeLife(int amount) {
        if (amount <= 0) {
            return;
        }
        currentLife = Math.max(currentLife - amount, 0);
        if (currentLife == 0) {
            onKilled();
        }
    }


    @Override
    public void gainAttackExp(int amount) {
        if (amount <= 0) {
            return;
        }
        if (armLife() * 100 / maxLife() <= 50) {
            emitEvent(PlayerTextEvent.armLifeTooLowToExp(this));
            return;
        }
        if (attackKungFu.gainExp(amount)) {
            emitEvent(new PlayerGainExpEvent(this, attackKungFu.name(), attackKungFu.level()));
        }
    }

    @Override
    public Inventory inventory() {
        return inventory;
    }

    @Override
    public int hit() {
        return innateHit;
    }

    @Override
    public Damage damage() {
        return innateDamage.add(attackKungFu.damage());
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
            log.debug("Target not attackable.");
            clearFightingEntity();
        }
        if (state() instanceof PlayerWaitDistanceState waitDistanceState) {
            waitDistanceState.onTargetEvent(this);
        }
    }

    @Override
    public int avoidance() {
        return innateAvoidance;
    }


    @Override
    public int maxLife() {
        return innateLife + age;
    }

    @Override
    public int currentLife() {
        return currentLife;
    }

    @Override
    public int bodyArmor() {
        var am = attackKungFu.bodyArmor();
        return protectKungFu().map(k -> k.bodyArmor() + am)
                .orElse(am);
    }


    // mandieNew 2003, ManDieOld 2005, womanDieNew 2203, womanDieOld 2205.

    @Override
    public Optional<String> hurtSound() {
        if (ThreadLocalRandom.current().nextInt(100) < 40) {
            return Optional.empty();
        }
        return Optional.of(age() < 6000 ?
                (isMale() ? "2002" : "2202") :
                (isMale() ? "2004" : "2204") );
    }


    @Override
    public Optional<String> dieSound() {
        return Optional.of(age() < 6000 ?
                (isMale() ? "2003" : "2203") :
                (isMale() ? "2005" : "2205") );
    }
}
