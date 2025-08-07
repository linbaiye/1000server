package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.*;
import org.y1000.entities.creatures.event.EntitySoundEvent;
import org.y1000.entities.players.Player;
import org.y1000.event.TypedEntityEvent;
import org.y1000.event.IEntityEvent;
import org.y1000.event.item.ItemEventVisitor;
import org.y1000.item.Item;
import org.y1000.item.ItemFactory;
import org.y1000.item.ItemSdb;
import org.y1000.util.Coordinate;

@Slf4j
final class ItemManagerImpl extends AbstractActiveEntityManager<GroundItem> implements ItemEventVisitor, GroundItemManager {
    private final ItemSdb itemSdb;
    private final EntityIdGenerator idGenerator;
    private final ItemFactory itemFactory;


    public ItemManagerImpl(MessageSender eventSender,
                           ItemSdb itemSdb,
                           EntityIdGenerator idGenerator,
                           ItemFactory itemFactory,
                           AOIManager aoiManager) {
        super(aoiManager, eventSender);
        this.itemSdb = itemSdb;
        this.itemFactory = itemFactory;
        this.idGenerator = idGenerator;
    }


    private void removeItem(GroundItem item) {
        sendToVisiblePlayers(item, GroundItemRemoveEvent.of(item));
        remove(item);
    }

    @Override
    public void pickItem(Player picker, long id) {
//        Validate.notNull(picker);
//        GroundedItem groundedItem = find(id).orElse(null);
//        if (groundedItem == null) {
//            return;
//        }
//        if (!groundedItem.canPickAt(picker.coordinate())) {
//            picker.emitEvent(PlayerTextEvent.tooFarAway(picker));
//            return;
//        }
//        if (!picker.inventory().canPick(groundedItem)) {
//            picker.emitEvent(PlayerTextEvent.inventoryFull(picker));
//            return;
//        }
//        Item slotItem = itemFactory.createItem(groundedItem);
//        int slot = picker.inventory().add(slotItem);
//        if (slot > 0) {
//            picker.emitEvent(new UpdateInventorySlotEvent(picker, slot, picker.inventory().getItem(slot)));
//            picker.emitEvent(PlayerTextEvent.pickedItem(picker, groundedItem.getName(), groundedItem.getNumber()));
//            visit(new RemoveEntityEvent(groundedItem));
//            slotItem.eventSound().ifPresent(s -> picker.emitEvent(new EntitySoundEvent(picker, s)));
//        }
    }

    @Override
    public void dropItem(String itemNumberRateArray, Coordinate at) {
//        if (StringUtils.isEmpty(itemNumberRateArray)) {
//            return;
//        }
//        Validate.notNull(at);
//        String[] tokens = itemNumberRateArray.split(":");
//        List<DropItem> dropItems = new ArrayList<>();
//        for (int i = 0; i < tokens.length / 3; i++) {
//            dropItems.add(new DropItem(tokens[i * 3], Integer.parseInt(tokens[i * 3 + 1]), Integer.parseInt(tokens[i * 3 + 2])));
//        }
//        for (DropItem dropItem : dropItems) {
//            if (dropItem.canDrop()) {
//                GroundedItem groundItem = createGroundItem(dropItem.name(), at, dropItem.number());
//                dropNewItem(groundItem);
//            }
//        }
    }

    @Override
    public void dropItem(String name, int number, Coordinate at) {
        Item item = itemFactory.createItem(name, number);
        dropItem(item, at);
    }

    @Override
    public void dropItem(Item item, Coordinate droppedAt) {
        if (item == null)
            return;
        GroundItem groundItem = new GroundItem(idGenerator.next(), item, droppedAt, this::removeItem);
        add(groundItem);
        sendToVisiblePlayers(groundItem, groundItem.captureSnapshot());
        item.dropSound().ifPresent(s -> sendToVisiblePlayers(groundItem, new EntitySoundEvent(groundItem, s)));
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
    public void onEvent(TypedEntityEvent entityEvent) {
        try {
            if (entityEvent instanceof IEntityEvent iEntityEvent)
                iEntityEvent.accept(this);
        } catch (Exception e) {
            log.error("Failed to handle event, ", e);
        }
    }
}
