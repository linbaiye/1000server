package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.*;
import org.y1000.entities.EntitySoundEvent;
import org.y1000.item.Item;
import org.y1000.item.ItemFactory;
import org.y1000.item.ItemSdb;
import org.y1000.util.Coordinate;

@Slf4j
final class ItemManagerImpl extends AbstractActiveEntityManager<GroundItem> implements GroundItemManager {
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
        item.dropSound().ifPresent(s -> sendToVisiblePlayers(groundItem, new EntitySoundEvent(s)));
    }


    @Override
    protected Logger log() {
        return log;
    }


    @Override
    public void update(long delta) {
        updateManagedEntities(delta);
    }
}
