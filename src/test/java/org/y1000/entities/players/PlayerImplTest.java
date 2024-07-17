package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.AttackableEntity;
import org.y1000.entities.creatures.event.EntitySoundEvent;
import org.y1000.entities.players.event.*;
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
import org.y1000.network.gen.ItemAttributePacket;
import org.y1000.realm.Realm;
import org.y1000.realm.RealmMap;
import org.y1000.repository.ItemRepositoryImpl;
import org.y1000.repository.KungFuBookRepositoryImpl;
import org.y1000.sdb.ItemDrugSdbImpl;
import org.y1000.util.Coordinate;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

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
        return new Weapon(name, mock);
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
        player = playerBuilder().hat(new Hat("test", new TestArmorAttributeProvider())).build();
        assertEquals("test", player.hat().get().name());
    }


    @Test
    void equipHatEvent() {
        Inventory inventory = new Inventory();
        int slot = inventory.add(new Hat("test", new TestArmorAttributeProvider()));
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
        player.changeState(PlayerAttackState.of(player));
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
        player.changeState(PlayerAttackState.of(player));

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
        player.changeState(PlayerAttackState.of(player));
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
    void useAssistantKungFu() {
        player.kungFuBook().addToBasic(AssistantKungFu.builder().name("feng").exp(0).build());
        player.kungFuBook().addToBasic(AssistantKungFu.builder().name("feng1").exp(0).build());
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
        AttackableEntity mock = Mockito.mock(AttackableEntity.class);
        player.setFightingEntity(mock);
        Mockito.verify(mock, Mockito.times(1)).registerEventListener(player);
        player.leaveRealm();
        Mockito.verify(mock, Mockito.times(1)).deregisterEventListener(player);
    }

    @Test
    void handleMoveEventWhenIdle() {
        RealmMap map = Mockito.mock(RealmMap.class);
        Realm realm = Mockito.mock(Realm.class);
        when(realm.map()).thenReturn(map);
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
        Chest chest = itemFactory.createChest(player.isMale() ? "男子黄金铠甲" : "女子黄金铠甲");
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
        assertTrue(player.attackedByAoe(Damage.DEFAULT, 100) > 0);
    }

    @Test
    void gainAssistantExp() {
        enableAssistant8KungFu();
        assertTrue(player.assistantKungFu().isPresent());
        player.gainAssistantExp(100);
        assertTrue( player.assistantKungFu().get().level() > 100);
    }
}