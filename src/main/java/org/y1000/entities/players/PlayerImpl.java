package org.y1000.entities.players;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.*;
import org.y1000.entities.players.equipment.*;
import org.y1000.entities.players.event.*;
import org.y1000.entities.players.event.PlayerDropItemEvent;
import org.y1000.guild.GuildMembership;
import org.y1000.item.*;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.kungfu.*;
import org.y1000.kungfu.attack.AttackKungFu;
import org.y1000.kungfu.attack.AttackKungFuType;
import org.y1000.kungfu.breath.BreathKungFu;
import org.y1000.kungfu.protect.ProtectKungFu;
import org.y1000.input.*;
import org.y1000.network.I2ClientMessage;
import org.y1000.realm.PlayerEventListener;
import org.y1000.realm.Realm;
import org.y1000.realm.RealmMap;
import org.y1000.util.Action;
import org.y1000.util.Coordinate;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
public class PlayerImpl extends AbstractCreature implements Player, PlayerInputHandler {

    public static final int DEFAULT_REGENERATE_SECONDS = 9;
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

    private int team;

    private final PlayerExperiencedAgedAttribute innerPower;

    private final PlayerExperiencedAgedAttribute power;

    private final PlayerExperiencedAgedAttribute outerPower;
    private final PlayerLife life;
    private final PlayerLife headLife;
    private final PlayerLife armLife;
    private final PlayerLife legLife;

    private final PlayerInnateAttributesProvider innateAttributesProvider;

    private int recoveryCooldown;

    private int attackCooldown;

    private final PillSlots pillSlots;

    private GuildMembership guildMembership;

    private final BuffPillSlot buffPillSlot;

    private PlayerState state;

    private PlayerEventListener eventListener;

    private CombatController combatController;

    private PlayerTrade playerTrade;

    private final List<Rope> ropes;

    private final ThreadLocal<Realm> realm;


    @Builder
    public PlayerImpl(long id,
                      Coordinate coordinate,
                      String name,
                      Inventory inventory,
                      AttackKungFu attackKungFu,
                      KungFuBook kungFuBook,
                      boolean male,
                      Map<EquipmentType, Equipment> equipments,
                      FootKungFu footKungfu,
                      ProtectKungFu protectKungFu,
                      BreathKungFu breathKungFu,
                      PlayerInnateAttributesProvider innateAttributesProvider,
                      PlayerLife life,
                      PlayerLife head,
                      PlayerLife arm,
                      PlayerLife leg,
                      PlayerExperiencedAgedAttribute power,
                      PlayerExperiencedAgedAttribute innerPower,
                      PlayerExperiencedAgedAttribute outerPower,
                      int revival,
                      YinYang yinYang,
                      PillSlots pillSlots,
                      GuildMembership guildMembership) {
        super(id, coordinate, Direction.DOWN, name);
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
        this.equippedEquipments = equipments != null ? equipments : new HashMap<>();
        this.innateAttributesProvider = innateAttributesProvider;
        this.revival = new PlayerRevival(revival);
        this.life = life;
        this.armLife = arm;
        this.legLife = leg;
        this.headLife = head;
        this.pillSlots = pillSlots;
        setRegenerateTimer();
        team = 0;
        this.guildMembership = guildMembership;
        this.buffPillSlot = new BuffPillSlot();
        this.changeState(PlayerStandState.idle(this));
        this.ropes = new ArrayList<>();
        this.realm = new ThreadLocal<>();
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


    void disableBreathAndSync() {
        if (breathKungFu != null) {
            breathKungFu = null;
            syncActiveKungFuList();
        }
    }


    private void disableProtectionAndSync() {
        if (protectKungFu != null) {
            sendSound(protectKungFu.disableSound());
            protectKungFu = null;
            syncActiveKungFuList();
        }
    }

    public void unequip(EquipmentType type) {
        if (isDead() || isLeftRealm() || type == null || !equippedEquipments.containsKey(type)) {
            return;
        }
        if (inventory.isFull()) {
            sendText("物品栏已满。");
            return;
        }
        Equipment equipped = equippedEquipments.get(type);
        if (equipped instanceof Weapon weapon && weapon.kungFuType() != AttackKungFuType.Fist) {
            tryChangeAttackKungFu(kungFuBook.findUnnamedAttack(AttackKungFuType.Fist));
        } else {
            equipped = equippedEquipments.remove(type);
            inventory.add(equipped);
            equipped.eventSound().ifPresent(this::sendSound);
            sendEvent(PlayerUnequipEvent.of(this, equipped.equipmentType()));
            syncInventoryQuietly();
        }
    }

    @Override
    public void swapKungFu(int page, int slot1, int slot2) {
        if (kungFuBook().swapSlot(page, slot1, slot2))
            syncKungFuBookQuietly();
    }

    private boolean dropActionInvalid(int slot, Coordinate at) {
        if (isDead())
            return false;
        if (inventory.getItem(slot) == null)
            return true;
        if (at.directDistance(coordinate()) > 2) {
            sendText("距离过远。");
            return true;
        }
        return false;
    }

    @Override
    public void startDropItem(int slot, Coordinate at) {
        if (dropActionInvalid(slot, at))
            return;
        sendEvent(StartDropItemMessage.of(this, slot, inventory.getItem(slot), at));
    }

    @Override
    public void confirmDropItem(int slot, int number, Coordinate at) {
        if (isLeftRealm() || isDead())
            return;
        if (dropActionInvalid(slot, at))
            return;
        Item removed = inventory.remove(slot, number);
        if (removed == null) {
            return;
        }
        sendEvent(new PlayerDropItemEvent(this, removed, at));
        sendEvent(UpdateInventorySlotMessage.update(this, slot));
    }

    @Override
    public void changeTradeState(PlayerTradeStateInput.State state) {
        if (playerTrade == null || isDead())
            return;
        switch (state) {
            case Cancel -> playerTrade.cancel(this);
            case Confirm -> playerTrade.confirm(this);
            case Unconfirmed -> playerTrade.unconfirm(this);
        }
    }

    @Override
    public void addTradeItem(int slot, int number) {
        if (isLeftRealm() || isDead())
            return;
        if (playerTrade != null)
            playerTrade.addTradeItem(this, slot, number);
    }

    @Override
    public void usePill(String name) {
        if (isLeftRealm() || isDead())
            return;
        int slot = inventory.findFirstSlot(name);
        if (slot == 0)
           return;
        if (inventory.getStackItem(slot, Pill.class).isPresent())
            handleInventorySlotDoubleClick(slot);
    }

    @Override
    public void chat(ChatInput input) {
        if (isLeftRealm())
            return;
        input.toPlayerEvent(this).ifPresent(this::sendEvent);
    }

    @Override
    public void clickEquipment(EquipmentType type) {
        getEquipment(type, Equipment.class)
                .ifPresent(e -> sendEvent(ItemDescriptionMessage.equipWindow(this, type, e)));
    }

    private void learnAndUpdateInventory(int inventorySlotId, KungFuItem kungFuItem) {
        if (kungFuItem.kungFu() instanceof AssistantKungFu kf) {
            String ret = kf.checkPreconditions(this);
            if (ret != null) {
                sendText(ret);
                return;
            }
        }
        KungFu kungFu = kungFuItem.kungFu().duplicate();
        var slot = kungFuBook().addToBasic(kungFu);
        if (slot == 0) {
            sendText("修炼失败。");
            return;
        }
        inventory().decrease(inventorySlotId);
        kungFuItem.eventSound().ifPresent(this::sendSound);
        syncKungFuBookQuietly();
        syncInventoryQuietly();
    }


    private void syncInventoryQuietly() {
        sendEvent(UpdateInventoryMessage.quiet(this));
    }

    private void syncKungFuBookQuietly() {
        sendEvent(KungFuBookMessage.quietly(this));
    }


    private void changeAndSayAndCooldown(AttackKungFu attackKungFu) {
        this.attackKungFu = attackKungFu;
        if (!this.attackKungFu.isLevelFull())
            assistantKungFu = null;
        sendEvent(PlayerSayEvent.kungfuTip(this, attackKungFu.name()));
        cooldownAttack();
    }

    /**
     * Used internally, make sure {@code equipment} is at {@code slot}.
     * @param slotId
     * @param equipment
     * @return
     */
    boolean tryEquipFromSlot(int slotId, Equipment equipment) {
        if (equipment instanceof Weapon newWeapon) {
            equipWeaponFromSlot(slotId);
            if (newWeapon.kungFuType() != attackKungFu.getType()) {
                changeAndSayAndCooldown(kungFuBook.findUnnamedAttack(newWeapon.kungFuType()));
                syncActiveKungFuList();
            }
        }
        else if (equipment instanceof SexualEquipment sexualEquipment && sexualEquipment.isMale() == isMale()) {
            equipArmorFromSlot(slotId);
        } else {
            sendText("你无法使用该装备。");
            return false;
        }
        equipment.eventSound().ifPresent(this::sendSound);
        sendEvent(PlayerEquipEvent.create(this, equipment));
        syncInventoryQuietly();
        return true;
    }

    private int unequipAndPutToInventory(EquipmentType type) {
        Equipment removed = equippedEquipments.remove(type);
        int slot = 0;
        if (removed != null) {
            slot = inventory.add(removed);
        }
        return slot;
    }



    private void sendText(String t) {
        sendEvent(PlayerTextMessage.bottom(this,t));
    }

    private void sendLeftText(String t) {
        sendEvent(PlayerTextMessage.left(this,t));
    }

    /**
     * Return true if attack kung fu is changed.
     * @param newKungFu newKungFu to use.
     * @return
     */
    boolean tryChangeAttackKungFu(AttackKungFu newKungFu) {
        if (newKungFu.nameEquals(attackKungFu)) {
            return false;
        }
        if (headPercent() < 50) {
            sendText("头部活力不足。");
            return false;
        }
        if (newKungFu.getType() == attackKungFu.getType()) {
            changeAndSayAndCooldown(newKungFu);
            syncActiveKungFuList();
            return true;
        }
        int weaponSlot = inventory.findWeaponSlot(newKungFu.getType());
        if (weaponSlot == 0) {
            if (newKungFu.getType() != AttackKungFuType.Fist) {
                sendText("没有对应的武器。");
                return false;
            }
            if (weapon().isPresent() && inventory.isFull()) {
                sendText("物品栏已满，无法卸下武器。");
                return false;
            }
            int slot = unequipAndPutToInventory(EquipmentType.WEAPON);
            if (slot > 0) {
                inventory.getItem(slot, Weapon.class).flatMap(Item::eventSound).ifPresent(this::sendSound);
                sendEvent(PlayerUnequipEvent.of(this, EquipmentType.WEAPON));
                syncInventoryQuietly();
            }
        } else {
            Weapon weapon = (Weapon) inventory.getItem(weaponSlot);
            equipWeaponFromSlot(weaponSlot);
            weapon.eventSound().ifPresent(this::sendSound);
            sendEvent(PlayerEquipEvent.create(this, weapon));
            syncInventoryQuietly();
        }
        changeAndSayAndCooldown(newKungFu);
        syncActiveKungFuList();
        return true;
    }



    private void handleInventorySlotDoubleClick(int slotId) {
        if (isDead())
            return;
        Item item = inventory.getItem(slotId);
        if (item == null) {
            return;
        }
        if (item instanceof Equipment equipment) {
            state.equip(slotId, equipment);
        } else if (item instanceof StackItem stackItem) {
            if (stackItem.item() instanceof KungFuItem kungFuItem) {
                learnAndUpdateInventory(slotId, kungFuItem);
            } else if (stackItem.item() instanceof Pill pill) {
                if (pillSlots.canTakePill() && inventory.decrease(slotId)) {
                    if (pillSlots.tryUsePill(pill)) {
                        sendLeftText("服用了" + pill.name() + "。");
                        sendEvent(UpdateInventorySlotMessage.update(this, slotId, inventory.getItem(slotId)));
                        pill.eventSound().ifPresent(this::sendSound);
                    }
                } else {
                    sendLeftText("无法再服用。");
                }
            }
        }
    }


    void disableFootKungFuAndSync() {
        if (footKungfu != null) {
            footKungfu = null;
            syncActiveKungFuList();
        }
    }

    void stopCombat() {
        combatController = null;
    }

    void toggleBreathAndSync(BreathKungFu newBreath) {
        if (newBreath.nameEquals(breathKungFu)) {
            breathKungFu = null;
        } else {
            breathKungFu = newBreath;
            footKungfu = null;
            protectKungFu().ifPresent(k -> sendSound(k.disableSound()));
            protectKungFu = null;
            stopCombat();
        }
        sendEvent(PlayerSayEvent.kungfuTip(this, newBreath.name()));
        syncActiveKungFuList();
    }

    private void toggleProtectionKungFu(ProtectKungFu newProtection) {
        if (newProtection.nameEquals(protectKungFu)) {
            sendSound(protectKungFu.disableSound());
            protectKungFu = null;
        } else {
            breathKungFu = null;
            protectKungFu = newProtection;
            sendSound(protectKungFu.enableSound());
            protectKungFu.resetTimer();
        }
        sendEvent(PlayerSayEvent.kungfuTip(this, newProtection.name()));
        syncActiveKungFuList();
    }

    void syncActiveKungFuList() {
        sendEvent(SyncActiveKungMessage.of(this));
    }

    void toggleFootAndSync(FootKungFu newKungFu) {
        if (newKungFu.nameEquals(footKungfu)) {
            this.footKungfu = null;
        } else {
            this.footKungfu = newKungFu;
        }
        breathKungFu = null;
        syncActiveKungFuList();
        sendEvent(PlayerSayEvent.kungfuTip(this, newKungFu.name()));
    }

    boolean movable(Coordinate coordinate) {
        return realmMap() != null && realmMap().movable(coordinate);
    }

    private void toggleAssistantKungFu(AssistantKungFu newAssistant) {
        if (isDead())
            return;
        if (attackKungFu.level() < 9999) {
            sendEvent(PlayerTextMessage.bottom(this, "满级武功方可使用" + newAssistant.name() + "。"));
            return;
        }
        if (newAssistant.nameEquals(this.assistantKungFu)) {
            assistantKungFu = null;
        } else {
            assistantKungFu = newAssistant;
        }
        sendEvent(PlayerSayEvent.kungfuTip(this, newAssistant.name()));
        syncActiveKungFuList();
    }


    private void handleDoubleClickKungFu(KungFu kungFu) {
        if (isLeftRealm() || isDead())
            return;
        if (kungFu instanceof FootKungFu newKungFu) {
            state.tryToggleFootKungFu(newKungFu);
        } else if (kungFu instanceof ProtectKungFu newProtectKungFu) {
            toggleProtectionKungFu(newProtectKungFu);
        } else if (kungFu instanceof BreathKungFu newBreath) {
            state.tryToggleBreathKungFu(newBreath);
        } else if (kungFu instanceof AssistantKungFu newAssistant) {
            toggleAssistantKungFu(newAssistant);
        } else if (kungFu instanceof AttackKungFu newAttack) {
            state.tryToggleAttackKungFu(newAttack);
        }
    }



    /**
     * Try to accept a combat, and PlayerState should change state accordingly if no strike happened.
     * @param entity the target to combat.
     * @return -1 if not acceptable, 1 if a strike is carried, 0 if accepted but no strike happened.
     */
    int tryAcceptAttack(ActiveEntity entity) {
        combatController = CombatController.acceptIfAllowed(this, entity);
        if (combatController == null)
            return -1;
        footKungfu = null;
        breathKungFu = null;
        syncActiveKungFuList();
        int ret =  combatController.update(0);
        if (ret == -1)
            combatController = null;
        return ret;
    }


    @Override
    public void attack(org.y1000.entities.ActiveEntity target) {
        if (!isLeftRealm() && target != null)
            state.attack(target);
    }


    @Override
    public void handleInput(SelfHandleInput input) {
        if (input == null || isLeftRealm())
            return;
        if (isDead() && !(input instanceof SimpleInput))
            return;
        input.accept(this);
    }

    @Override
    public void move(MoveInput moveInput) {
        state.tryMove(moveInput);
    }

    @Override
    public void turn(TurnInput turnInput) {
        state.turn(turnInput);
    }


    @Override
    public void handleSimpleInput(SimpleInput.Type type) {
        switch (type) {
            case KungFuBook -> sendEvent(KungFuBookMessage.forceful(this));
            case Inventory -> sendEvent(UpdateInventoryMessage.forceful(this));
            case KeyF4 -> state.sayHello();
            case KeyF3 -> state.sitOrStandUp();
            case KeyF2 -> state.switchStand();
            case KungFuBookQuietly -> sendEvent(KungFuBookMessage.quietly(this));
            case InventoryQuietly -> sendEvent(UpdateInventoryMessage.quiet(this));
            case GetPills -> sendEvent(PillsMessage.of(this));
            case AttributeEquipment -> sendEvent(AttributeEquipmentMessage.of(this));
            case AttributeQuietly -> sendEvent(AttributeEquipmentMessage.quietly(this));
        }
    }

    @Override
    public boolean canBeSeenAt(Coordinate another) {
        return realm.get() != null && super.canBeSeenAt(another);
    }

    @Override
    public void onKungFuClicked(int page, int slot, ClickKungFuInput.ClickType type) {
        if (isLeftRealm())
            return;
        kungFuBook().getKungFu(page, slot).ifPresent(kungFu -> {
            if (type == ClickKungFuInput.ClickType.LeftDoubleClick) {
                handleDoubleClickKungFu(kungFu);
            } else if (type == ClickKungFuInput.ClickType.LeftClick) {
                sendEvent(PlayerTextMessage.bottom(this, kungFu.detailText()));
            } else if (type == ClickKungFuInput.ClickType.RightClick) {
                sendEvent(ItemDescriptionMessage.kungfu(this, slot, kungFu));
            }
        });
    }


    private boolean tryDye(int from, int to) {
        var fromItem = inventory.getItem(from);
        if (fromItem == null) {
            return false;
        }
        var toItem = inventory.getItem(to);
        if (!(toItem instanceof Equipment equipment)) {
            return false;
        }
        if (fromItem instanceof StackItem stackItem && stackItem.item() instanceof Dye dye) {
            var dyable = equipment.findAbility(Dyable.class).orElse(null);
            if (dyable == null) {
                return false;
            }
            inventory.decrease(from, 1);
            dye.dye(dyable);
            sendEvent(UpdateInventorySlotMessage.update(this, from));
            sendEvent(UpdateInventorySlotMessage.update(this, to));
            return true;
        }
        return false;

    }

    @Override
    public void swapItem(int from, int to) {
        if (isLeftRealm())
            return;
        if (tryDye(from, to))
            return;
        if (inventory.move(from, to)) {
            sendEvent(UpdateInventoryMessage.forceful(this));
        }
    }


    @Override
    public void onInventorySlotClicked(int slot, ClickInventorySlotInput.ClickType type) {
        if (type == AbstractClickContainerSlotInput.ClickType.LeftDoubleClick) {
            handleInventorySlotDoubleClick(slot);
        } else if (type == AbstractClickContainerSlotInput.ClickType.RightClick) {
            var item = inventory.getItem(slot);
            if (item != null)
                sendEvent(ItemDescriptionMessage.inventory(this, slot, item));
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
    public Optional<SexualEquipment> hair() {
        return getEquipment(EquipmentType.HAIR, SexualEquipment.class);
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
    public Optional<SexualEquipment> clothing() {
        return getEquipment(EquipmentType.CLOTHING, SexualEquipment.class);
    }

    @Override
    public Optional<SexualEquipment> trouser() {
        return getEquipment(EquipmentType.TROUSER, SexualEquipment.class);
    }


    @Override
    public void joinRealm(Realm realm, PlayerEventListener messageListener) {
        joinRealm(realm, coordinate(), messageListener);
    }

    @Override
    public void sendEvent(PlayerEvent event) {
        if (eventListener != null)
            eventListener.onEvent(event);
    }

    @Override
    public void joinRealm(Realm realm, Coordinate coordinate, PlayerEventListener eventListener) {
        Validate.notNull(realm);
        Validate.notNull(coordinate);
        Validate.isTrue(this.realm.get() == null);
        this.realm.set(realm);
        changeCoordinate(coordinate);
        this.eventListener = eventListener;
    }


    private void gainProtectionExp(int bodyDamage) {
        if (protectKungFu == null) {
            return;
        }
        var exp = ExperienceUtil.DEFAULT_EXP - ExperienceUtil.damageToExp(life.maxValue(), bodyDamage);
        protectKungFu.gainExp(this, exp);
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


    @Override
    public RealmMap realmMap() {
        return getRealm() != null ? getRealm().map() : null;
    }

    @Override
    public Realm getRealm() {
        return this.realm.get();
    }

    @Override
    public void leaveRealm() {
        var r = getRealm();
        if (r != null) {
            r.map().free(this);
        }
        eventListener = null;
        realm.remove();
        if (playerTrade != null)
            playerTrade.cancel(this);
        ropes.clear();
        combatController = null;
    }

    @Override
    public boolean isLeftRealm() {
        return realm.get() == null;
    }

    private void equipWeaponFromSlot(int slot) {
        Weapon weaponToEquip = (Weapon) inventory.remove(slot);
        weapon().ifPresent(equippedWeapon -> {
            inventory.add(slot, equippedWeapon);
        });
        equippedEquipments.put(EquipmentType.WEAPON, weaponToEquip);
    }

    private void equipArmorFromSlot(int slot) {
        SexualEquipment equipment = (SexualEquipment) inventory.remove(slot);
        getEquipment(equipment.equipmentType(), SexualEquipment.class)
                .ifPresent(equipped -> inventory.add(slot, equipped));
        equippedEquipments.put(equipment.equipmentType(), equipment);
    }

    @Override
    public int attackSpeed() {
        var spd = weapon().map(Weapon::attackSpeed).orElse(0) * -1 +
                innateAttributesProvider.attackSpeed() + attackKungFu.attackSpeed();
        var p = legPercent();
        return p >= 50 ? spd : spd + spd * (50 - p )/ 50;
    }

    public Optional<Weapon> weapon() {
        return getEquipment(EquipmentType.WEAPON, Weapon.class);
    }

    @Override
    public I2ClientMessage captureSnapshot() {
        MoveAction action = state instanceof PlayerMoveState moveState ?  moveState.moveAction() : null;
        return PlayerSnapshot.build(this, state.elapsedMillis(), action);
    }


    private void regenerate(int delta) {
        regenerateTimer -= delta;
        if (regenerateTimer > 0) {
            return;
        }
        setRegenerateTimer();
        var newYY = yinYang.accumulate(DEFAULT_REGENERATE_SECONDS);
        if (newYY.hasHigherLevel(yinYang)) {
             sendEvent(PlayerGainExpMessage.nonKungFu(this, yinYang.isYin() ? "阴气" : "阳气"));
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
        sendEvent(PlayerAttributeMessage.of(this));
    }

    private void doGainExperiencedResource(AgedAttribute attribute, String name, int v) {
        int old = attribute.maxValue();
        attribute.gain(v);
        if (attribute.maxValue() != old) {
            sendEvent(PlayerGainExpMessage.nonKungFu(this, name));
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

    private boolean consumeAndDisable(PeriodicalKungFu kungFu,
                                      int delta,
                                      Action disableAction) {
        if (kungFu == null) {
            return false;
        }
        var ret = kungFu.consumeResources(this, delta);
        if (ret && !kungFu.canKeep(this)) {
            disableAction.invoke();
        }
        return ret;
    }

    private void updateKungFu(int delta) {
        boolean consumed = consumeAndDisable(protectKungFu, delta, this::disableProtectionAndSync);
        consumed = consumed|| consumeAndDisable(footKungfu, delta, this::disableFootKungFuAndSync);
        if (consumed) {
            sendEvent(PlayerAttributeMessage.of(this));
            return;
        }
        if (breathKungFu != null) {
            breathKungFu.update(this, delta);
        }
        /*if (!breathKungFu.canRegenerateResources(this)) {
            standUp(true);
        }*/
    }

    private void updateBuff(int delta) {
        boolean previousEffective = buffPillSlot.isEffective();
        buffPillSlot.update(delta);
        if (previousEffective && !buffPillSlot.isEffective()) {
            buffPillSlot.cancel();
//            emitEvent(UpdateBuffEvent.fade(this));
        }
    }

    /**
     * Update combat if there is any.
     * @param delta
     * @return Return true if changed to attack state.
     */
    boolean tryCombatStrike(int delta) {
        if (combatController == null)
            return false;
        int ret = combatController.update(delta);
        if (ret == -1)
            combatController = null;
        return ret == 1;
    }

    private void updateRopes(int delta) {
        ropes.forEach(r -> r.update(delta));
        ropes.removeIf(Rope::isBroken);
    }

    @Override
    public void update(int delta) {
        cooldown(delta);
        regenerate(delta);
        updateKungFu(delta);
        pillSlots.update(this, delta);
        updateBuff(delta);
        this.state.update(delta);
        updateRopes(delta);
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
            handleKilled();
        } else {
            sendEvent(PlayerAttributeMessage.of(this));
        }
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
    public boolean consumeItem(int slotId) {
        var ret = inventory.decrease(slotId);
        if (ret)
            sendEvent(UpdateInventorySlotMessage.update(this, slotId, inventory.getItem(slotId)));
        return ret;
    }

    @Override
    public PlayerExperiencedAgedAttribute innerPowerAttribute() {
        return innerPower;
    }

    @Override
    public PlayerExperiencedAgedAttribute outerPowerAttribute() {
        return outerPower;
    }

    @Override
    public PlayerExperiencedAgedAttribute powerAttribute() {
        return power;
    }

    @Override
    public PlayerLife headLife() {
        return headLife;
    }

    @Override
    public PlayerLife armLife() {
        return armLife;
    }

    @Override
    public PlayerLife legLife() {
        return legLife;
    }

    @Override
    public YinYang yinyang() {
        return yinYang;
    }

    @Override
    public int revivalExp() {
        return revival.exp();
    }

    @Override
    public int team() {
        return team;
    }

    @Override
    public Optional<GuildMembership> guildMembership() {
        return Optional.ofNullable(guildMembership);
    }

    @Override
    public void joinGuild(GuildMembership membership) {
        if (guildMembership().isPresent()) {
            sendEvent(PlayerTextMessage.systip(this, "你已有门派。"));
        } else {
            guildMembership = membership;
        }
    }

    @Override
    public void quitGuild() {
        guildMembership = null;
    }

    @Override
    public void cancelBuff() {
        if (buffPillSlot.isEffective()) {
            buffPillSlot.cancel();
            //emitEvent(UpdateBuffEvent.fade(this));
        }
    }

    @Override
    public boolean pickItem(Item item) {
        if (isLeftRealm() || isDead())
            return false;
        if (!inventory().canAdd(item)) {
            sendText("物品栏已满。");
            return false;
        }
        int slot = inventory().add(item);
        sendEvent(UpdateInventorySlotMessage.update(this, slot));
        item.eventSound().ifPresent(s -> sendEvent(PlayerSoundEvent.toSelf(this, s)));
        long number = item instanceof StackItem stackItem ? stackItem.number() : 1;
        sendLeftText("获得 " + item.name() + " " + number + "个。");
        return true;
    }

    void changeState(PlayerState playerState) {
        this.state = playerState;
    }


    @Override
    public PlayerStateEnum stateEnum() {
        return state.playerStateEnum();
    }

    @Override
    public int accuracy() {
        return innateAttributesProvider.hit();
    }

    private void startTradeWith(Player another, int slot) {
        if (isDead() || isLeftRealm()) {
            sendText("你无法进行交易。");
            return;
        }
        if (coordinate().directDistance(another.coordinate()) > 2) {
            sendText("距离过远。");
            return;
        }
        if (playerTrade != null) {
            sendText("交易正在进行中。");
            return;
        }
        var trade = new PlayerTrade(this, another);
        if (!another.acceptTrade(trade)) {
            sendText("对方无法被交易。");
            return;
        }
        playerTrade = trade;
        var item = inventory.getItem(slot);
        sendEvent(OpenTradeWindowMessage.proactive(this, another.viewName(), slot, item instanceof StackItem stackItem ? stackItem.number() : 1, item.name()));
    }

    private void dragDeadPlayer(Player dragged, int slot) {
        if (!dragged.canBeDragged())
            return;
        Item item = inventory().getItem(slot);
        if (!item.name().equals("追魂索"))
            return;
        for (Rope rope : ropes) {
            if (rope.isDragging(this, dragged))
                return;
        }
        Rope rope = new Rope(this, dragged);
        ropes.add(rope);
        inventory.decrease(slot, 1);
        sendEvent(UpdateInventorySlotMessage.update(this, slot));
    }

    @Override
    public void dropItemOnAnother(Player another, int slot) {
        if (isLeftRealm())
            return;
        if (this.equals(another))
            return;
        Item item = inventory().getItem(slot);
        if (item == null)
            return;
        if (!another.isDead())
            startTradeWith(another, slot);
        else
            dragDeadPlayer(another, slot);
    }

    @Override
    public boolean acceptTrade(PlayerTrade trade) {
        if (isDead() || isLeftRealm() || playerTrade != null)
            return false;
        Player another = trade.getAnother(this).orElse(null);
        if (another == null)
            return false;
        this.playerTrade = trade;
        sendEvent(OpenTradeWindowMessage.passive(this, another.viewName()));
        return true;
    }

    @Override
    public void closeTrade() {
        playerTrade = null;
    }

    @Override
    public boolean canBeDragged() {
        return !isLeftRealm() && state.canBeDragged();
    }

    private void sendSound(String s) {
        sendEvent(PlayerSoundEvent.toAll(this, s));
    }

    @Override
    public Inventory inventory() {
        return inventory;
    }

    @Override
    public Damage damage() {
        var dmg = weapon().map(Weapon::damage).orElse(Damage.ZERO)
                .add(innateAttributesProvider.damage())
                .add(attackKungFu().damage());
        dmg = buffPillSlot.apply(dmg);
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
    public Armor armor() {
        return aggregateArmor();
    }

    // mandieNew 2003, ManDieOld 2005, womanDieNew 2203, womanDieOld 2205.

    private Optional<String> hurtSound() {
        if (ThreadLocalRandom.current().nextInt(0, 10) < 4) {
            return Optional.empty();
        }
        return Optional.of(age() < 6000 ?
                (isMale() ? "2002" : "2202") :
                (isMale() ? "2004" : "2204") );
    }

    private Optional<String> dieSound() {
        return Optional.of(age() < 6000 ?
                (isMale() ? "2003" : "2203") :
                (isMale() ? "2005" : "2205") );
    }

    @Override
    public boolean isDead() {
        return stateEnum() == PlayerStateEnum.Die;
    }


   int attackCooldown() {
        return attackCooldown;
    }

    private void cooldown(int delta) {
        recoveryCooldown = recoveryCooldown > delta ? recoveryCooldown - delta : 0;
        attackCooldown = attackCooldown > delta ? attackCooldown - delta : 0;
    }

    private void cooldownRecovery() {
        recoveryCooldown = recovery() * Realm.STEP_MILLIS;
    }

    void cooldownAttack() {
        attackCooldown = attackSpeed() * Realm.STEP_MILLIS;
    }

    private void handleKilled() {
        footKungfu = null;
        breathKungFu = null;
        combatController = null;
        var oldLevel = revival.level();
        revival = revival.gainExp();
        if (oldLevel != revival.level()) {
            sendEvent(PlayerGainExpMessage.nonKungFu(this, "再生"));
        }
        syncActiveKungFuList();
        dieSound().ifPresent(this::sendSound);
        changeState(PlayerDieState.of(this));
        if (playerTrade != null)
            playerTrade.cancel(this);
        sendEvent(PlayerChangeStateEvent.allVisible(this));
    }


    @Override
    public <AB> Optional<AB> findAbility(Class<AB> type) {
        return  type.isAssignableFrom(this.getClass()) ?
            Optional.of(type.cast(this)) : Optional.empty();
    }
    @Override
    public boolean canBeAttacked() {
        return !isDead() && realm.get() != null;
    }

    @Override
    public boolean swingAllowed() {
        return !isLeftRealm();
    }

    int maxCooldown(){
        return Math.max(attackCooldown, recoveryCooldown);
    }

    private boolean isDodged(int attackerHit) {
        var rand = ThreadLocalRandom.current().nextInt(0, attackerHit + avoidance());
        return rand < avoidance();
    }


    @Override
    public int attacked(ActiveEntity attacker, Damage damage, int accuracy) {
        if (isDodged(accuracy))
            return -1;
        if (isDead() || isLeftRealm())
            return -1;
        int old = life.currentValue();
        takeDamage(damage);
        sendEvent(PlayerLifeBarEvent.of(this));
        sendEvent(PlayerDamagedEvent.create(this));
        if (life.currentValue() > 0) {
            cooldownRecovery();
            hurtSound().ifPresent(this::sendSound);
            changeState(PlayerHurtState.create(this, state));
            sendEvent(PlayerChangeStateEvent.allVisible(this));
            gainProtectionExp(old - currentLife());
        } else {
            handleKilled();
        }
        return ExperienceUtil.damageToExp(maxLife(), old - life.currentValue());
    }

    @Override
    public Set<Entity> getEntitiesAt(Set<Coordinate> coordinates) {
        var event = FilterVisibleEvent.filterVisibleAt(this, coordinates);
        sendEvent(event);
        return event.resultStream(Entity.class).collect(Collectors.toSet());
    }

    @Override
    public Optional<String> clickText() {
        StringBuilder stringBuilder = new StringBuilder("名称: ")
                .append(viewName()).append("\r\n");
        guildMembership().ifPresent(m -> m.append(stringBuilder));
        stringBuilder.append("使用武功: ").append(attackKungFu().name());
        protectKungFu().ifPresent(p -> stringBuilder.append(" ").append(p.name()));
        footKungFu().ifPresent(f -> stringBuilder.append(" ").append(f.name()));
        assistantKungFu().ifPresent(a -> stringBuilder.append(" ").append(a.name()));
        breathKungFu().ifPresent(b -> stringBuilder.append(" ").append(b.name()));
        return Optional.of(stringBuilder.toString());
    }
}
