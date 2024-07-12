package org.y1000.kungfu.protect;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.players.Player;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ProtectKungFuTest {

    private static class TestingProtParameters implements ProtectionParameters {

        @Override
        public int bodyArmor() {
            return 1;
        }

        @Override
        public int headArmor() {
            return 2;
        }

        @Override
        public int armArmor() {
            return 3;
        }

        @Override
        public int legArmor() {
            return 10;
        }

        @Override
        public int innerPowerPer5Seconds() {
            return 5;
        }

        @Override
        public int outerPowerPer5Seconds() {
            return 6;
        }

        @Override
        public int powerPer5Seconds() {
            return 7;
        }

        @Override
        public int lifePer5Seconds() {
            return 8;
        }

        @Override
        public int powerToKeep() {
            return 9;
        }

        @Override
        public int innerPowerToKeep() {
            return 10;
        }

        @Override
        public int outerPowerToKeep() {
            return 11;
        }

        @Override
        public int lifeToKeep() {
            return 12;
        }

        @Override
        public String enableSound() {
            return null;
        }

        @Override
        public String disableSound() {
            return null;
        }
    }

    private ProtectKungFu protectKungFu;

    private ProtectionParameters parameters;

    @BeforeEach
    void setUp() {
        parameters = new TestingProtParameters();
        protectKungFu = ProtectKungFu.builder()
                .name("test")
                .exp(0)
                .parameters(parameters)
                .build();
    }

    @Test
    void useResources() {
        Player player = Mockito.mock(Player.class);
        when(player.currentLife()).thenReturn(100);
        protectKungFu.updateResources(player, 5000);
        Mockito.verify(player).consumeLife(parameters.lifePer5Seconds());
        Mockito.verify(player).consumeInnerPower(parameters.innerPowerPer5Seconds());
        Mockito.verify(player).consumeOuterPower(parameters.outerPowerPer5Seconds());
        Mockito.verify(player).consumePower(parameters.powerPer5Seconds());
        player = Mockito.mock(Player.class);
        when(player.currentLife()).thenReturn(2);
        protectKungFu.updateResources(player, 5000);
        Mockito.verify(player).consumeLife(1);
    }

    @Test
    void canKeep() {
        Player player = Mockito.mock(Player.class);
        when(player.power()).thenReturn(0);
        when(player.currentLife()).thenReturn(0);
        when(player.innerPower()).thenReturn(0);
        when(player.outerPower()).thenReturn(0);
        assertFalse(protectKungFu.canKeep(player));

        player = Mockito.mock(Player.class);
        when(player.power()).thenReturn(10);
        when(player.currentLife()).thenReturn(20);
        when(player.innerPower()).thenReturn(20);
        when(player.outerPower()).thenReturn(20);
        assertTrue(protectKungFu.canKeep(player));
    }

    @Test
    void gainExp() {
        assertTrue(protectKungFu.gainExp(1000));
        assertEquals(159, protectKungFu.level());
    }

    @Test
    void computeArmor() {
        assertEquals(protectKungFu.bodyArmor(), 1);
        assertEquals(protectKungFu.headArmor(), 2);
        assertEquals(protectKungFu.armArmor(), 3);
        assertEquals(10, protectKungFu.legArmor());
        while (protectKungFu.level() != 9999)
            protectKungFu.gainExp(10000);
        assertEquals(protectKungFu.bodyArmor(), 2);
        assertEquals(32, protectKungFu.legArmor());
    }

    @Test
    void description() {
        String description = protectKungFu.description();
        System.out.println(description);
        assertTrue(description.contains("修炼等级: 1.00"));
        assertTrue(description.contains("防御力: 1 / 2 / 3 / 10"));
    }
}