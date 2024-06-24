package org.y1000.realm;

import org.y1000.entities.GroundedItem;
import org.y1000.entities.creatures.event.CreatureDieEvent;
import org.y1000.entities.creatures.monster.AbstractMonster;
import org.y1000.item.ItemSdb;
import org.y1000.message.serverevent.EntityEvent;
import org.y1000.message.serverevent.EntityEventListener;

class ItemManager implements EntityEventListener {
    private final RealmEntityManager entityManager;
    private final ItemSdb itemSdb;

    public ItemManager(RealmEntityManager entityManager, ItemSdb itemSdb) {
        this.entityManager = entityManager;
        this.itemSdb = itemSdb;
    }


    private GroundedItem createGroundItem() {

    }


    @Override
    public void onEvent(EntityEvent entityEvent) {
        if (entityEvent instanceof CreatureDieEvent && entityEvent.source()
                instanceof AbstractMonster monster && monster.name().equals("牛")) {

            entityManager.showItem(new GroundedItem(entityManager.generateEntityId(), "肉", monster.coordinate(), 0, 0, 4));
            entityManager.showItem(new GroundedItem(entityManager.generateEntityId(), "皮", monster.coordinate(), 0, 0, 4));
        }
    }
}
