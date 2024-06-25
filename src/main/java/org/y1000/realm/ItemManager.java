package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.GroundedItem;
import org.y1000.entities.RemoveEntityEvent;
import org.y1000.entities.creatures.event.CreatureDieEvent;
import org.y1000.entities.creatures.event.EntitySoundEvent;
import org.y1000.entities.creatures.monster.AbstractMonster;
import org.y1000.event.EntityEvent;
import org.y1000.event.EntityEventListener;
import org.y1000.item.ItemSdb;
import org.y1000.message.PlayerDropItemEvent;
import org.y1000.message.serverevent.*;
import org.y1000.sdb.MonstersSdb;
import org.y1000.util.Coordinate;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
final class ItemManager extends AbstractEntityManager<GroundedItem> implements EntityEventListener,
        PlayerEventVisitor {
    private final EntityEventSender eventSender;
    private final ItemSdb itemSdb;
    private final MonstersSdb monstersSdb;
    private final EntityIdGenerator idGenerator;
    private record DropItem(String name, int count, int rate) {
        public boolean canDrop() {
            return ThreadLocalRandom.current().nextInt(rate) == 0;
        }
    }
    private final Map<String, List<DropItem>> dropItems;

    public ItemManager(EntityEventSender eventSender,
                       ItemSdb itemSdb,
                       MonstersSdb monstersSdb,
                       EntityIdGenerator idGenerator) {
        this.eventSender = eventSender;
        this.itemSdb = itemSdb;
        this.monstersSdb = monstersSdb;
        this.dropItems = new HashMap<>();
        this.idGenerator = idGenerator;
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
    public void update(long delta) {
        updateManagedEntities(delta);
    }

    @Override
    protected void onAdded(GroundedItem entity) {
        entity.registerEventListener(this);
        eventSender.add(entity);
        eventSender.sendEvent(new ShowItemEvent(entity));
        entity.dropSound().ifPresent(s -> eventSender.sendEvent(new EntitySoundEvent(entity, s)));
    }

    @Override
    protected void onDeleted(GroundedItem entity) {
        eventSender.remove(entity);
    }

    @Override
    public void visit(RemoveEntityEvent event) {
        if (event.source() instanceof GroundedItem item) {
            delete(item);
        }
    }

    @Override
    public void visit(PlayerDropItemEvent event) {
        GroundedItem groundedItem = event.createGroundedItem(idGenerator.next());
        add(groundedItem);
    }

    @Override
    public void visit(CreatureDieEvent event) {
        if (event.source() instanceof AbstractMonster monster) {
            List<DropItem> dropItems = getFor(monster.name());
            for (DropItem dropItem : dropItems) {
                if (!dropItem.canDrop()) {
                    continue;
                }
                add(createGroundItem(dropItem.name(), monster.coordinate(), dropItem.count()));
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
