package org.y1000.kungfu;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.entities.players.Player;
import org.y1000.kungfu.attack.AttackKungFuFixedParametersImpl;
import org.y1000.kungfu.attack.QuanfaKungFu;

class TimelyPowerConsumingKungFuTest extends AbstractUnitTestFixture {
    private QuanfaKungFu quanfaKungFu;

    private Player player;

    @BeforeEach
    void setUp() {
        quanfaKungFu = QuanfaKungFu.builder()
                .parameters(new AttackKungFuFixedParametersImpl("无名拳法", KungFuSdb.INSTANCE))
                .name("无名拳法")
                .build();
    }

    @Test
    void consumingTimer() {
        player = playerBuilder().build();
    }
}
