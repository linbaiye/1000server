package org.y1000.entities.players.inventory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.players.Player;
import org.y1000.item.Weapon;
import org.y1000.kungfu.attack.AttackKungFuType;
import org.y1000.message.clientevent.ClientDropItemEvent;
import org.y1000.message.serverevent.EntityEvent;
import org.y1000.util.Coordinate;
import org.y1000.util.UnaryAction;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    private ClientDropItemEvent createDropEvent(int slot, int number) {
        return new ClientDropItemEvent(number, slot, 1, 1, new Coordinate(2, 2));
    }

    @Test
    void handleDropWeaponEvent() {

    }

    @Test
    void canPickNonStack() {

    }

    @Test
    void canPickStack() {

    }
}