package org.y1000.entities.players;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.TestingEventListener;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.State;
import org.y1000.message.BreakRopeEvent;
import org.y1000.message.PlayerDraggedEvent;
import org.y1000.message.PositionType;
import org.y1000.message.serverevent.PlayerLeftEvent;
import org.y1000.network.gen.PositionPacket;
import org.y1000.realm.Realm;
import org.y1000.util.Coordinate;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class RopeTest extends AbstractPlayerUnitTestFixture {

    private Player dragger;
    private Player dragged;

    private Realm realm;

    private TestingEventListener draggerListener;

    private TestingEventListener draggedListener;

    private Player killer;

    @BeforeEach
    void setUp() {
        realm = mockAllFlatRealm();
        dragger = playerBuilder().build();
        dragger.joinRealm(realm);
        draggerListener = new TestingEventListener();
        dragger.registerEventListener(draggerListener);

        dragged = playerBuilder().build();
        dragged.joinRealm(realm);
        killer = playerBuilder().coordinate(dragged.coordinate().moveBy(Direction.RIGHT)).build();
        while (dragged.stateEnum() != State.DIE) {
            dragged.attackedBy(killer);
        }
        draggedListener = new TestingEventListener();
        dragged.registerEventListener(draggedListener);
    }

    @Test
    void drag() {
        dragger.changeCoordinate(Coordinate.xy(1, 1));
        dragged.changeCoordinate(Coordinate.xy(1, 4));
        Rope rope = new Rope(dragged, dragger);
        rope.update(Realm.STEP_MILLIS);
        assertEquals(Direction.UP, dragged.direction());
        PositionPacket positionPacket = draggedListener.removeFirst(PlayerDraggedEvent.class).toPacket().getPositionPacket();
        assertEquals(PositionType.DRAGGED.value(), positionPacket.getType());
        rope.update(Realm.STEP_MILLIS);
        assertEquals(Coordinate.xy(1, 3), dragged.coordinate());
        positionPacket = draggedListener.removeFirst(PlayerDraggedEvent.class).toPacket().getPositionPacket();
        assertEquals(PositionType.DRAGGED.value(), positionPacket.getType());
        rope.update(Realm.STEP_MILLIS);
        assertEquals(Coordinate.xy(1, 2), dragged.coordinate());
        positionPacket = draggedListener.removeFirst(PlayerDraggedEvent.class).toPacket().getPositionPacket();
        assertEquals(PositionType.DRAGGED.value(), positionPacket.getType());
        draggedListener.clearEvents();
        rope.update(Realm.STEP_MILLIS);
        assertEquals(Coordinate.xy(1, 2), dragged.coordinate());
        assertTrue(draggedListener.isEmpty());
    }

    @Test
    void breakIfAnyLeft() {
        dragger.changeCoordinate(Coordinate.xy(1, 1));
        dragged.changeCoordinate(Coordinate.xy(1, 3));
        Rope rope = new Rope(dragged, dragger);
        assertFalse(rope.isBroken());
        rope.onEvent(new PlayerLeftEvent(dragged));
        assertTrue(rope.isBroken());
        assertNotNull(draggedListener.removeFirst(BreakRopeEvent.class));

        dragger.changeCoordinate(Coordinate.xy(1, 1));
        dragged.changeCoordinate(Coordinate.xy(1, 3));
        rope = new Rope(dragged, dragger);
        rope.onEvent(new PlayerLeftEvent(dragger));
        assertTrue(rope.isBroken());
        assertNotNull(draggedListener.removeFirst(BreakRopeEvent.class));
    }

    @Test
    void breakIfDraggedAgain() {
        dragger.changeCoordinate(Coordinate.xy(1, 1));
        dragged.changeCoordinate(Coordinate.xy(1, 3));
        Rope rope = new Rope(dragged, dragger);
        rope.breakIfDraggedAgain(dragged);
        assertNotNull(draggedListener.removeFirst(BreakRopeEvent.class));
    }
}