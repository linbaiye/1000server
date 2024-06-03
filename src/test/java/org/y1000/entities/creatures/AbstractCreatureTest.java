package org.y1000.entities.creatures;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.monster.PassiveMonster;
import org.y1000.message.MoveEvent;
import org.y1000.message.serverevent.EntityEvent;
import org.y1000.message.serverevent.EntityEventListener;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.EventListener;

import static org.junit.jupiter.api.Assertions.*;

class AbstractCreatureTest {

    private PassiveMonster monster1;
    private static class TestingEventListener implements EntityEventListener  {
        public EntityEvent receivedEvent;
        @Override
        public void OnEvent(EntityEvent entityEvent) {
            receivedEvent = entityEvent;
        }
    }
    private static class TestingCountEventListener implements EntityEventListener {
        public int count = 0;
        @Override
        public void OnEvent(EntityEvent entityEvent) {
            count++;
        }
    }

    @BeforeEach
    void setUp() {
        monster1 = new PassiveMonster(1L, new Coordinate(1, 2), Direction.DOWN, "test", Mockito.mock(RealmMap.class));
    }

    @Test
    void registerOrderedEventListener() {
        TestingEventListener listener1 = new TestingEventListener();
        TestingEventListener listener2 = new TestingEventListener();
        monster1.registerOrderedEventListener(listener1);
        monster1.registerOrderedEventListener(listener2);
        MoveEvent event = new MoveEvent(monster1, Direction.DOWN, monster1.coordinate());
        monster1.emitEvent(event);
        assertSame(event, listener1.receivedEvent);
        assertSame(event, listener2.receivedEvent);
        TestingCountEventListener testingCountEventListener = new TestingCountEventListener();
        monster1.registerOrderedEventListener(testingCountEventListener);
        monster1.registerOrderedEventListener(testingCountEventListener);
        monster1.emitEvent(event);
        assertEquals(1, testingCountEventListener.count);
    }

    @Test
    void deregisterEventListener() {
        TestingEventListener listener1 = new TestingEventListener();
        TestingEventListener listener2 = new TestingEventListener();
        monster1.registerOrderedEventListener(listener1);
        monster1.registerOrderedEventListener(listener2);
        monster1.deregisterEventListener(listener2);
        monster1.deregisterEventListener(listener2);
        MoveEvent event = new MoveEvent(monster1, Direction.DOWN, monster1.coordinate());
        monster1.emitEvent(event);
        assertSame(event, listener1.receivedEvent);
        assertNull(listener2.receivedEvent);
    }
}