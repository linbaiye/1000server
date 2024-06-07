package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.TestingEventListener;
import org.y1000.entities.PhysicalEntity;
import org.y1000.entities.creatures.State;
import org.y1000.entities.players.event.PlayerCooldownEvent;
import org.y1000.entities.players.event.PlayerSitDownEvent;
import org.y1000.entities.players.event.PlayerToggleKungFuEvent;
import org.y1000.entities.players.event.PlayerUnequipEvent;
import org.y1000.entities.players.fight.PlayerAttackState;
import org.y1000.entities.players.fight.PlayerCooldownState;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.item.EquipmentType;
import org.y1000.item.Weapon;
import org.y1000.kungfu.AssistantKungFu;
import org.y1000.kungfu.KungFuType;
import org.y1000.kungfu.attack.AttackKungFuType;
import org.y1000.item.Hat;
import org.y1000.kungfu.attack.QuanfaKungFu;
import org.y1000.kungfu.attack.SwordKungFu;
import org.y1000.kungfu.breath.BreathKungFu;
import org.y1000.kungfu.protect.ProtectKungFu;
import org.y1000.message.PlayerTextEvent;
import org.y1000.message.clientevent.ClientDoubleClickSlotEvent;
import org.y1000.message.clientevent.ClientToggleKungFuEvent;
import org.y1000.message.clientevent.ClientUnequipEvent;
import org.y1000.message.serverevent.PlayerEquipEvent;
import org.y1000.message.serverevent.UpdateInventorySlotEvent;
import org.y1000.realm.Realm;
import org.y1000.util.Coordinate;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@Slf4j
class PlayerImplTest extends AbstractUnitTestFixture {

    private PlayerImpl player;

    private TestingEventListener eventListener;

    private Inventory inventory;

    @BeforeEach
    public void setup() {
        inventory = new Inventory();
        attachListener(playerBuilder().inventory(inventory));
    }


    private void attachListener(PlayerImpl.PlayerImplBuilder builder) {
        eventListener = new TestingEventListener();
        player = builder.build();
        player.registerEventListener(eventListener);
    }


    @Test
    void hat() {
        player = playerBuilder().hat(new Hat("test")).build();
        assertEquals("test", player.hat().get().name());
    }

    @Test
    void equipHatEvent() {
        Inventory inventory = new Inventory();
        int slot = inventory.add(new Hat("test"));
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
        Weapon test = new Weapon("sword", AttackKungFuType.SWORD);
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
        Weapon sword = new Weapon("sword", AttackKungFuType.SWORD);
        int slot1 = inventory.add(sword);
        player.handleClientEvent(new ClientDoubleClickSlotEvent(slot1));
        eventListener.clearEvents();
        var axe = new Weapon("axe", AttackKungFuType.AXE);
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
        Weapon sword = new Weapon("sword", AttackKungFuType.SWORD);
        int slot1 = inventory.add(sword);
        player.handleClientEvent(new ClientDoubleClickSlotEvent(slot1));
        player.setFightingEntity(createMonster(new Coordinate(1, 1)));
        player.changeState(PlayerAttackState.of(player));
        eventListener.clearEvents();
        var axe = new Weapon("axe", AttackKungFuType.AXE);
        int slot2 = inventory.add(axe);
        player.handleClientEvent(new ClientDoubleClickSlotEvent(slot2));

        assertEquals(player.cooldown(), (PlayerImpl.INNATE_ATTACKSPEED + player.kungFuBook().findUnnamedAttack(AttackKungFuType.AXE).getAttackSpeed()) * Realm.STEP_MILLIS);
        assertTrue(player.state() instanceof PlayerCooldownState);
    }

    @Test
    void changeAttackKungFu_sameType() {
        PlayerImpl.PlayerImplBuilder builder = playerBuilder().attackKungFu(QuanfaKungFu.builder().name("test").level(100).build());
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
        PlayerImpl.PlayerImplBuilder builder = playerBuilder().attackKungFu(QuanfaKungFu.builder().name("test").level(100).build())
                .weapon(new Weapon("fist", AttackKungFuType.QUANFA)).inventory(inventory);
        attachListener(builder);
        player.setFightingEntity(createMonster(new Coordinate(1, 2)));
        player.changeState(PlayerAttackState.of(player));

        int slot = inventory.add(new Weapon("sword", AttackKungFuType.SWORD));
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
        PlayerImpl.PlayerImplBuilder builder = playerBuilder().attackKungFu(QuanfaKungFu.builder().name("test").level(100).build())
                .weapon(new Weapon("fist", AttackKungFuType.QUANFA)).inventory(inventory);
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
        PlayerImpl.PlayerImplBuilder builder = playerBuilder().attackKungFu(SwordKungFu.builder().name("test").level(100).build())
                .weapon(new Weapon("sword", AttackKungFuType.SWORD)).inventory(inventory);
        attachListener(builder);
        player.setFightingEntity(createMonster(new Coordinate(2, 2)));
        player.changeState(PlayerAttackState.of(player));
        player.handleClientEvent(new ClientUnequipEvent(EquipmentType.WEAPON));
        var kungFuEvent = eventListener.removeFirst(PlayerToggleKungFuEvent.class);
        assertEquals(kungFuEvent.toPacket().getToggleKungFu().getName(), "无名拳法");
        var cooldownEvent = eventListener.removeFirst(PlayerCooldownEvent.class);
        assertNotNull(cooldownEvent);
        assertSame(player.stateEnum(), State.COOLDOWN);
        assertEquals((PlayerImpl.INNATE_ATTACKSPEED + player.kungFuBook().findUnnamedAttack(AttackKungFuType.QUANFA).getAttackSpeed()) *  Realm.STEP_MILLIS, player.cooldown());
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

        player.handleClientEvent(new ClientToggleKungFuEvent(1, 10));
        event = eventListener.dequeue(PlayerToggleKungFuEvent.class);
        assertFalse(event.toPacket().getToggleKungFu().getQuietly());
        assertFalse(event.toPacket().getToggleKungFu().hasLevel());
    }

    @Test
    void useProtectionWhenBreathingEnabled() {
        PlayerImpl.PlayerImplBuilder builder = playerBuilder().breathKungFu(BreathKungFu.builder().name("breath").level(100).build());
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
        PlayerImpl.PlayerImplBuilder builder = playerBuilder().breathKungFu(BreathKungFu.builder().name("breath").level(100).build());
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
        PlayerImpl.PlayerImplBuilder builder = playerBuilder().protectKungFu(ProtectKungFu.builder().name("prot").level(100).build());
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
        player.kungFuBook().addToBasic(AssistantKungFu.builder().name("feng").level(100).build());
        player.kungFuBook().addToBasic(AssistantKungFu.builder().name("feng1").level(100).build());
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
        PhysicalEntity mock = Mockito.mock(PhysicalEntity.class);
        player.setFightingEntity(mock);
        Mockito.verify(mock, Mockito.times(1)).registerEventListener(player);
        player.leaveRealm();
        Mockito.verify(mock, Mockito.times(1)).deregisterEventListener(player);
    }
}