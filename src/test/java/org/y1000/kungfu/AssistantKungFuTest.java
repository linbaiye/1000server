package org.y1000.kungfu;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.entities.Direction;
import org.y1000.entities.players.Damage;
import org.y1000.util.Coordinate;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AssistantKungFuTest extends AbstractUnitTestFixture  {

    private AssistantKungFu assistantKungFu;

    @BeforeEach
    void setUp() {
        create(true);
    }

    private void create(boolean eight) {
        assistantKungFu = AssistantKungFu.builder().name("5").exp(0).eightDirection(eight).build();
    }

    @Test
    void testEight() {
        var player = playerBuilder().coordinate(Coordinate.xy(2, 2)).build();
        player.changeDirection(Direction.UP);
        Set<Coordinate> coordinates = assistantKungFu.affectedCoordinates(player);
        List<Coordinate> affected = List.of(Coordinate.xy(2,3), Coordinate.xy(1, 1), Coordinate.xy(1,2), Coordinate.xy(1, 3)
        , Coordinate.xy(3, 1), Coordinate.xy(3, 2), Coordinate.xy(3, 3));
        assertTrue(coordinates.containsAll(affected));
        assertEquals(coordinates.size(), affected.size());
    }


    @Test
    void testFive() {
        var player = playerBuilder().coordinate(Coordinate.xy(2, 2)).build();
        player.changeDirection(Direction.UP_LEFT);
        create(false);
        Set<Coordinate> coordinates = assistantKungFu.affectedCoordinates(player);
        assertEquals(4, coordinates.size());
        // 1,1
        assertTrue(coordinates.containsAll(List.of(Coordinate.xy(1, 2), Coordinate.xy(1, 3), Coordinate.xy(2,1), Coordinate.xy(3, 1))));
        // 3,1
        player.changeDirection(Direction.UP_RIGHT);
        coordinates = assistantKungFu.affectedCoordinates(player);
        assertTrue(coordinates.containsAll(List.of(Coordinate.xy(1, 1), Coordinate.xy(2, 1), Coordinate.xy(3,2), Coordinate.xy(3, 3))));
        // 1,3
        player.changeDirection(Direction.DOWN_LEFT);
        coordinates = assistantKungFu.affectedCoordinates(player);
        assertTrue(coordinates.containsAll(List.of(Coordinate.xy(1, 1), Coordinate.xy(1, 2), Coordinate.xy(2,3), Coordinate.xy(3, 3))));
        // 3,3
        player.changeDirection(Direction.DOWN_RIGHT);
        coordinates = assistantKungFu.affectedCoordinates(player);
        assertTrue(coordinates.containsAll(List.of(Coordinate.xy(1, 3), Coordinate.xy(2, 3), Coordinate.xy(3,1), Coordinate.xy(3, 2))));
    }

    @Test
    void description() {
        assertTrue(assistantKungFu.description().contains("修炼等级: 1.00"));
    }

    @Test
    void damage() {
        Damage damage = assistantKungFu.computeDamage(new Damage(10000, 1000, 200, 10));
        assertEquals(99, damage.bodyDamage());
        assertEquals(9, damage.headDamage());
        assertEquals(1, damage.armDamage());
        assertEquals(0, damage.legDamage());
    }

    @Test
    void checkPreconditions() {
        create(true);
        var player = playerBuilder().coordinate(Coordinate.xy(2, 2)).build();
        var ret = assistantKungFu.checkPreconditions(player);
        assertEquals("需要风灵旋满方可修炼。", ret);
        create(false);
        ret = assistantKungFu.checkPreconditions(player);
        assertNull(ret);
    }
}