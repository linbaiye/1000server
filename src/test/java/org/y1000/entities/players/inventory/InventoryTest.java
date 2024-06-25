package org.y1000.entities.players.inventory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.TestingEventListener;
import org.y1000.entities.players.Player;
import org.y1000.item.StackItem;
import org.y1000.item.Weapon;
import org.y1000.kungfu.attack.AttackKungFuType;
import org.y1000.event.EntityEvent;
import org.y1000.message.serverevent.UpdateInventorySlotEvent;
import org.y1000.util.UnaryAction;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InventoryTest {

    private Inventory inventory;

    private Player player;

    private UnaryAction<EntityEvent> eventEmiter;

    private static class EventSaver implements UnaryAction<EntityEvent> {
        private EntityEvent entityEvent;

        @Override
        public void invoke(EntityEvent entityEvent) {
            this.entityEvent = entityEvent;
        }
    }

    @BeforeEach
    void setUp() {
        inventory = new Inventory();
        player = Mockito.mock(Player.class);
        eventEmiter = new EventSaver();
    }

    @Test
    void findByType() {
        inventory.add(new Weapon("test", AttackKungFuType.SWORD));
        Optional<Weapon> weapon = inventory.findWeapon(AttackKungFuType.SWORD);
        assertTrue(weapon.isPresent());
        weapon.ifPresent(w -> assertEquals(w.kungFuType(), AttackKungFuType.SWORD));
    }

    @Test
    void findSlot() {
        inventory.add(new Weapon("test", AttackKungFuType.SWORD));
        int weaponSlot = inventory.findWeaponSlot(AttackKungFuType.SWORD);
        assertEquals(1, weaponSlot);
        assertEquals(0, inventory.findWeaponSlot(AttackKungFuType.AXE));
    }

    @Test
    void consumeStackItem() {
        TestingEventListener eventListener = new TestingEventListener();
        assertFalse(inventory.consumeStackItem(player, "箭", eventListener::onEvent));
        inventory.add(new StackItem("箭", 2));
        assertTrue(inventory.consumeStackItem(player, "箭", eventListener::onEvent));
        UpdateInventorySlotEvent event = eventListener.dequeue(UpdateInventorySlotEvent.class);
        assertEquals("箭", event.toPacket().getUpdateSlot().getName());
        assertEquals(1, event.toPacket().getUpdateSlot().getNumber());
        assertTrue(inventory.contains("箭"));

        assertTrue(inventory.consumeStackItem(player, "箭", eventListener::onEvent));
        event = eventListener.dequeue(UpdateInventorySlotEvent.class);
        assertEquals("", event.toPacket().getUpdateSlot().getName());
        assertFalse(event.toPacket().getUpdateSlot().hasNumber());
        assertFalse(inventory.contains("箭"));
    }
}