package org.y1000.entities.projectile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.entities.players.Damage;
import org.y1000.entities.players.Player;
import org.y1000.util.Coordinate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


class PlayerProjectileTest {

    private PlayerProjectile projectile;

    private Player player;

    private Npc npc;

    @BeforeEach
    void setUp() {
        player = Mockito.mock(Player.class);
        when(player.coordinate()).thenReturn(Coordinate.xy(1, 1));
        when(player.damage()).thenReturn(Damage.ZERO);
        npc = Mockito.mock(Npc.class);
        when(npc.coordinate()).thenReturn(Coordinate.xy(5, 1));
    }

    @Test
    void direction() {
        projectile = new PlayerProjectile(player, npc, Damage.ZERO, 1, 2);
        assertEquals(Direction.RIGHT, projectile.direction());
    }

    @Test
    void update() {
        projectile = new PlayerProjectile(player, npc, Damage.ZERO, 1, 2);
        projectile.update(100000);
        verify(player, times(1)).onProjectileReachTarget(projectile);
    }
}