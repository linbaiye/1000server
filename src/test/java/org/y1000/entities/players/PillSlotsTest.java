package org.y1000.entities.players;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.entities.creatures.event.EntitySoundEvent;
import org.y1000.entities.players.event.PlayerAttributeEvent;
import org.y1000.item.ItemFactory;
import org.y1000.item.ItemSdbImpl;
import org.y1000.item.Pill;
import org.y1000.item.StackItem;
import org.y1000.kungfu.KungFuSdb;
import org.y1000.message.PlayerTextEvent;
import org.y1000.repository.ItemRepositoryImpl;
import org.y1000.repository.KungFuBookRepositoryImpl;
import org.y1000.sdb.ItemDrugSdbImpl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

class PillSlotsTest extends AbstractUnitTestFixture {
    private PillSlots slots;

    private Player player;

    private ItemFactory itemFactory = createItemFactory();

    @BeforeEach
    void setUp() {
        player = Mockito.mock(Player.class);
        slots = new PillSlots();
    }



    @Test
    void usePill() {
        StackItem stackItem= (StackItem) itemFactory.createItem("生药", 100);
        var pill = (Pill) stackItem.item();
        slots.usePill(player, pill);
        Mockito.verify(player, Mockito.times(1)).emitEvent(any(EntitySoundEvent.class));
        Mockito.verify(player, Mockito.times(1)).emitEvent(any(PlayerTextEvent.class));
        slots.usePill(player, pill);
        Mockito.verify(player, Mockito.times(2)).emitEvent(any(EntitySoundEvent.class));
        Mockito.verify(player, Mockito.times(2)).emitEvent(any(PlayerTextEvent.class));
        slots.usePill(player, pill);
        Mockito.verify(player, Mockito.times(3)).emitEvent(any(EntitySoundEvent.class));
        Mockito.verify(player, Mockito.times(3)).emitEvent(any(PlayerTextEvent.class));
        slots.usePill(player, pill);
        Mockito.verify(player, Mockito.times(3)).emitEvent(any(EntitySoundEvent.class));
        Mockito.verify(player, Mockito.times(3)).emitEvent(any(PlayerTextEvent.class));
    }

    @Test
    void update() {
        StackItem stackItem= (StackItem) itemFactory.createItem("生药", 100);
        var pill = (Pill) stackItem.item();
        slots.usePill(player,  pill);
        slots.update(player, pill.useInterval());
        Mockito.verify(player, Mockito.times(1)).emitEvent(any(PlayerAttributeEvent.class));
        for (int i = 0; i < pill.useCount(); i++) {
            slots.update(player, pill.useInterval());
        }
        Mockito.verify(player, Mockito.times(pill.useCount())).emitEvent(any(PlayerAttributeEvent.class));

        // make sure ropeSlot is emptied.
        Mockito.reset(player);
        slots.usePill(player, pill);
        slots.usePill(player, pill);
        slots.usePill(player, pill);
        Mockito.verify(player, Mockito.times(3)).emitEvent(any(EntitySoundEvent.class));
        Mockito.verify(player, Mockito.times(3)).emitEvent(any(PlayerTextEvent.class));
    }

    @Test
    void canTake() {
        StackItem stackItem= (StackItem) itemFactory.createItem("生药", 100);
        var pill = (Pill) stackItem.item();
        slots.usePill(player, pill);
        slots.usePill(player, pill);
        assertTrue(slots.canTakePill());
        slots.usePill(player, pill);
        assertFalse(slots.canTakePill());
    }
}