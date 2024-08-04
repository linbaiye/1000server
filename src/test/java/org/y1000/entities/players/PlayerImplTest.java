package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.AttackableActiveEntity;
import org.y1000.entities.creatures.event.CreatureHurtEvent;
import org.y1000.entities.creatures.event.EntitySoundEvent;
import org.y1000.entities.creatures.monster.Monster;
import org.y1000.entities.players.event.*;
import org.y1000.exp.ExperienceUtil;
import org.y1000.item.*;
import org.y1000.kungfu.*;
import org.y1000.TestingEventListener;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.monster.PassiveMonster;
import org.y1000.entities.players.fight.PlayerAttackState;
import org.y1000.entities.players.fight.PlayerCooldownState;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.kungfu.attack.AttackKungFuType;
import org.y1000.kungfu.attack.QuanfaKungFu;
import org.y1000.kungfu.attack.SwordKungFu;
import org.y1000.kungfu.breath.BreathKungFu;
import org.y1000.kungfu.protect.ProtectKungFu;
import org.y1000.kungfu.protect.ProtectionParameters;
import org.y1000.message.InputResponseMessage;
import org.y1000.message.PlayerTextEvent;
import org.y1000.message.PositionType;
import org.y1000.message.RightClickType;
import org.y1000.message.clientevent.*;
import org.y1000.message.clientevent.input.RightMouseClick;
import org.y1000.message.serverevent.PlayerEquipEvent;
import org.y1000.message.serverevent.UpdateInventorySlotEvent;
import org.y1000.network.gen.AttributePacket;
import org.y1000.network.gen.ItemAttributePacket;
import org.y1000.network.gen.KungFuPacket;
import org.y1000.network.gen.PlayerRightClickAttributePacket;
import org.y1000.realm.Realm;
import org.y1000.realm.RealmMap;
import org.y1000.repository.ItemRepositoryImpl;
import org.y1000.repository.KungFuBookRepositoryImpl;
import org.y1000.sdb.ItemDrugSdbImpl;
import org.y1000.util.Coordinate;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@Slf4j
class PlayerImplTest extends AbstractPlayerUnitTestFixture {

    private Inventory inventory;

    private final ItemFactory itemFactory = new ItemRepositoryImpl(ItemSdbImpl.INSTANCE, ItemDrugSdbImpl.INSTANCE, new KungFuBookRepositoryImpl());
    private final KungFuFactory kungFuFactory = new KungFuBookRepositoryImpl();

    @BeforeEach
    public void setUp() {
        setup();
        inventory = player.inventory();
    }


    private Weapon createWeapon(String name, AttackKungFuType type) {
        ItemSdb mock = Mockito.mock(ItemSdb.class);
        when(mock.getAttackKungFuType(anyString())).thenReturn(type);
        return new WeaponImpl(name, mock);
    }



    private void attachListener(PlayerImpl.PlayerImplBuilder builder) {
        eventListener = new TestingEventListener();
        player = builder.build();
        player.registerEventListener(eventListener);
    }

    private PassiveMonster createMonster(Coordinate coordinate) {
        return monsterBuilder().coordinate(coordinate).build();
    }

    @Test
    void hat() {
        player = playerBuilder().hat(new ArmorEquipmentImpl("test", new TestArmorAttributeProvider(), EquipmentType.HAT)).build();
        assertEquals("test", player.hat().get().name());
        player.handleClientEvent(new ClientUnequipEvent(EquipmentType.HAT));
        assertTrue(player.hat().isEmpty());
    }

    @Test
    void chest() {
        player = playerBuilder().chest(new ArmorEquipmentImpl("chest", new TestArmorAttributeProvider(), EquipmentType.CHEST)).build();
        assertEquals("chest", player.chest().get().name());
        player.handleClientEvent(new ClientUnequipEvent(EquipmentType.CHEST));
        assertTrue(player.chest().isEmpty());
    }

    @Test
    void boot() {
        player = playerBuilder().boot(new ArmorEquipmentImpl("boot", new TestArmorAttributeProvider(), EquipmentType.BOOT)).build();
        assertEquals("boot", player.boot().get().name());
        player.handleClientEvent(new ClientUnequipEvent(EquipmentType.BOOT));
        assertTrue(player.boot().isEmpty());
    }


    @Test
    void equipHatEvent() {
        Inventory inventory = new Inventory();
        int slot = inventory.add(new ArmorEquipmentImpl("test", new TestArmorAttributeProvider(), EquipmentType.HAT));
        player = playerBuilder().inventory(inventory).build();
        player.registerEventListener(eventListener);
        player.handleClientEvent(new ClientDoubleClickSlotEvent(slot));
        PlayerEquipEvent first = eventListener.dequeue(PlayerEquipEvent.class);
        assertEquals(first.player(), player);
        assertEquals(first.toPacket().getEquip().getEquipmentName(), "test");
        UpdateInventorySlotEvent second = eventListener.dequeue(UpdateInventorySlotEvent.class);
        assertEquals(second.toPacket().getUpdateSlot().getSlotId(), slot);
        assertEquals(second.toPacket().getUpdateSlot().getName(), "");
    }

    @Test
    void equipNotSameTypeWeapon() {
        Weapon test = createWeapon("sword", AttackKungFuType.SWORD);
        int slot = inventory.add(test);
        player.handleClientEvent(new ClientDoubleClickSlotEvent(slot));
        assertTrue(player.weapon().isPresent());
        assertSame(player.attackKungFu().getType(), AttackKungFuType.SWORD);
        assertTrue(player.cooldown() != 0);
        var removeInventorySlotEvent = eventListener.dequeue(UpdateInventorySlotEvent.class);
        assertEquals(removeInventorySlotEvent.toPacket().getUpdateSlot().getSlotId(), slot);
        PlayerEquipEvent equipEvent = eventListener.dequeue(PlayerEquipEvent.class);
        assertEquals(equipEvent.toPacket().getEquip().getEquipmentName(), "sword");
        PlayerToggleKungFuEvent kungFuEvent = eventListener.dequeue(PlayerToggleKungFuEvent.class);
        assertEquals(kungFuEvent.toPacket().getToggleKungFu().getName(), player.kungFuBook().findUnnamedAttack(AttackKungFuType.SWORD).name());
        assertNull(inventory.getItem(slot));
    }

    @Test
    void equipWeapon_swapEquipped() {
        Weapon sword = createWeapon("sword", AttackKungFuType.SWORD);
        int slot1 = inventory.add(sword);
        player.handleClientEvent(new ClientDoubleClickSlotEvent(slot1));
        eventListener.clearEvents();
        var axe = createWeapon("axe", AttackKungFuType.AXE);
        int slot2 = inventory.add(axe);

        // act
        player.handleClientEvent(new ClientDoubleClickSlotEvent(slot2));

        assertEquals(player.attackKungFu().name(), player.kungFuBook().findUnnamedAttack(AttackKungFuType.AXE).name());
        assertSame(inventory.getItem(slot2), sword);
        assertTrue(player.weapon().isPresent());
        player.weapon().ifPresent(weapon -> assertSame(axe, weapon));
        var removeItemEvent = eventListener.dequeue(UpdateInventorySlotEvent.class);
        assertEquals(removeItemEvent.toPacket().getUpdateSlot().getSlotId(), slot2);
        var putItemEvent = eventListener.dequeue(UpdateInventorySlotEvent.class);
        assertEquals(putItemEvent.toPacket().getUpdateSlot().getSlotId(), slot2);
        assertEquals(putItemEvent.toPacket().getUpdateSlot().getName(), "sword");
        var equipEvent = eventListener.dequeue(PlayerEquipEvent.class);
        assertEquals(equipEvent.toPacket().getEquip().getEquipmentName(), "axe");
        var kungFuEvent = eventListener.dequeue(PlayerToggleKungFuEvent.class);
        assertEquals(kungFuEvent.toPacket().getToggleKungFu().getName(),
                player.kungFuBook().findUnnamedAttack(AttackKungFuType.AXE).name());
    }

    @Test
    void equipWeaponWileAttacking() {
        Weapon sword = createWeapon("sword", AttackKungFuType.SWORD);
        int slot1 = inventory.add(sword);
        player.handleClientEvent(new ClientDoubleClickSlotEvent(slot1));
        player.setFightingEntity(createMonster(new Coordinate(1, 1)));
        player.changeState(PlayerAttackState.melee(player));
        eventListener.clearEvents();
        var axe = createWeapon("axe", AttackKungFuType.AXE);
        int slot2 = inventory.add(axe);
        player.handleClientEvent(new ClientDoubleClickSlotEvent(slot2));

        assertEquals(player.cooldown(), (PlayerImpl.INNATE_ATTACKSPEED + player.kungFuBook().findUnnamedAttack(AttackKungFuType.AXE).attackSpeed()) * Realm.STEP_MILLIS);
        assertTrue(player.state() instanceof PlayerCooldownState);
    }

    @Test
    void changeAttackKungFu_sameType() {
        PlayerImpl.PlayerImplBuilder builder = playerBuilder().attackKungFu(QuanfaKungFu.builder().name("test").exp(0).build());
        attachListener(builder);
        player.handleClientEvent(new ClientToggleKungFuEvent(1, 1));
        var kungFuEvent = eventListener.dequeue(PlayerToggleKungFuEvent.class);
        assertEquals(kungFuEvent.toPacket().getToggleKungFu().getName(), "无名拳法");
        player.handleClientEvent(new ClientToggleKungFuEvent(1, 1));
        assertEquals(0, eventListener.eventSize());
        player.handleClientEvent(new ClientToggleKungFuEvent(1, 2));
        var text = eventListener.dequeue(PlayerTextEvent.class);
        assertEquals(text.toPacket().getText().getType(), PlayerTextEvent.TextType.NO_WEAPON.value());
        player.handleClientEvent(new ClientToggleKungFuEvent(1, 2));
    }

    @Test
    void changeAttackKungFu_differentTypeWhileAttacking() {
        PlayerImpl.PlayerImplBuilder builder = playerBuilder().attackKungFu(QuanfaKungFu.builder().name("test").parameters(new TestingAttackKungFuParameters()).exp(0).build())
                .weapon(createWeapon("fist", AttackKungFuType.QUANFA)).inventory(inventory);
        attachListener(builder);
        player.setFightingEntity(createMonster(new Coordinate(1, 2)));
        player.changeState(PlayerAttackState.melee(player));

        int slot = inventory.add(createWeapon("sword", AttackKungFuType.SWORD));
        player.handleClientEvent(new ClientToggleKungFuEvent(1, 2));
        assertEquals(player.attackKungFu().getType(), AttackKungFuType.SWORD);
        assertTrue(player.weapon().isPresent());
        player.weapon().ifPresent(weapon -> assertEquals(weapon.name(), "sword"));
        Weapon weapon = (Weapon) inventory.getItem(slot);
        assertEquals(weapon.name(), "fist");
        PlayerCooldownEvent playerCooldownEvent = eventListener.removeFirst(PlayerCooldownEvent.class);
        assertNotNull(playerCooldownEvent);
        var kungFuEvent = eventListener.removeFirst(PlayerToggleKungFuEvent.class);
        assertNotNull(kungFuEvent);
    }

    @Test
    void unequipWeapon() {
        PlayerImpl.PlayerImplBuilder builder = playerBuilder().attackKungFu(QuanfaKungFu.builder().name("test").exp(0).build())
                .weapon(createWeapon("fist", AttackKungFuType.QUANFA)).inventory(inventory);
        attachListener(builder);
        player.handleClientEvent(new ClientUnequipEvent(EquipmentType.WEAPON));
        var inventorySlotEvent = eventListener.removeFirst(UpdateInventorySlotEvent.class);
        assertEquals(inventorySlotEvent.toPacket().getUpdateSlot().getName(), "fist");
        assertTrue(player.weapon().isEmpty());
        assertTrue(inventory.findWeaponSlot(AttackKungFuType.QUANFA) != 0);
        PlayerUnequipEvent unequipEvent = eventListener.removeFirst(PlayerUnequipEvent.class);
        assertEquals(unequipEvent.toPacket().getUnequip().getEquipmentType(), EquipmentType.WEAPON.value());
    }

    @Test
    void unequipWeaponWhileAttacking() {
        PlayerImpl.PlayerImplBuilder builder = playerBuilder().attackKungFu(SwordKungFu.builder().name("test").exp(0).parameters(new TestingAttackKungFuParameters()).build())
                .weapon(createWeapon("sword", AttackKungFuType.SWORD)).inventory(inventory);
        attachListener(builder);
        player.setFightingEntity(createMonster(new Coordinate(2, 2)));
        player.changeState(PlayerAttackState.melee(player));
        player.handleClientEvent(new ClientUnequipEvent(EquipmentType.WEAPON));
        var kungFuEvent = eventListener.removeFirst(PlayerToggleKungFuEvent.class);
        assertEquals(kungFuEvent.toPacket().getToggleKungFu().getName(), "无名拳法");
        var cooldownEvent = eventListener.removeFirst(PlayerCooldownEvent.class);
        assertNotNull(cooldownEvent);
        assertSame(player.stateEnum(), State.COOLDOWN);
        assertEquals((PlayerImpl.INNATE_ATTACKSPEED + player.kungFuBook().findUnnamedAttack(AttackKungFuType.QUANFA).attackSpeed()) *  Realm.STEP_MILLIS, player.cooldown());
    }

    @Test
    void useProtectionKungFu() {
        player.handleClientEvent(new ClientToggleKungFuEvent(1, 10));
        assertTrue(player.protectKungFu().isPresent());
        player.protectKungFu().ifPresent(kf -> assertEquals(kf.kungFuType(), KungFuType.PROTECTION));
        var event = eventListener.dequeue(PlayerToggleKungFuEvent.class);
        assertFalse(event.toPacket().getToggleKungFu().getQuietly());
        assertEquals(event.toPacket().getToggleKungFu().getName(), player.kungFuBook().getUnnamedProtection().name());
        assertTrue(event.toPacket().getToggleKungFu().hasLevel());
        assertNotNull(eventListener.removeFirst(EntitySoundEvent.class));

        eventListener.clearEvents();
        player.handleClientEvent(new ClientToggleKungFuEvent(1, 10));
        event = eventListener.removeFirst(PlayerToggleKungFuEvent.class);
        assertFalse(event.toPacket().getToggleKungFu().getQuietly());
        assertFalse(event.toPacket().getToggleKungFu().hasLevel());
        assertNotNull(eventListener.removeFirst(EntitySoundEvent.class));
    }

    @Test
    void useProtectionWhenBreathingEnabled() {
        PlayerImpl.PlayerImplBuilder builder = playerBuilder().breathKungFu(BreathKungFu.builder().name("breath").exp(0).sound("1").build());
        attachListener(builder);
        player.handleClientEvent(new ClientToggleKungFuEvent(1, 10));
        assertTrue(player.breathKungFu().isEmpty());
        var disableBreathEvent = eventListener.dequeue(PlayerToggleKungFuEvent.class);
        assertTrue(disableBreathEvent.toPacket().getToggleKungFu().getQuietly());
        assertEquals(disableBreathEvent.toPacket().getToggleKungFu().getName(), "breath");
        assertFalse(disableBreathEvent.toPacket().getToggleKungFu().hasLevel());
        var protectEvent = eventListener.dequeue(PlayerToggleKungFuEvent.class);
        assertFalse(protectEvent.toPacket().getToggleKungFu().getQuietly());
    }

    @Test
    void useBreathKungFu_toggleTheSameOne() {
        player.handleClientEvent(new ClientToggleKungFuEvent(1, 9));
        assertTrue(player.breathKungFu().isPresent());
        player.breathKungFu().ifPresent(breathKungFu -> assertSame(breathKungFu, player.kungFuBook().getUnnamedBreath()));
        assertEquals(player.stateEnum(), State.SIT);
        var sitDownEvent = eventListener.dequeue(PlayerSitDownEvent.class);
        assertNotNull(sitDownEvent);
        var kungFuEvent = eventListener.dequeue(PlayerToggleKungFuEvent.class);
        assertTrue(kungFuEvent.toPacket().getToggleKungFu().hasLevel());
        assertFalse(kungFuEvent.toPacket().getToggleKungFu().getQuietly());

        player.handleClientEvent(new ClientToggleKungFuEvent(1, 9));
        assertTrue(player.breathKungFu().isEmpty());
        assertEquals(player.stateEnum(), State.SIT);
        kungFuEvent = eventListener.dequeue(PlayerToggleKungFuEvent.class);
        assertFalse(kungFuEvent.toPacket().getToggleKungFu().hasLevel());
        assertFalse(kungFuEvent.toPacket().getToggleKungFu().getQuietly());
    }

    @Test
    void useBreathKungFu_useDifferentOne() {
        PlayerImpl.PlayerImplBuilder builder = playerBuilder().breathKungFu(BreathKungFu.builder().name("breath").exp(0).sound("1").build());
        attachListener(builder);
        player.handleClientEvent(new ClientToggleKungFuEvent(1, 9));
        assertTrue(player.breathKungFu().isPresent());
        player.breathKungFu().ifPresent(breathKungFu -> assertSame(breathKungFu, player.kungFuBook().getUnnamedBreath()));
        var kungFuEvent = eventListener.dequeue(PlayerToggleKungFuEvent.class);
        assertTrue(kungFuEvent.toPacket().getToggleKungFu().hasLevel());
        assertFalse(kungFuEvent.toPacket().getToggleKungFu().getQuietly());
        assertEquals(kungFuEvent.toPacket().getToggleKungFu().getName(), player.kungFuBook().getUnnamedBreath().name());
    }

    @Test
    void useBreath_shouldDisableProtection() {
        ProtectionParameters parameters = Mockito.mock(ProtectionParameters.class);
        when(parameters.disableSound()).thenReturn("");
        PlayerImpl.PlayerImplBuilder builder = playerBuilder().protectKungFu(ProtectKungFu.builder().name("prot").exp(0).parameters(parameters).build());
        attachListener(builder);
        player.handleClientEvent(new ClientToggleKungFuEvent(1, 9));
        assertTrue(player.protectKungFu().isEmpty());
        boolean foundDisableProtection = false;
        PlayerToggleKungFuEvent event;
        while ((event = eventListener.removeFirst(PlayerToggleKungFuEvent.class)) != null)  {
            if (event.toPacket().getToggleKungFu().getName().equals("prot")) {
                foundDisableProtection = true;
                break;
            }
        }
        assertTrue(foundDisableProtection);
        assertTrue(event.toPacket().getToggleKungFu().getQuietly());
    }


    @Test
    void assistantWhenSwitchingKungFu() {
        player.kungFuBook().addToBasic(AssistantKungFu.builder().name("feng").exp(0).build());
        while (player.attackKungFu().level() < 9999) {
            player.gainAttackExp(ExperienceUtil.DEFAULT_EXP);
        }

        player.kungFuBook().addToBasic(kungFuFactory.create("太极剑结"));
        player.handleClientEvent(new ClientToggleKungFuEvent(2, 2));
        while (player.attackKungFu().level() < 9999) {
            player.gainAttackExp(ExperienceUtil.DEFAULT_EXP);
        }
        player.handleClientEvent(new ClientToggleKungFuEvent(2, 1));
        assertTrue(player.assistantKungFu().isPresent());

        player.handleClientEvent(new ClientToggleKungFuEvent(1, 1));
        assertTrue(player.assistantKungFu().isPresent());
        assertEquals(AttackKungFuType.QUANFA, player.attackKungFu().getType());

        player.inventory().add(itemFactory.createItem("长剑"));
        player.handleClientEvent(new ClientToggleKungFuEvent(1, 2));
        assertTrue(player.assistantKungFu().isEmpty());
    }


    @Test
    void useAssistantKungFu() {
        player.kungFuBook().addToBasic(AssistantKungFu.builder().name("feng").exp(0).build());
        player.kungFuBook().addToBasic(AssistantKungFu.builder().name("feng1").exp(0).build());
        player.handleClientEvent(new ClientToggleKungFuEvent(2, 1));
        PlayerTextEvent event = eventListener.removeFirst(PlayerTextEvent.class);
        assertEquals(PlayerTextEvent.TextType.KUNGFU_LEVEL_LOW.value(), event.toPacket().getText().getType());

        while (player.attackKungFu().level() < 9999) {
            player.gainAttackExp(ExperienceUtil.DEFAULT_EXP);
        }
        player.handleClientEvent(new ClientToggleKungFuEvent(2, 1));
        var kungFuEvent = eventListener.removeFirst(PlayerToggleKungFuEvent.class);
        assertEquals(kungFuEvent.toPacket().getToggleKungFu().getName(), "feng");
        assertTrue(kungFuEvent.toPacket().getToggleKungFu().hasLevel());
        assertTrue(player.assistantKungFu().isPresent());

        eventListener.clearEvents();
        player.handleClientEvent(new ClientToggleKungFuEvent(2, 2));
        kungFuEvent = eventListener.removeFirst(PlayerToggleKungFuEvent.class);
        assertEquals(kungFuEvent.toPacket().getToggleKungFu().getName(), "feng1");
        assertTrue(kungFuEvent.toPacket().getToggleKungFu().hasLevel());
        assertTrue(player.assistantKungFu().isPresent());

        player.handleClientEvent(new ClientToggleKungFuEvent(2, 2));
        kungFuEvent = eventListener.dequeue(PlayerToggleKungFuEvent.class);
        assertEquals(kungFuEvent.toPacket().getToggleKungFu().getName(), "feng1");
        assertFalse(kungFuEvent.toPacket().getToggleKungFu().hasLevel());
        assertFalse(player.assistantKungFu().isPresent());
    }

    @Test
    void monitorFightingTarget() {
        AttackableActiveEntity mock = Mockito.mock(AttackableActiveEntity.class);
        player.setFightingEntity(mock);
        Mockito.verify(mock, Mockito.times(1)).registerEventListener(player);
        player.leaveRealm();
        Mockito.verify(mock, Mockito.times(1)).deregisterEventListener(player);
    }

    @Test
    void handleMoveEventWhenIdle() {
        RealmMap map = mockRealmMap();
        Realm realm = mockRealm(map);
        player.joinReam(realm);
        eventListener.clearEvents();
        when(map.movable(player.coordinate().moveBy(Direction.DOWN))).thenReturn(true);
        player.handleClientEvent(new ClientMovementEvent(new RightMouseClick(1, Direction.DOWN), player.coordinate()));
        InputResponseMessage responseMessage = eventListener.dequeue(InputResponseMessage.class);
        assertEquals(PositionType.MOVE.value(), responseMessage.toPacket().getResponsePacket().getPositionPacket().getType());
        assertTrue(player.state() instanceof AbstractPlayerMoveState);

        player = playerBuilder().build();
        player.registerEventListener(eventListener);
        player.joinReam(realm);
        eventListener.clearEvents();
        Mockito.reset(map);
        when(map.movable(player.coordinate().moveBy(Direction.UP))).thenReturn(false);
        player.handleClientEvent(new ClientMovementEvent(new RightMouseClick(1, Direction.UP), player.coordinate()));
        responseMessage = eventListener.dequeue(InputResponseMessage.class);
        assertEquals(PositionType.REWIND.value(), responseMessage.toPacket().getResponsePacket().getPositionPacket().getType());
    }

    @Test
    void consumeAttributes() {
        player = playerBuilder().innerPower(PlayerTestingAttribute.of(100))
                .outerPower(PlayerTestingAttribute.of(50)).power(PlayerTestingAttribute.of(20))
                .life(new PlayerLife(10, 0)).build();
        player.consumePower(1);
        player.consumeInnerPower(1);
        player.consumeOuterPower(1);
        assertEquals(19, player.power());
        assertEquals(49, player.outerPower());
        assertEquals(99, player.innerPower());
        player.consumePower(100);
        player.consumeLife(120);
        player.consumeInnerPower(100);
        player.consumeOuterPower(100);
        assertEquals(0, player.outerPower());
        assertEquals(0, player.currentLife());
        assertEquals(0, player.innerPower());
        assertEquals(0, player.outerPower());
    }

    @Test
    void updateProtectKungFu() {
        ProtectionParameters parameters = Mockito.mock(ProtectionParameters.class);
        player = playerBuilder().power(PlayerTestingAttribute.of(1)).life(new PlayerLife(1, 0)).innerPower(PlayerTestingAttribute.of(1)).outerPower(PlayerTestingAttribute.of(1))
                .protectKungFu(ProtectKungFu.builder().exp(0).parameters(parameters).name("test").build())
                .build();
        player.registerEventListener(eventListener);
        when(parameters.innerPowerPer5Seconds()).thenReturn(2);
        when(parameters.innerPowerToKeep()).thenReturn(1);
        when(parameters.lifePer5Seconds()).thenReturn(103);
        when(parameters.lifeToKeep()).thenReturn(3);
        when(parameters.powerPer5Seconds()).thenReturn(1);
        when(parameters.powerToKeep()).thenReturn(1);
        player.update(5000);
        assertEquals(1, player.currentLife());
        assertEquals(0, player.innerPower());
        assertTrue(player.protectKungFu().isEmpty());
        assertNotNull(eventListener.removeFirst(PlayerToggleKungFuEvent.class));
        assertNotNull(eventListener.removeFirst(EntitySoundEvent.class));
    }

    @Test
    void startBowAttack() {
        PassiveMonster monster = createMonster(player.coordinate().move(2, 0));
        player.inventory().add(createWeapon("bow", AttackKungFuType.BOW));
        enableBowKungFu();
        // long enough to clean cooldown frozen because of equipping bow.
        player.update(100000);
        ClientAttackEvent clientEvent = new ClientAttackEvent(1, monster.id(), State.BOW, Direction.RIGHT);
        player.attack(clientEvent, monster);
        // no ammo.
        assertEquals(State.IDLE, player.stateEnum());
        PlayerTextEvent event = eventListener.removeFirst(PlayerTextEvent.class);
        assertEquals(PlayerTextEvent.TextType.OUT_OF_AMMO.value(), event.toPacket().getText().getType());

        player.inventory().add(itemFactory.createItem("箭", 1));
        eventListener.clearEvents();

        player.attack(clientEvent, monster);
        assertTrue(player.state() instanceof PlayerAttackState);
        PlayerAttackEventResponse response = eventListener.removeFirst(PlayerAttackEventResponse.class);
        assertTrue(response.isAccepted());
        assertNotNull(eventListener.removeFirst(UpdateInventorySlotEvent.class));
    }

    @Test
    void accumulateYinYang() {
        YinYang.YinYangDecider decider = Mockito.mock(YinYang.YinYangDecider.class);
        when(decider.isYin()).thenReturn(true);
        attachListener(playerBuilder().yinYang(new YinYang(0, 0, decider)));
        player.update(PlayerImpl.DEFAULT_REGENERATE_SECONDS * 1000);
        PlayerGainExpEvent event = eventListener.removeFirst(PlayerGainExpEvent.class);
        assertEquals("阴气", event.toPacket().getGainExp().getName());
    }

    @Test
    void gainPowerExp() {
        int current = player.power();
        player.consumePower(player.power());
        for (int i = 0; i < current; i++)
            player.gainPower(1);
        var event = eventListener.removeFirst(PlayerGainExpEvent.class);
        assertEquals("武功", event.toPacket().getGainExp().getName());
    }

    @Test
    void armor() {
        assertEquals(player.attackKungFu().armor().body(), player.bodyArmor());
        enableProtectKungFu();
        assertEquals(player.attackKungFu().armor().add(player.protectKungFu().get().armor()).body(), player.bodyArmor());
        ArmorEquipment chest = itemFactory.createChest(player.isMale() ? "男子黄金铠甲" : "女子黄金铠甲");
        int slot = player.inventory().add(chest);
        player.handleClientEvent(new ClientDoubleClickSlotEvent(slot));
        assertEquals(player.attackKungFu().armor().add(player.protectKungFu().get().armor())
                .add(chest.armor()).body(), player.bodyArmor());
    }

    @Test
    void rightClickInventorySlot() {
        var slot = player.inventory().add(itemFactory.createItem("生药", 2));
        player.handleClientEvent(new ClientRightClickEvent(RightClickType.INVENTORY, slot, 0));
        ItemOrKungFuAttributeEvent event = eventListener.removeFirst(ItemOrKungFuAttributeEvent.class);
        assertTrue(event.toPacket().getItemAttribute().getText().contains("身体"));
    }

    @Test
    void rightClickUnnamedKungFu() {
        player.handleClientEvent(new ClientRightClickEvent(RightClickType.KUNGFU, 1, 1));
        ItemOrKungFuAttributeEvent event = eventListener.removeFirst(ItemOrKungFuAttributeEvent.class);
        ItemAttributePacket itemAttribute = event.toPacket().getItemAttribute();
        var text = itemAttribute.getText();
        assertTrue(text.contains("修炼等级"));
        assertTrue(text.contains("破坏力"));
        assertTrue(text.contains("防御力"));
        assertTrue(text.contains("攻击速度"));
        assertTrue(text.contains("闪躲"));
        assertTrue(text.contains("恢复"));
        assertEquals(RightClickType.KUNGFU.value(), itemAttribute.getType());
        assertEquals(1, itemAttribute.getPage());
        assertEquals(1, itemAttribute.getSlotId());
    }

    @Test
    void rightClickBasicKungFu() {
        int slot = player.kungFuBook().addToBasic(kungFuFactory.createProtection("金钟罩"));
        player.handleClientEvent(new ClientRightClickEvent(RightClickType.KUNGFU, slot, 2));
        ItemOrKungFuAttributeEvent event = eventListener.removeFirst(ItemOrKungFuAttributeEvent.class);
        ItemAttributePacket itemAttribute = event.toPacket().getItemAttribute();
        var text = itemAttribute.getText();
        assertTrue(text.contains("修炼等级"));
    }

    @Test
    void doubleClickPill() {
        int slot = player.inventory().add(itemFactory.createItem("生药", 2));
        player.handleClientEvent(new ClientDoubleClickSlotEvent(slot));
        assertEquals(1, ((StackItem)player.inventory().getItem(slot)).number());
    }

    @Test
    void attackedByAoe() {
        player = playerBuilder().innateAttributesProvider(TestingPlayerInnateAttributesProvider.builder().avoid(0).build()).build();
        enableTestingKungFu();
        assertTrue(player.attackedByAoe(Damage.DEFAULT, 100) > 0);
    }

    @Test
    void gainAssistantExp() {
        while (player.attackKungFu().level() < 9999) {
            player.attackKungFu().gainExp(ExperienceUtil.DEFAULT_EXP);
        }
        enableAssistant8KungFu();
        assertTrue(player.assistantKungFu().isPresent());
        player.gainAssistantExp(100);
        assertTrue( player.assistantKungFu().get().level() > 100);
    }

    @Test
    void attackedByMonster() {
        PlayerLife arm = PlayerLife.create();
        PlayerLife leg = PlayerLife.create();
        PlayerLife head = PlayerLife.create();
        player = playerBuilder().arm(arm).leg(leg).head(head).innateAttributesProvider(TestingPlayerInnateAttributesProvider.builder().avoid(0).build()).build();
        player.registerEventListener(eventListener);
        var monster = Mockito.mock(Monster.class);
        Damage dmg = new Damage(300, 100, 99, 98);
        when(monster.damage()).thenReturn(dmg);
        enableTestingKungFu();
        assertEquals(0, player.avoidance());
        player.attackedBy(monster);
        var am = player.attackKungFu().armor();
        assertEquals(player.maxLife() - (dmg.bodyDamage()  - am.body()), player.currentLife());
        assertEquals(leg.maxValue() - (dmg.legDamage()  - am.leg()), leg.currentValue());
        assertEquals(head.maxValue() - (dmg.headDamage()  - am.head()), head.currentValue());
        assertEquals(arm.maxValue() - (dmg.armDamage()  - am.arm()), arm.currentValue());
        assertNotNull(eventListener.removeFirst(CreatureHurtEvent.class));
    }

    @Test
    void attackedByPlayer() {
        PlayerLife arm = PlayerLife.create();
        PlayerLife leg = PlayerLife.create();
        PlayerLife head = PlayerLife.create();
        player = playerBuilder().arm(arm).leg(leg).head(head).innateAttributesProvider(TestingPlayerInnateAttributesProvider.builder().avoid(0).build()).build();
        enableTestingKungFu();
        player.registerEventListener(eventListener);
        var attacker = Mockito.mock(Player.class);
        Damage dmg = new Damage(300, 300, 300, 300);
        when(attacker.damage()).thenReturn(dmg);
        var am = player.attackKungFu().armor();
        eventListener.clearEvents();
        player.attackedBy(attacker);
        assertEquals(player.maxLife() - (dmg.bodyDamage()  - am.body()), player.currentLife());
        assertNotEquals(100, player.legPercent());
        assertNotEquals(100, player.headPercent());
        assertNotEquals(100, player.armPercent());
        verify(attacker, times(1)).gainAttackExp(anyInt());
        AttributePacket attribute = eventListener.removeFirst(PlayerAttributeEvent.class).toPacket().getAttribute();
        assertEquals(player.armPercent(), attribute.getArmPercent());
        assertEquals(player.legPercent(), attribute.getLegPercent());
        assertEquals(player.headPercent(), attribute.getHeadPercent());
    }


    @Test
    void damageAffectedByArm() {
        // make it 1000
        PlayerLife arm = new PlayerLife(900, 0);
        player = playerBuilder().arm(arm).innateAttributesProvider(TestingPlayerInnateAttributesProvider.builder().avoid(0).damage(Damage.ZERO).build()).build();
        enableTestingKungFu();
        assertTrue(player.damage().equalTo(player.attackKungFu().damage()));
        arm.consume(499);
        assertTrue(player.damage().equalTo(player.attackKungFu().damage()));
        arm.consume(1);
        assertTrue(player.damage().equalTo(player.attackKungFu().damage()));

        //98 % damage.
        arm.consume(10);
        assertEquals(player.attackKungFu().damage().multiply(0.98f).bodyDamage(), player.damage().bodyDamage());
        arm.consume(480);
        assertEquals(player.attackKungFu().damage().multiply(0.02f).bodyDamage(), player.damage().bodyDamage());
    }

    @Test
    void attackSpeedAffectedByLeg() {
        PlayerLife leg = new PlayerLife(900, 0);
        player = playerBuilder().leg(leg).innateAttributesProvider(TestingPlayerInnateAttributesProvider.builder().speed(0).build()).build();
        enableTestingKungFu();
        var speed = player.attackKungFu().attackSpeed();
        assertEquals(speed, player.attackSpeed());
        leg.consume(500);
        assertEquals(speed, player.attackSpeed());

        leg.consume(10);
        assertEquals(speed + speed * 0.02, player.attackSpeed());
        leg.consume(480);
        assertEquals(speed + speed * 0.98, player.attackSpeed());
    }

    @Test
    void learnKungFu() {
        var slot = player.inventory().add(itemFactory.createItem("闪光剑破解"));
        player.handleClientEvent(new ClientDoubleClickSlotEvent(slot));
        assertNull(player.inventory().getItem(slot));
        assertNotNull(player.kungFuBook().getKungFu(2, 1));
    }

    @Test
    void avoidance() {
        TestingPlayerInnateAttributesProvider attr = TestingPlayerInnateAttributesProvider.builder().speed(0).avoid(1).build();
        player = playerBuilder().innateAttributesProvider(attr).build();
        enableTestingKungFu();
        assertEquals(1, player.avoidance());
        Weapon weapon = Mockito.mock(Weapon.class);
        when(weapon.avoidance()).thenReturn(1);
        when(weapon.equipmentType()).thenReturn(EquipmentType.WEAPON);
        when(weapon.kungFuType()).thenReturn(AttackKungFuType.QUANFA);
        int slot = player.inventory().add(weapon);
        player.handleClientEvent(new ClientDoubleClickSlotEvent(slot));
        assertEquals(2, player.avoidance());

        var hat = Mockito.mock(ArmorEquipment.class);
        when(hat.equipmentType()).thenReturn(EquipmentType.HAT);
        when(hat.avoidance()).thenReturn(5);
        slot = player.inventory().add(hat);
        player.handleClientEvent(new ClientDoubleClickSlotEvent(slot));
        assertEquals(7, player.avoidance());
    }

    @Test
    void recovery() {
        TestingPlayerInnateAttributesProvider attr = TestingPlayerInnateAttributesProvider.builder().speed(0).recovery(3).build();
        player = playerBuilder().innateAttributesProvider(attr).build();
        enableTestingKungFu();
        assertEquals(3, player.recovery());
        Weapon weapon = Mockito.mock(Weapon.class);
        when(weapon.recovery()).thenReturn(1);
        when(weapon.equipmentType()).thenReturn(EquipmentType.WEAPON);
        when(weapon.kungFuType()).thenReturn(AttackKungFuType.QUANFA);
        int slot = player.inventory().add(weapon);
        player.handleClientEvent(new ClientDoubleClickSlotEvent(slot));
        assertEquals(2, player.recovery());
        var chest = Mockito.mock(ArmorEquipment.class);
        when(chest.equipmentType()).thenReturn(EquipmentType.CHEST);
        when(chest.recovery()).thenReturn(1);

        slot = player.inventory().add(chest);
        player.handleClientEvent(new ClientDoubleClickSlotEvent(slot));
        assertEquals(1, player.recovery());
    }

    @Test
    void damage() {
        TestingPlayerInnateAttributesProvider attr = TestingPlayerInnateAttributesProvider.builder().speed(0).damage(Damage.DEFAULT).build();
        player = playerBuilder().innateAttributesProvider(attr).build();
        enableTestingKungFu();
        var expected = player.attackKungFu().damage().add(Damage.DEFAULT);
        assertTrue(expected.equalTo(player.damage()));
        Weapon weapon = Mockito.mock(Weapon.class);
        when(weapon.damage()).thenReturn(new Damage(1, 1,1,1));
        when(weapon.equipmentType()).thenReturn(EquipmentType.WEAPON);
        when(weapon.kungFuType()).thenReturn(AttackKungFuType.QUANFA);
        int slot = player.inventory().add(weapon);
        player.handleClientEvent(new ClientDoubleClickSlotEvent(slot));
        assertTrue(expected.add(new Damage(1,1,1,1)).equalTo(player.damage()));
    }

    @Test
    void gainExp() {
        PlayerLife arm = new PlayerLife(900, 0);
        player = playerBuilder().arm(arm).build();
        enableTestingKungFu();
        player.gainAttackExp(0);
        assertEquals(100, player.attackKungFu().level());
        arm.consume(510);
        eventListener.clearEvents();
        player.registerEventListener(eventListener);
        player.gainAttackExp(1000);
        var event = eventListener.removeFirst(PlayerTextEvent.class);
        assertEquals(PlayerTextEvent.TextType.NOT_ENOUGH_ARM_LIFE.value(), event.toPacket().getText().getType());
        assertEquals(100, player.attackKungFu().level());
        arm.gain(10);
        player.gainAttackExp(1000);
        assertNotNull(eventListener.removeFirst(PlayerGainExpEvent.class));
        assertNotEquals(100, player.attackKungFu().level());
    }

    @Test
    void toggleBufa() {
        clickBasicFootKungFu();
        assertTrue(player.footKungFu().isPresent());
        clickBasicFootKungFu();
        assertTrue(player.footKungFu().isEmpty());
        addBasicKungFu(kungFuFactory.create("草上飞"));
        player.handleClientEvent(new ClientToggleKungFuEvent(2, 1));
        assertTrue(player.footKungFu().isPresent());
        clickBasicFootKungFu();
        assertTrue(player.footKungFu().isPresent());
    }

    @Test
    void rightClickAttribute() {
        player.handleClientEvent(new ClientRightClickEvent(RightClickType.CHARACTER, 0, 0));
        player.consumePower(20);
        player.consumeOuterPower(20);
        player.consumeInnerPower(20);
        player.consumeLife(20);
        PlayerRightClickAttributeEvent event = eventListener.removeFirst(PlayerRightClickAttributeEvent.class);
        PlayerRightClickAttributePacket attr = event.toPacket().getRightClickAttribute();
        assertEquals(player.maxInnerPower(), attr.getMaxInnerPower());
        assertEquals(player.maxOuterPower(), attr.getMaxOuterPower());
        assertEquals(player.maxPower(), attr.getMaxPower());
        assertEquals(player.maxLife(), attr.getMaxLife());
        assertEquals(player.attackSpeed(), attr.getAttackSpeed());
        assertEquals(player.recovery(), attr.getRecovery());
        assertEquals(player.avoidance(), attr.getAvoidance());
        assertEquals(player.damage().bodyDamage(), attr.getBodyDamage());
        assertEquals(player.damage().headDamage(), attr.getHeadDamage());
        assertEquals(player.damage().legDamage(), attr.getLegDamage());
        assertEquals(player.damage().armDamage(), attr.getArmDamage());
        assertEquals(player.armor().body(), attr.getBodyArmor());
        assertEquals(player.armor().head(), attr.getHeadArmor());
        assertEquals(player.armor().arm(), attr.getArmArmor());
        assertEquals(player.armor().leg(), attr.getLegArmor());
    }

    @Test
    void swapKungFuSlots() {
        player.kungFuBook().addToBasic(kungFuFactory.create("雷剑式"));
        player.handleClientEvent(new ClientSwapKungFuSlotEvent(2, 1, 2));
        KungFuPacket packet = eventListener.removeFirst(UpdateKungFuSlotEvent.class).toPacket().getUpdateKungFuSlot();
        assertEquals(1, packet.getSlot());
        assertEquals("", packet.getName());
        packet = eventListener.removeFirst(UpdateKungFuSlotEvent.class).toPacket().getUpdateKungFuSlot();
        assertEquals(2, packet.getSlot());
        assertEquals("雷剑式", packet.getName());
        assertEquals(100, packet.getLevel());
        assertEquals(AttackKungFuType.SWORD.value(), packet.getType());
    }
}