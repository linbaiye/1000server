package org.y1000.entities.players;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.*;
import org.y1000.entities.creatures.*;
import org.y1000.entities.creatures.event.CreatureDieEvent;
import org.y1000.entities.creatures.event.CreatureHurtEvent;
import org.y1000.entities.creatures.event.EntitySoundEvent;
import org.y1000.entities.players.event.*;
import org.y1000.entities.players.fight.PlayerAttackState;
import org.y1000.entities.players.fight.PlayerCooldownState;
import org.y1000.entities.players.fight.PlayerWaitDistanceState;
import org.y1000.entities.projectile.Projectile;
import org.y1000.event.EntityEvent;
import org.y1000.event.EntityEventListener;
import org.y1000.exp.ExperienceUtil;
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
import org.y1000.util.UnaryAction;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public final class PlayerImpl extends AbstractCreature<PlayerImpl, PlayerState> implements Player,
        EntityEventListener {

    public static final int DEFAULT_REGENERATE_SECONDS = 9;
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

    private PlayerRevival revival;

    private int regenerateTimer;

    private YinYang yinYang;

    private final PlayerAgedAttribute innerPower;

    private final PlayerAgedAttribute power;

    private final PlayerAgedAttribute outerPower;
    private final PlayerLife life;
    private final PlayerLife headLife;
    private final PlayerLife armLife;
    private final PlayerLife legLife;

    private final PlayerInnateAttributesProvider innateAttributesProvider;

    private int recoveryCooldown;

    private int attackCooldown;

    @Getter
    private AttackableActiveEntity fightingEntity;

    private final PillSlots pillSlots;

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
                      ArmorEquipment hat,
                      ArmorEquipment chest,
                      Hair hair,
                      ArmorEquipment wrist,
                      ArmorEquipment boot,
                      Trouser trouser,
                      Clothing clothing,
                      FootKungFu footKungfu,
                      ProtectKungFu protectKungFu,
                      BreathKungFu breathKungFu,
                      PlayerInnateAttributesProvider innateAttributesProvider,
                      PlayerLife life,
                      PlayerLife head,
                      PlayerLife arm,
                      PlayerLife leg,
                      PlayerAgedAttribute power,
                      PlayerAgedAttribute innerPower,
                      PlayerAgedAttribute outerPower,
                      int revival,
                      YinYang yinYang,
                      PillSlots pillSlots) {
        super(id, coordinate, Direction.DOWN, name, STATE_MILLIS);
        Objects.requireNonNull(kungFuBook, "kungFuBook can't be null.");
        Objects.requireNonNull(attackKungFu, "attackKungFu can't be null.");
        Objects.requireNonNull(inventory, "inventory can't be null.");
        initProtectKungFu(protectKungFu);
        initBreathKungFu(breathKungFu);
        this.inventory = inventory;
        this.attackKungFu = attackKungFu;
        this.kungFuBook = kungFuBook;
        this.male = male;
        this.footKungfu = footKungfu;
        this.power = power;
        this.innerPower = innerPower;
        this.outerPower = outerPower;
        this.yinYang = yinYang != null ? yinYang : new YinYang();
        this.equippedEquipments = new HashMap<>();
        equippedEquipments.put(EquipmentType.HAT, hat);
        equippedEquipments.put(EquipmentType.WEAPON, weapon);
        equippedEquipments.put(EquipmentType.BOOT, boot);
        equippedEquipments.put(EquipmentType.CHEST, chest);
        equippedEquipments.put(EquipmentType.CLOTHING, clothing);
        equippedEquipments.put(EquipmentType.WRIST, wrist);
        equippedEquipments.put(EquipmentType.TROUSER, trouser);
        equippedEquipments.put(EquipmentType.HAIR, hair);
        this.innateAttributesProvider = innateAttributesProvider;
        this.revival = new PlayerRevival(revival);
        this.life = life;
        this.armLife = arm;
        this.legLife = leg;
        this.headLife = head;
        this.pillSlots = pillSlots;
        this.changeState(new PlayerStillState(getStateMillis(State.IDLE)));
        setRegenerateTimer();
    }

    private void setRegenerateTimer() {
        regenerateTimer = DEFAULT_REGENERATE_SECONDS * 1000;
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


    @Override
    public Optional<FootKungFu> footKungFu() {
        return Optional.ofNullable(footKungfu);
    }


    @Override
    public AttackKungFu attackKungFu() {
        return attackKungFu;
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

    private void disableAssistantKungFuNoTip() {
        if (assistantKungFu != null) {
            emitEvent(PlayerToggleKungFuEvent.disableNoTip(this, assistantKungFu));
        }
        assistantKungFu = null;
    }

    private void disableProtectionNoTip() {
        if (protectKungFu != null) {
            emitEvent(PlayerToggleKungFuEvent.disableNoTip(this, protectKungFu));
            emitEvent(new EntitySoundEvent(this, protectKungFu.disableSound()));
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
        equipped.eventSound().ifPresent(s -> emitEvent(new EntitySoundEvent(this, s)));
    }


    private void learnKungFu(int inventorySlotId, KungFuItem kungFuItem) {
        KungFu kungFu = kungFuItem.kungFu().duplicate();
        var slot = kungFuBook().addToBasic(kungFu);
        if (slot == 0) {
            return;
        }
        emitEvent(new PlayerLearnKungFuEvent(this, slot, kungFu));
        inventory().decrease(inventorySlotId);
        emitEvent(new UpdateInventorySlotEvent(this, inventorySlotId, inventory().getItem(inventorySlotId)));
        kungFuItem.eventSound().ifPresent(s -> emitEvent(new EntitySoundEvent(this, s)));
    }


    private void handleInventorySlotDoubleClick(int slotId) {
        Item item = inventory.getItem(slotId);
        if (item == null) {
            return;
        }
        if (item instanceof Equipment equipment) {
            equip(slotId, equipment);
        } else if (item instanceof StackItem stackItem) {
            if (stackItem.item() instanceof Pill pill) {
                if (inventory.decrease(slotId)) {
                    emitEvent(new UpdateInventorySlotEvent(this, slotId, item));
                    pillSlots.usePill(this, pill);
                }
            } else if (stackItem.item() instanceof KungFuItem kungFuItem) {
                learnKungFu(slotId, kungFuItem);
            }
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
            emitEvent(new EntitySoundEvent(this, protectKungFu.disableSound()));
            protectKungFu = null;
            return;
        }
        protectKungFu = newProtection;
        protectKungFu.resetTimer();
        emitEvent(PlayerToggleKungFuEvent.enable(this, protectKungFu));
        emitEvent(new EntitySoundEvent(this, protectKungFu.enableSound()));
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
                emitEvent(new EntitySoundEvent(this, this.protectKungFu.disableSound()));
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
                emitEvent(PlayerToggleKungFuEvent.disable(this, this.footKungfu));
                this.footKungfu = null;
            } else {
                this.footKungfu = newKungFu;
                emitEvent(PlayerToggleKungFuEvent.enable(this, this.footKungfu));
            }
            return;
        }
        if (stateEnum() == State.SIT) {
            this.changeState(new PlayerStandUpState(this));
            emitEvent(new PlayerStandUpEvent(this, true));
        } else if (stateEnum() != State.WALK && stateEnum() != State.ENFIGHT_WALK) {
            this.changeState(PlayerStillState.idle(this));
            emitEvent(new SetPositionEvent(this, direction(), coordinate()));
        }
        disableBreathKungNoTip();
        this.footKungfu = newKungFu;
        emitEvent(new PlayerToggleKungFuEvent(this, this.footKungfu));
    }

    private void toggleAssistantKungFu(AssistantKungFu newAssistant) {
        if (attackKungFu.level() < 9999) {
            emitEvent(PlayerTextEvent.kungFuLevelLow(this));
            return;
        }
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
        this.changeState(PlayerSitDownState.sit(this));
        emitEvent(new PlayerSitDownEvent(this, includeSelf));
    }

    private void standUp(boolean includeSelf) {
        if (state().canStandUp()) {
            disableBreathKungNoTip();
            this.changeState(new PlayerStandUpState(this));
            emitEvent(new PlayerStandUpEvent(this, includeSelf));
        }
    }

    public void attack(ClientAttackEvent event, AttackableActiveEntity target) {
        Validate.notNull(event, "event can't be null.");
        Validate.notNull(target, "target can't be null.");
        attackKungFu.startAttack(this, event, target);
    }

    private void move(ClientMovementEvent event) {
        if (state() instanceof MovableState movableState) {
            movableState.move(this, event);
        } else if (state() instanceof AbstractPlayerMoveState moveState) {
            moveState.onMoveEvent(event);
        }
    }

    private void handleRightClick(ClientRightClickEvent event) {
        if (event.type() == RightClickType.INVENTORY) {
            Item item = inventory.getItem(event.slotId());
            if (item != null) {
                emitEvent(new ItemOrKungFuAttributeEvent(this, event.page(), event.slotId(), item.description(), event.type()));
            }
        } else if (event.type() == RightClickType.KUNGFU) {
            Optional<KungFu> kungFu = kungFuBook.getKungFu(event.page(), event.slotId());
            kungFu.ifPresent(k -> emitEvent(new ItemOrKungFuAttributeEvent(this, event.page(), event.slotId(), k.description(), event.type())));
        } else if (event.type() == RightClickType.CHARACTER) {
            emitEvent(new PlayerRightClickAttributeEvent(this));
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
        } else if (clientEvent instanceof ClientUnequipEvent unequipEvent) {
            unequip(unequipEvent.type());
        } else if (clientEvent instanceof ClientToggleKungFuEvent useKungFuEvent) {
            kungFuBook().getKungFu(useKungFuEvent.tab(), useKungFuEvent.slot()).ifPresent(this::useKungFu);
        } else if (clientEvent instanceof ClientSitDownEvent) {
            sitDown(false);
        } else if (clientEvent instanceof ClientStandUpEvent) {
            standUp(false);
        } else if (clientEvent instanceof ClientMovementEvent movementEvent) {
            move(movementEvent);
        } else if (clientEvent instanceof ClientRightClickEvent event) {
            handleRightClick(event);
        }
    }

    private <T extends Equipment> Optional<T> getEquipment(EquipmentType type, Class<T> clazz) {
        Equipment equipment = equippedEquipments.get(type);
        return equipment != null && clazz.isAssignableFrom(equipment.getClass()) ?
                Optional.of(clazz.cast(equipment)) : Optional.empty();
    }

    @Override
    public Optional<ArmorEquipment> hat() {
        return getEquipment(EquipmentType.HAT, ArmorEquipment.class);
    }

    @Override
    public Optional<ArmorEquipment> chest() {
        return getEquipment(EquipmentType.CHEST, ArmorEquipment.class);
    }

    @Override
    public Optional<Hair> hair() {
        return getEquipment(EquipmentType.HAIR, Hair.class);
    }

    @Override
    public Optional<ArmorEquipment> wrist() {
        return getEquipment(EquipmentType.WRIST, ArmorEquipment.class);
    }

    @Override
    public Optional<ArmorEquipment> boot() {
        return getEquipment(EquipmentType.BOOT, ArmorEquipment.class);
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
        if (realm == null) {
            return;
        }
        changeDirection(Direction.DOWN);
        changeState(PlayerStillState.idle(this));
        doJoinRealm(realm, null);
        emitEvent(new JoinedRealmEvent(this, coordinate(), inventory, realm));
    }

    private void doJoinRealm(Realm realm, Coordinate coordinate) {
        if (this.realm != null) {
            leaveRealm();
        }
        this.realm = realm;
        if (coordinate != null) {
            changeCoordinate(coordinate);
        } else {
            realm.map().occupy(this);
        }
    }

    @Override
    public void teleport(Realm realm, Coordinate coordinate) {
        if (realm != null && coordinate != null) {
            doJoinRealm(realm, coordinate);
            emitEvent(new PlayerTeleportEvent(this, realm, coordinate));
        }
    }

    private void onKilled() {
        disableFootKungFuNoTip();
        disableBreathKungNoTip();
        var oldLevel = revival.level();
        revival = revival.gainExp();
        if (oldLevel != revival.level()) {
            emitEvent(PlayerGainExpEvent.nonKungFu(this, "再生"));
        }
        this.changeState(PlayerDeadState.die(this));
        emitEvent(new CreatureDieEvent(this));
    }

    private void gainProtectionExp(int bodyDamage) {
        if (protectKungFu == null) {
            return;
        }
        var exp = ExperienceUtil.DEFAULT_EXP - damagedLifeToExp(bodyDamage);
        if (protectKungFu.gainExp(exp)) {
            emitEvent(new PlayerGainExpEvent(this, protectKungFu.name(), protectKungFu.level()));
        }
    }


    private void afterTakingDamage(int damagedLife) {
        if (currentLife() > 0) {
            cooldownRecovery();
            state().moveToHurtCoordinate(this);
            State afterHurtState = state().decideAfterHurtState();
            this.changeState(PlayerHurtState.hurt(this, afterHurtState));
            emitEvent(new CreatureHurtEvent(this, afterHurtState));
            gainProtectionExp(damagedLife);
        } else {
            onKilled();
        }
        emitEvent(new PlayerAttributeEvent(this));
    }


    @Override
    public void attackedBy(ViolentCreature attacker) {
        Validate.notNull(attacker);
        doAttacked(attacker.damage(), attacker.hit(), e -> {});
    }

    private void takeDamage(Damage damage) {
        var armor = aggregateArmor();
        var damagedLife = Math.max(damage.bodyDamage() - armor.body(), 1);
        life.consume(damagedLife);
        var damagedHead = Math.max(damage.headDamage() - armor.head(), 1);
        headLife.consume(damagedHead);
        var damagedArm = Math.max(damage.armDamage() - armor.arm(), 1);
        armLife.consume(damagedArm);
        var damagedLeg = Math.max(damage.legDamage() - armor.leg(), 1);
        legLife.consume(damagedLeg);
    }

    private boolean doAttacked(Damage damage, int hit, UnaryAction<Integer> gainExp) {
        int damagedLife = doAttackedAndGiveExp(damage, hit, this::takeDamage, gainExp);
        if (damagedLife > 0) {
            afterTakingDamage(damagedLife);
        }
        return damagedLife > 0;
    }


    @Override
    public boolean attackedBy(Player attacker) {
        return doAttacked(attacker.damage(), attacker.hit(), attacker::gainAttackExp);
    }

    @Override
    public void attackedBy(Projectile projectile) {
        if (projectile.shooter() instanceof Player player) {
            doAttacked(projectile.damage(), projectile.hit(), player::gainRangedAttackExp);
        } else {
            attackedBy(projectile.shooter());
        }
    }

    @Override
    public RealmMap realmMap() {
        return realm.map();
    }

    @Override
    public Realm getRealm() {
        return this.realm;
    }

    @Override
    public void leaveRealm() {
        if (realm != null) {
            realm.map().free(this);
        }
        realm = null;
        emitEvent(new PlayerLeftEvent(this));
        clearFightingEntity();
    }

    private void changeAttackKungFu(AttackKungFu newKungFu) {
        if (newKungFu.level() < 9999) {
            disableAssistantKungFuNoTip();
        }
        boolean needCooldownState = newKungFu.getType() != this.attackKungFu.getType();
        this.attackKungFu = newKungFu;
        cooldownAttack();
        if (needCooldownState && state() instanceof PlayerAttackState) {
            this.changeState(new PlayerCooldownState(cooldown()));
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
        weaponToEquip.eventSound().ifPresent(s -> emitEvent(new EntitySoundEvent(this, s)));
        log.debug("Equipped weapon {}.", weaponToEquip.name());
    }

    @Override
    public boolean canBeAttackedNow() {
        return realm != null && super.canBeAttackedNow();
    }

    private void equip(int slotId, Equipment equipmentInSlot) {
        if (equipmentInSlot.equipmentType() == EquipmentType.WEAPON) {
            Weapon weaponInSlot = (Weapon) equipmentInSlot;
            equipWeaponFromSlot(slotId, weaponInSlot);
            if (attackKungFu.getType() != weaponInSlot.kungFuType()) {
                changeAttackKungFu(kungFuBook.findUnnamedAttack(weaponInSlot.kungFuType()));
            }
        } else {
            if (equipmentInSlot instanceof SexualEquipment sexualEquipment &&
                    sexualEquipment.isMale() != isMale()) {
                return;
            }
            inventory.remove(slotId);
            Equipment currentEquipped = equippedEquipments.put(equipmentInSlot.equipmentType(), equipmentInSlot);
            emitEvent(new PlayerEquipEvent(this, equipmentInSlot.name()));
            if (currentEquipped != null) {
                inventory.put(slotId, currentEquipped);
            }
            emitEvent(new UpdateInventorySlotEvent(this, slotId, currentEquipped));
            equipmentInSlot.eventSound().ifPresent(s -> emitEvent(new EntitySoundEvent(this, s)));
        }
    }


    @Override
    public int attackSpeed() {
        var spd = weapon().map(Weapon::attackSpeed).orElse(0) +
                innateAttributesProvider.attackSpeed() + attackKungFu.attackSpeed();
        var p = legPercent();
        return p >= 50 ? spd : spd + spd * (50 - p )/ 50;
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
        setRegenerateTimer();
        var newYY = yinYang.accumulate(DEFAULT_REGENERATE_SECONDS);
        if (newYY.hasHigherLevel(yinYang)) {
            emitEvent(PlayerGainExpEvent.nonKungFu(this, yinYang.isYin() ? "阴气" : "阳气"));
        }
        int newAge = newYY.age();
        if (newAge != yinYang.age()) {
            life.onAgeIncreased(newAge);
            power.onAgeIncreased(newAge);
            innerPower.onAgeIncreased(newAge);
            outerPower.onAgeIncreased(newAge);
            armLife.onAgeIncreased(newAge);
            headLife.onAgeIncreased(newAge);
            legLife.onAgeIncreased(newAge);
        }
        yinYang = newYY;
        int halLife =revival.regenerateHalLife(stateEnum());
        armLife.gain(halLife);
        headLife.gain(halLife);
        legLife.gain(halLife);
        var resource = revival.regenerateResources(stateEnum());
        life.gain(resource);
        gainOuterPower(resource);
        gainInnerPower(resource);
        gainPower(resource / 2);
        emitEvent(new PlayerAttributeEvent(this));
    }

    private void doGainExperiencedResource(PlayerAgedAttribute attribute, String name, int v) {
        int old = attribute.maxValue();
        attribute.gain(v);
        if (attribute.maxValue() != old) {
            emitEvent(PlayerGainExpEvent.nonKungFu(this, name));
        }
    }

    public void gainPower(int v) {
        doGainExperiencedResource(power, "武功", v);
    }

    public void gainInnerPower(int v) {
        doGainExperiencedResource(innerPower, "内功", v);
    }

    public void gainOuterPower(int v) {
        doGainExperiencedResource(outerPower, "外功", v);
    }

    public void gainLife(int v) {
        life.gain(v);
    }

    @Override
    public void gainHeadLife(int v) {
        headLife.gain(v);
    }

    @Override
    public void gainArmLife(int v) {
        armLife.gain(v);
    }

    @Override
    public void gainLegLife(int v) {
        legLife.gain(v);
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
        pillSlots.update(this, delta);
        state().update(this, delta);
    }

    public int recovery() {
        int r = attackKungFu.recovery() + innateAttributesProvider.recovery() -
                weapon().map(Weapon::recovery).orElse(0);
        for (Equipment equipment : equippedEquipments.values()) {
            if (equipment instanceof ArmorEquipment armorEquipment) {
                r -= armorEquipment.recovery();
            }
        }
        return r;
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
        return yinYang.age();
    }

    @Override
    public int power() {
        return power.currentValue();
    }

    @Override
    public int maxPower() {
        return power.maxValue();
    }

    @Override
    public int innerPower() {
        return innerPower.currentValue();
    }

    @Override
    public int maxInnerPower() {
        return innerPower.maxValue();
    }

    @Override
    public int outerPower() {
        return outerPower.currentValue();
    }

    @Override
    public int maxOuterPower() {
        return outerPower.maxValue();
    }

    @Override
    public int maxEnergy() {
        return 0;
    }

    @Override
    public int energy() {
        return 0;
    }


    @Override
    public void consumePower(int amount) {
        power.consume(amount);
    }

    @Override
    public void consumeInnerPower(int amount) {
        innerPower.consume(amount);
    }

    @Override
    public void consumeOuterPower(int amount) {
        outerPower.consume(amount);
    }

    @Override
    public void consumeLife(int amount) {
        if (amount <= 0) {
            return;
        }
        life.consume(amount);
        if (life.currentValue() == 0) {
            onKilled();
        }
    }


    private void doGainExp(int amount, KungFu kungFu) {
        if (amount <= 0) {
            return;
        }
        if (armLife.percent() < 50) {
            emitEvent(PlayerTextEvent.armLifeTooLowToExp(this));
            return;
        }
        if (kungFu.gainExp(amount)) {
            emitEvent(new PlayerGainExpEvent(this, kungFu.name(), kungFu.level()));
        }
    }


    @Override
    public void gainAttackExp(int amount) {
        doGainExp(amount, attackKungFu);
    }

    @Override
    public void gainRangedAttackExp(int amount) {
        if (attackKungFu.isRanged()) {
            gainAttackExp(amount);
        }
    }

    @Override
    public void gainAssistantExp(int amount) {
        assistantKungFu().ifPresent(kf -> doGainExp(amount, kf));
    }

    @Override
    public int headPercent() {
        return headLife.percent();
    }

    @Override
    public int armPercent() {
        return armLife.percent();
    }

    @Override
    public int legPercent() {
        return legLife.percent();
    }

    @Override
    public int attackedByAoe(Damage damage, int hit) {
        int[] exp = new int[1];
        doAttacked(damage, hit, e -> exp[0] = e);
        return exp[0];
    }

    @Override
    public boolean consumeItem(int slotId) {
        var ret = inventory.decrease(slotId);
        if (ret)
            emitEvent(new UpdateInventorySlotEvent(this, slotId, inventory.getItem(slotId)));
        return ret;
    }

    @Override
    public Inventory inventory() {
        return inventory;
    }

    @Override
    public int hit() {
        return innateAttributesProvider.hit();
    }

    @Override
    public Damage damage() {
        var dmg = weapon().map(Weapon::damage).orElse(Damage.ZERO)
                .add(innateAttributesProvider.damage())
                .add(attackKungFu().damage());
        int percent = armLife.percent();
        if (percent >= 50)
            return dmg;
        return dmg.multiply((float) percent / 50);
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
    public void onEvent(EntityEvent entityEvent) {
        if (!entityEvent.source().equals(getFightingEntity())) {
            return;
        }
        if (!canChaseOrAttack(entityEvent.source())) {
            clearFightingEntity();
        }
        if (state() instanceof PlayerWaitDistanceState waitDistanceState) {
            waitDistanceState.onTargetEvent(this);
        }
    }

    @Override
    public int avoidance() {
        int av = attackKungFu.avoidance() + innateAttributesProvider.avoidance() +
                weapon().map(Weapon::avoidance).orElse(0);
        for (Equipment equipment : equippedEquipments.values()) {
            if (equipment instanceof ArmorEquipment armorEquipment) {
                av += armorEquipment.avoidance();
            }
        }
        return av;
    }

    @Override
    public int maxLife() {
        return life.maxValue();
    }

    @Override
    public int currentLife() {
        return life.currentValue();
    }


    private Armor aggregateArmor() {
        var armor = protectKungFu().map(ProtectKungFu::armor)
                .orElse(Armor.Empty)
                .add(attackKungFu.armor());
        for (var e : equippedEquipments.values()) {
            if (e instanceof ArmorEquipment armorEquipment) {
                armor = armor.add(armorEquipment.armor());
            }
        }
        return armor;
    }

    @Override
    public int bodyArmor() {
        return armor().body();
    }

    @Override
    public Armor armor() {
        return aggregateArmor();
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


    @Override
    public int recoveryCooldown() {
        return recoveryCooldown;
    }

    @Override
    public int attackCooldown() {
        return attackCooldown;
    }

    private void cooldown(int delta) {
        recoveryCooldown = recoveryCooldown > delta ? recoveryCooldown - delta : 0;
        attackCooldown = attackCooldown > delta ? attackCooldown - delta : 0;
    }

    public void cooldownRecovery() {
        recoveryCooldown = recovery() * Realm.STEP_MILLIS;
    }

    public void cooldownAttack() {
        attackCooldown = attackSpeed() * Realm.STEP_MILLIS;
    }

    public void setFightingEntity(AttackableActiveEntity entity){
        Objects.requireNonNull(entity, "entity can't be null");
        if (this.fightingEntity != null) {
            this.fightingEntity.deregisterEventListener(this);
        }
        this.fightingEntity = entity;
        this.fightingEntity.registerEventListener(this);
    }


    private void clearFightingEntity() {
        if (this.fightingEntity != null) {
            this.fightingEntity.deregisterEventListener(this);
            this.fightingEntity = null;
        }
    }
}
