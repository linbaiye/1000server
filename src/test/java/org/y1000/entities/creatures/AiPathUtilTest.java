package org.y1000.entities.creatures;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.monster.AbstractMonsterUnitTestFixture;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;


import java.util.concurrent.atomic.AtomicReference;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;


@Slf4j
class AiPathUtilTest extends AbstractMonsterUnitTestFixture {

    private AtomicReference<Coordinate> previous = new AtomicReference<>();

    private AtomicReference<Coordinate> npcCoordinate = new AtomicReference<>();

    private AtomicReference<Direction> npcDirection = new AtomicReference<>();

    @Test
    void moveProcess() {
        int[][] mapmask = {
            {1,1,1,1}, // y:0
            {0,0,1,1}, // y:1
            {0,1,1,1}, // y:2
            {0,0,0,0}, // y:3
         //x:0,1,2,3
        };
        RealmMap map = Mockito.mock(RealmMap.class);
        when(map.movable(any(Coordinate.class))).thenAnswer(invocationOnMock -> {
            Coordinate coordinate = invocationOnMock.getArgument(0);
            if (coordinate.x() < 0 || coordinate.x() > 3 || coordinate.y() < 0 || coordinate.y() > 3) {
                return false;
            }
            return mapmask[coordinate.y()][coordinate.x()] == 0;
        });
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
}