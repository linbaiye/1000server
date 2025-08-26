package org.y1000.entities.players;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.entities.players.event.PlayerSoundEvent;
import org.y1000.entities.players.event.PlayerAttributeMessage;
import org.y1000.entities.players.event.PlayerTextMessage;
import org.y1000.item.ItemFactory;
import org.y1000.item.Pill;
import org.y1000.item.StackItem;

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
        slots.tryUsePill(player, pill);
        Mockito.verify(player, Mockito.times(1)).sendEvent(any(PlayerSoundEvent.class));
        Mockito.verify(player, Mockito.times(1)).sendEvent(any(PlayerTextMessage.class));
        slots.tryUsePill(player, pill);
        Mockito.verify(player, Mockito.times(2)).sendEvent(any(PlayerSoundEvent.class));
        Mockito.verify(player, Mockito.times(2)).sendEvent(any(PlayerTextMessage.class));
        slots.tryUsePill(player, pill);
        Mockito.verify(player, Mockito.times(3)).sendEvent(any(PlayerSoundEvent.class));
        Mockito.verify(player, Mockito.times(3)).sendEvent(any(PlayerTextMessage.class));
        slots.tryUsePill(player, pill);
        Mockito.verify(player, Mockito.times(3)).sendEvent(any(PlayerSoundEvent.class));
        Mockito.verify(player, Mockito.times(3)).sendEvent(any(PlayerTextMessage.class));
    }

    @Test
    void update() {
        StackItem stackItem= (StackItem) itemFactory.createItem("生药", 100);
        var pill = (Pill) stackItem.item();
        slots.tryUsePill(player,  pill);
        slots.update(player, pill.useInterval());
        Mockito.verify(player, Mockito.times(1)).sendEvent(any(PlayerAttributeMessage.class));
        for (int i = 0; i < pill.useCount(); i++) {
            slots.update(player, pill.useInterval());
        }
        Mockito.verify(player, Mockito.times(pill.useCount())).sendEvent(any(PlayerAttributeMessage.class));

        // make sure ropeSlot is emptied.
        Mockito.reset(player);
        slots.tryUsePill(player, pill);
        slots.tryUsePill(player, pill);
        slots.tryUsePill(player, pill);
        Mockito.verify(player, Mockito.times(3)).sendEvent(any(PlayerSoundEvent.class));
        Mockito.verify(player, Mockito.times(3)).sendEvent(any(PlayerTextMessage.class));
    }

    @Test
    void canTake() {
        StackItem stackItem= (StackItem) itemFactory.createItem("生药", 100);
        var pill = (Pill) stackItem.item();
        slots.tryUsePill(player, pill);
        slots.tryUsePill(player, pill);
        assertTrue(slots.canTakePill());
        slots.tryUsePill(player, pill);
        assertFalse(slots.canTakePill());
    }
}