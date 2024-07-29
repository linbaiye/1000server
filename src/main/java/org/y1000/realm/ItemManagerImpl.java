package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.y1000.entities.GroundedItem;
import org.y1000.entities.RemoveEntityEvent;
import org.y1000.entities.creatures.event.CreatureDieEvent;
import org.y1000.entities.creatures.event.EntitySoundEvent;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.entities.players.Player;
import org.y1000.event.EntityEvent;
import org.y1000.event.item.ItemEventVisitor;
import org.y1000.item.Item;
import org.y1000.item.ItemFactory;
import org.y1000.item.ItemSdb;
import org.y1000.message.PlayerDropItemEvent;
import org.y1000.message.PlayerTextEvent;
import org.y1000.message.serverevent.UpdateInventorySlotEvent;
import org.y1000.sdb.MonstersSdb;
import org.y1000.util.Coordinate;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
final class ItemManagerImpl extends AbstractActiveEntityManager<GroundedItem> implements ItemEventVisitor, GroundItemManager {
    private final EntityEventSender eventSender;
    private final ItemSdb itemSdb;
    private final MonstersSdb monstersSdb;
    private final EntityIdGenerator idGenerator;

    private final ItemFactory itemFactory;


    private record DropItem(String name, int number, int rate) {
        public boolean canDrop() {
            return ThreadLocalRandom.current().nextInt(rate) == 0;
        }
    }
    private final Map<String, List<DropItem>> dropItems;


    public ItemManagerImpl(EntityEventSender eventSender,
                           ItemSdb itemSdb,
                           MonstersSdb monstersSdb,
                           EntityIdGenerator idGenerator,
                           ItemFactory itemFactory) {
        this.eventSender = eventSender;
        this.itemSdb = itemSdb;
        this.monstersSdb = monstersSdb;
        this.itemFactory = itemFactory;
        this.dropItems = new HashMap<>();
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
            visit(new RemoveEntityEvent(groundedItem));
            slotItem.eventSound().ifPresent(s -> picker.emitEvent(new EntitySoundEvent(picker, s)));
        }
    }

    @Override
    public void dropItem(String itemNumberRateArray, Coordinate at) {
        Validate.notNull(at);
        if (StringUtils.isEmpty(itemNumberRateArray)) {
            return;
        }
        String[] tokens = itemNumberRateArray.split(":");
        List<DropItem> dropItems = new ArrayList<>();
        for (int i = 0; i < tokens.length / 3; i++) {
            dropItems.add(new DropItem(tokens[i * 3], Integer.parseInt(tokens[i * 3 + 1]), Integer.parseInt(tokens[i * 3 + 2])));
        }
        for (DropItem dropItem : dropItems) {
            if (dropItem.canDrop()) {
                GroundedItem groundItem = createGroundItem(dropItem.name(), at, dropItem.number());
                insertNewItem(groundItem);
            }
        }
    }


    private List<DropItem> getFor(String name) {
        if (dropItems.containsKey(name)) {
            return dropItems.get(name);
        }
        String haveItem = monstersSdb.getHaveItem(name);
        if (haveItem == null) {
            dropItems.put(name, Collections.emptyList());
            return Collections.emptyList();
        }
        String[] tokens = haveItem.split(":");
        List<DropItem> result = new ArrayList<>();
        for (int i = 0; i < tokens.length / 3; i++) {
            result.add(new DropItem(tokens[i * 3], Integer.parseInt(tokens[i * 3 + 1]), Integer.parseInt(tokens[i * 3 + 2])));
        }
        dropItems.put(name, result);
        return result;
    }


    private GroundedItem createGroundItem(String name,
                                          Coordinate coordinate,
                                          int number) {
        return GroundedItem.builder()
                .id(idGenerator.next())
                .name(name)
                .number(number)
                .coordinate(coordinate)
                .pickSound(itemSdb.getSoundEvent(name))
                .dropSound(itemSdb.getSoundDrop(name))
                .build();
    }

    @Override
    protected Logger log() {
        return log;
    }

    @Override
    protected void onAdded(GroundedItem entity) {

    }

    @Override
    public void update(long delta) {
        updateManagedEntities(delta);
    }


    @Override
    protected void onDeleted(GroundedItem entity) {
    }

    @Override
    public void visit(RemoveEntityEvent event) {
        if (event.source() instanceof GroundedItem item) {
            eventSender.notifyVisiblePlayers(event.source(), event);
            eventSender.remove(event.source());
            remove(item);
        }
    }

    private void insertNewItem(GroundedItem item) {
        eventSender.add(item);
        eventSender.notifyVisiblePlayers(item, item.captureInterpolation());
        item.registerEventListener(this);
        add(item);
        item.dropSound().ifPresent(s -> eventSender.sendEvent(new EntitySoundEvent(item, s)));
    }

    @Override
    public void visit(PlayerDropItemEvent event) {
        GroundedItem groundedItem = event.createGroundedItem(idGenerator.next());
        insertNewItem(groundedItem);
        log.debug("Dropped item at {}", groundedItem.coordinate());
    }

    @Override
    public void visit(CreatureDieEvent event) {
        if (event.source() instanceof Npc npc) {
            List<DropItem> dropItems = getFor(npc.idName());
            for (DropItem dropItem : dropItems) {
                if (!dropItem.canDrop()) {
                    continue;
                }
                GroundedItem groundItem = createGroundItem(dropItem.name(), npc.coordinate(), dropItem.number());
                insertNewItem(groundItem);
            }
        }
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
