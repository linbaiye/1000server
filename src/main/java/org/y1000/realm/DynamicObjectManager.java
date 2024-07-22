package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.objects.DynamicObject;
import org.y1000.entities.objects.DynamicObjectFactory;
import org.y1000.event.EntityEvent;
import org.y1000.sdb.DynamicObjectSdb;

@Slf4j
public final class DynamicObjectManager extends AbstractEntityManager<DynamicObject> {

    private final DynamicObjectFactory factory;

    private final DynamicObjectSdb dynamicObjectSdb;

    public DynamicObjectManager(DynamicObjectFactory factory,
                                DynamicObjectSdb dynamicObjectSdb) {
        this.factory = factory;
        this.dynamicObjectSdb = dynamicObjectSdb;
    }


    @Override
    public void onEvent(EntityEvent entityEvent) {

    }

    @Override
    protected Logger log() {
        return log;
    }

    @Override
    protected void onAdded(DynamicObject entity) {

    }

    @Override
    protected void onDeleted(DynamicObject entity) {

    }

    @Override
    public void update(long delta) {

    }

    public void init(int id, RealmMap map) {


    }
}
