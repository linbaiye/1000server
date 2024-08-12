package org.y1000.entities.objects;


import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.TestingEventListener;
import org.y1000.entities.players.Player;
import org.y1000.realm.RealmMap;
import org.y1000.sdb.DynamicObjectSdb;

import java.util.Optional;

import static org.mockito.Mockito.when;

public abstract class AbstractObjectUnitTestFixture extends AbstractUnitTestFixture {


    protected DynamicObjectSdb dynamicObjectSdb;

    protected final String idName = "test";

    protected RealmMap realmMap;

    protected TestingEventListener eventListener;

    protected void setup() {
        dynamicObjectSdb = Mockito.mock(DynamicObjectSdb.class);
        realmMap = Mockito.mock(RealmMap.class);
        when(dynamicObjectSdb.getShape(idName)).thenReturn("shape");
        when(dynamicObjectSdb.getSStep0(idName)).thenReturn("0");
        when(dynamicObjectSdb.getEStep0(idName)).thenReturn("0");
        when(dynamicObjectSdb.getSStep1(idName)).thenReturn("1");
        when(dynamicObjectSdb.getEStep1(idName)).thenReturn("4");
        when(dynamicObjectSdb.getSStep2(idName)).thenReturn("5");
        when(dynamicObjectSdb.getEStep2(idName)).thenReturn("5");
        when(dynamicObjectSdb.getViewName(idName)).thenReturn(Optional.of("testView"));
        when(dynamicObjectSdb.getArmor(idName)).thenReturn(0);
        when(dynamicObjectSdb.getLife(idName)).thenReturn(1);
        when(dynamicObjectSdb.getOpenedMillis(idName)).thenReturn(400);
        when(dynamicObjectSdb.getSoundEvent(idName)).thenReturn(Optional.of("eventSound"));
        when(dynamicObjectSdb.getSoundSpecial(idName)).thenReturn(Optional.of("specialSound"));
        eventListener = new TestingEventListener();
    }
}

