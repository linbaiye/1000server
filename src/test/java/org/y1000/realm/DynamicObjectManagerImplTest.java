package org.y1000.realm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.TestingEventListener;
import org.y1000.entities.Direction;
import org.y1000.entities.objects.*;
import org.y1000.entities.players.Damage;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.item.Item;
import org.y1000.sdb.*;
import org.y1000.util.Coordinate;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DynamicObjectManagerImplTest {

    private DynamicObjectManagerImpl manager;

    private DynamicObjectFactory factory;


    private EntityIdGenerator entityIdGenerator;

    private EntityEventSender entityEventSender;

    private RealmMap realmMap;

    private GroundItemManager itemManager;

    private CreateDynamicObjectSdb createDynamicObjectSdb;

    private CrossRealmEventSender eventHandler;


    @BeforeEach
    void setUp() {
        factory = Mockito.mock(DynamicObjectFactory.class);
        entityIdGenerator = new EntityIdGenerator();
        entityEventSender = Mockito.mock(EntityEventSender.class);
        itemManager = Mockito.mock(GroundItemManager.class);
        createDynamicObjectSdb = Mockito.mock(CreateDynamicObjectSdb.class);
        eventHandler = Mockito.mock(CrossRealmEventSender.class);
        realmMap = Mockito.mock(RealmMap.class);
        // manager = new DynamicObjectManagerImpl(factory, entityIdGenerator, entityEventSender, itemManager, createDynamicObjectSdb, eventHandler, realmMap);
    }



}