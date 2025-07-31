package org.y1000.realm;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.y1000.entities.objects.*;
import org.y1000.sdb.*;

import static org.mockito.Mockito.verify;

class DynamicObjectManagerImplTest {

    private DynamicObjectManagerImpl manager;

    private DynamicObjectFactory factory;


    private EntityIdGenerator entityIdGenerator;

    private EntityEventSender entityEventSender;

    private RealmMap realmMap;

    private GroundItemManager itemManager;

    private CreateDynamicObjectSdb createDynamicObjectSdb;

    private RealmEventSender eventHandler;


    @BeforeEach
    void setUp() {
        factory = Mockito.mock(DynamicObjectFactory.class);
        entityIdGenerator = new EntityIdGenerator();
        entityEventSender = Mockito.mock(EntityEventSender.class);
        itemManager = Mockito.mock(GroundItemManager.class);
        createDynamicObjectSdb = Mockito.mock(CreateDynamicObjectSdb.class);
        eventHandler = Mockito.mock(RealmEventSender.class);
        realmMap = Mockito.mock(RealmMap.class);
        // manager = new DynamicObjectManagerImpl(factory, entityIdGenerator, entityEventSender, itemManager, createDynamicObjectSdb, eventHandler, realmMap);
    }



}