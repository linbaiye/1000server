package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.y1000.entities.GroundedItem;
import org.y1000.entities.RemoveEntityEvent;
import org.y1000.entities.creatures.event.EntitySoundEvent;
import org.y1000.entities.players.Player;
import org.y1000.event.EntityEvent;
import org.y1000.event.item.ItemEventVisitor;
import org.y1000.item.Item;
import org.y1000.item.ItemFactory;
import org.y1000.item.ItemSdb;
import org.y1000.message.PlayerDropItemEvent;
import org.y1000.message.PlayerTextEvent;
import org.y1000.message.serverevent.UpdateInventorySlotEvent;
import org.y1000.util.Coordinate;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
final class ItemManagerImpl extends AbstractActiveEntityManager<GroundedItem> implements ItemEventVisitor, GroundItemManager {
    private final EntityEventSender eventSender;
    private final ItemSdb itemSdb;
    private final EntityIdGenerator idGenerator;

    private final ItemFactory itemFactory;

    private record DropItem(String name, int number, int rate) {
        public boolean canDrop() {
            return ThreadLocalRandom.current().nextInt(rate) == 0;
        }
    }

    public ItemManagerImpl(EntityEventSender eventSender,
                           ItemSdb itemSdb,
                           EntityIdGenerator idGenerator,
                           ItemFactory itemFactory) {
        this.eventSender = eventSender;
        this.itemSdb = itemSdb;
        this.itemFactory = itemFactory;
        this.idGenerator = idGenerator;
    }


    @Override
    public void pickItem(Player picker, long id) {
        Validate.notNull(picker);
        GroundedItem groundedItem = find(id).orElse(null);
        if (groundedItem == null) {
            return;
        }
        if (!groundedItem.canPickAt(picker.coordinate())) {
            picker.emitEvent(PlayerTextEvent.tooFarAway(picker));
            return;
        }
        if (!picker.inventory().canPick(groundedItem)) {
            picker.emitEvent(PlayerTextEvent.inventoryFull(picker));
            return;
        }
        Item slotItem = itemFactory.createItem(groundedItem);
        int slot = picker.inventory().add(slotItem);
        if (slot > 0) {
            picker.emitEvent(new UpdateInventorySlotEvent(picker, slot, picker.inventory().getItem(slot)));
            picker.emitEvent(PlayerTextEvent.pickedItem(picker, groundedItem.getName(), groundedItem.getNumber()));
            visit(new RemoveEntityEvent(groundedItem));
            slotItem.eventSound().ifPresent(s -> picker.emitEvent(new EntitySoundEvent(picker, s)));
        }
    }

    @Override
    public void dropItem(String itemNumberRateArray, Coordinate at) {
        if (StringUtils.isEmpty(itemNumberRateArray)) {
            return;
        }
        Validate.notNull(at);
        String[] tokens = itemNumberRateArray.split(":");
        List<DropItem> dropItems = new ArrayList<>();
        for (int i = 0; i < tokens.length / 3; i++) {
            dropItems.add(new DropItem(tokens[i * 3], Integer.parseInt(tokens[i * 3 + 1]), Integer.parseInt(tokens[i * 3 + 2])));
        }
        for (DropItem dropItem : dropItems) {
            if (dropItem.canDrop()) {
                GroundedItem groundItem = createGroundItem(dropItem.name(), at, dropItem.number());
                dropNewItem(groundItem);
            }
        }
    }

    @Override
    public void dropItem(String name, int number, Coordinate at) {
        Validate.notNull(name);
        Validate.notNull(at);
        dropNewItem(createGroundItem(name, at, number));
    }

    @Override
    public void dropItem(PlayerDropItemEvent dropItemEvent) {
        if (dropItemEvent == null)
            return;
        GroundedItem groundedItem = dropItemEvent.createGroundedItem(idGenerator.next());
        dropNewItem(groundedItem);
        log.debug("Dropped item at {}", groundedItem.coordinate());

    }


    private GroundedItem createGroundItem(String name,
                                          Coordinate coordinate,
                                          int number) {
        return GroundedItem.builder()
                .id(idGenerator.next())
                .name(name)
                .number(number)
                .coordinate(coordinate)
                .color(itemSdb.getColor(name))
                .dropSound(itemSdb.getSoundDrop(name))
                .build();
    }

    @Override
    protected Logger log() {
        return log;
    }


    @Override
    public void update(long delta) {
        updateManagedEntities(delta);
    }

    @Override
    public void visit(RemoveEntityEvent event) {
        if (event.source() instanceof GroundedItem item) {
            eventSender.notifyVisiblePlayers(event.source(), event);
            eventSender.remove(event.source());
            remove(item);
        }
    }

    private void dropNewItem(GroundedItem item) {
        eventSender.add(item);
        eventSender.notifyVisiblePlayers(item, item.captureInterpolation());
        item.registerEventListener(this);
        add(item);
        item.dropSound().ifPresent(s -> eventSender.sendEvent(new EntitySoundEvent(item, s)));
    }

    @Override
    public void onEvent(EntityEvent entityEvent) {
        try {
            entityEvent.accept(this);
        } catch (Exception e) {
            log.error("Failed to handle event, ", e);
        }
    }
}
