package org.y1000.entities.creatures.npc.AI;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@Slf4j
class MoverTest {


    private Direction direction;
    private Coordinate coordinate;
    private State state;
    private Npc npc;

    @BeforeEach
    void setUp() {
        npc = Mockito.mock(Npc.class);
        state = State.IDLE;
        when(npc.direction()).thenAnswer(invocationOnMock -> direction);
        when(npc.coordinate()).thenAnswer(invocationOnMock -> coordinate);
        when(npc.stateEnum()).thenAnswer(invocationOnMock -> state);
        doAnswer(invocationOnMock -> {
            state = State.WALK;
            coordinate = coordinate.moveBy(direction);
            return null;
        }).when(npc).move(anyInt());
        doAnswer(invocationOnMock -> {
            state = State.IDLE;
            return null;
        }).when(npc).stay(anyInt());
        doAnswer(invocationOnMock -> {
            direction = invocationOnMock.getArgument(0);
            return null;
        }).when(npc).changeDirection(any(Direction.class));
    }

    private RealmMap buildMap(int [][] mask) {
        RealmMap map = Mockito.mock(RealmMap.class);
        when(map.movable(any(Coordinate.class))).thenAnswer(invocationOnMock -> {
            Coordinate coordinate = invocationOnMock.getArgument(0);
            if (coordinate.x() < 0 || coordinate.x() >= mask[0].length || coordinate.y() < 0 || coordinate.y() >= mask.length) {
                return false;
            }
            return mask[coordinate.y()][coordinate.x()] == 0;
        });
        return map;
    }


    private void cantbe(Npc npc) {
        throw new RuntimeException();
    }

    @Test
    void walk() {
        int[][] mapmask = {
                {1,1,1,1,1,0}, // y:0
                {0,0,0,0,0,0}, // y:1
                {0,1,1,1,1,0}, // y:2
                {0,0,0,0,0,0}, // y:3
             //x:0,1,2,3,4,5
        };
        RealmMap map = buildMap(mapmask);
        coordinate = Coordinate.xy(2, 3);
        direction = Direction.UP;
        when(npc.getStateMillis(State.IDLE)).thenReturn(0);
        when(npc.getStateMillis(State.WALK)).thenReturn(200);
        when(npc.walkSpeed()).thenReturn(200);
        when(npc.realmMap()).thenReturn(map);
        Mover<Npc> mover = Mover.ofWalk(npc, Coordinate.xy(2, 1));
        mover.nextMove(this::cantbe);
        verify(npc, times(1)).move(200);;
        assertEquals(Direction.RIGHT, direction);
        assertEquals(Coordinate.xy(3,3), coordinate);
        mover.nextMove(this::cantbe);
        assertEquals(Coordinate.xy(4,3), coordinate);
        mover.nextMove(this::cantbe);
        assertEquals(Coordinate.xy(5,2), coordinate);
    }

    @Test
    void walkSpeed() {
        RealmMap map = Mockito.mock(RealmMap.class);
        when(map.movable(any(Coordinate.class))).thenReturn(true);
        when(npc.realmMap()).thenReturn(map);
        coordinate = Coordinate.xy(2, 3);
        direction = Direction.DOWN;
        when(npc.getStateMillis(State.IDLE)).thenReturn(200);
        when(npc.getStateMillis(State.WALK)).thenReturn(240);
        when(npc.walkSpeed()).thenReturn(500);
        Mover<Npc> mover = Mover.ofWalk(npc, Coordinate.xy(2, 10));
        mover.nextMove(this::cantbe);
        verify(npc, times(1)).move(240);;
        mover.nextMove(this::cantbe);
        verify(npc, times(1)).stay(260);;
        when(npc.getStateMillis(State.IDLE)).thenReturn(100);
        when(npc.getStateMillis(State.WALK)).thenReturn(100);
        when(npc.walkSpeed()).thenReturn(100);
        mover = Mover.ofWalk(npc, Coordinate.xy(2, 10));
        mover.nextMove(this::cantbe);
        verify(npc, times(1)).move(200);;
        mover.nextMove(this::cantbe);
        verify(npc, times(0)).stay(0);;
    }

    @Test
    void run() {
        RealmMap map = Mockito.mock(RealmMap.class);
        when(map.movable(any(Coordinate.class))).thenReturn(true);
        when(npc.realmMap()).thenReturn(map);
        coordinate = Coordinate.xy(2, 3);
        direction = Direction.DOWN;
        when(npc.getStateMillis(State.IDLE)).thenReturn(200);
        when(npc.getStateMillis(State.WALK)).thenReturn(210);
        when(npc.walkSpeed()).thenReturn(1000);
        Mover<Npc> mover = Mover.ofRun(npc, Coordinate.xy(2, 10));
        mover.run(this::cantbe);
        verify(npc, times(1)).move(210);
        mover.run(this::cantbe);
        verify(npc, times(1)).stay(290);
    }
}