package org.y1000.entities.creatures;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.monster.AbstractMonsterUnitTestFixture;
import org.y1000.entities.creatures.npc.AI.AiPathUtil;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;


import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;


@Slf4j
class AiPathUtilTest extends AbstractMonsterUnitTestFixture {

    private AtomicReference<Coordinate> previous = new AtomicReference<>();

    private AtomicReference<Coordinate> npcCoordinate = new AtomicReference<>();

    private AtomicReference<Direction> npcDirection = new AtomicReference<>();


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

    @Test
    void moveProcess() {
        int[][] mapmask = {
            {1,1,1,1}, // y:0
            {0,0,1,1}, // y:1
            {0,1,1,1}, // y:2
            {0,0,0,0}, // y:3
         //x:0,1,2,3
        };
        RealmMap map = buildMap(mapmask);
        Npc npc = Mockito.mock(Npc.class);
        previous.set(Coordinate.xy(0, 0));
        npcCoordinate.set(Coordinate.xy(0, 3));
        npcDirection.set(Direction.RIGHT);
        when(npc.coordinate()).thenAnswer(invocationOnMock -> npcCoordinate.get());
        when(npc.realmMap()).thenReturn(map);
        when(npc.direction()).thenAnswer(invocationOnMock -> npcDirection.get());

        Mockito.doAnswer(invocationOnMock -> {
            npcDirection.set(invocationOnMock.getArgument(0));
            log.debug("Change direction to {}.",npcDirection.get());
            return null;
        }).when(npc).changeDirection(any(Direction.class));

        Mockito.doAnswer(invocationOnMock -> {
            previous.set(npcCoordinate.get());
            npcCoordinate.set(npcCoordinate.get().moveBy(npcDirection.get()));
            log.debug("Move by direction to {}.", npcCoordinate.get());
            return null;
        }).when(npc).move(anyInt());
        for (int i = 0; i < 5; i++) {
            AiPathUtil.moveProcess(npc, Coordinate.xy(2, 1), previous.get(), () -> log.debug("Nopath"), 10, 10);
        }
    }

    @Test
    void computeNextMoveDirection() {
        int[][] mapmask = {
                {1,1,1,1,1,0}, // y:0
                {0,0,0,0,0,0}, // y:1
                {0,1,1,1,1,0}, // y:2
                {0,0,0,0,0,0}, // y:3
             //x:0,1,2,3,4,5
        };
        RealmMap map =buildMap(mapmask);
        for (int i = 0; i <5; i++) {
            for (int j = 0; j < 4; j++) {
                System.out.println(Coordinate.xy(i, j) + ": " + map.movable(Coordinate.xy(i, j)));
            }
        }
        Npc npc = Mockito.mock(Npc.class);
        when(npc.realmMap()).thenReturn(map);
        when(npc.direction()).thenReturn(Direction.UP);
        when(npc.coordinate()).thenReturn(Coordinate.xy(3,3));
        Direction direction = AiPathUtil.computeNextMoveDirection(npc, Coordinate.xy(3, 1), Coordinate.Empty);
        assertEquals(Direction.RIGHT, direction);
    }

    @Test
    void computeEscapePoint() {
    }
}