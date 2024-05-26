package org.y1000.entities.players.inventory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.players.EventEmiter;
import org.y1000.entities.players.Player;
import org.y1000.message.clientevent.ClientDropItemEvent;
import org.y1000.message.serverevent.EntityEvent;
import org.y1000.util.Coordinate;

class InventoryTest {

    private Inventory inventory;

    private Player player;

    private EventEmiter eventEmiter;

    private static class EventSaver implements EventEmiter {
        private EntityEvent entityEvent;

        @Override
        public void emitEvent(EntityEvent event) {
            entityEvent = event;
        }

        public EntityEvent entityEvent() {
            return entityEvent;
        }
    }

    @BeforeEach
    void setUp() {
        inventory = new Inventory();
        player = Mockito.mock(Player.class);
        eventEmiter = new EventSaver();
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