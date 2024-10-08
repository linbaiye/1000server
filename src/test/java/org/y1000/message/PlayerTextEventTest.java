package org.y1000.message;

import org.junit.jupiter.api.Test;
import org.y1000.entities.players.AbstractPlayerUnitTestFixture;
import org.y1000.kungfu.TestingAttackKungFuParameters;
import org.y1000.kungfu.attack.QuanfaKungFu;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTextEventTest extends AbstractPlayerUnitTestFixture {

    @Test
    void clickPlayerText() {
        var source = playerBuilder().build();
        var clicked = playerBuilder().name("clicked").attackKungFu(new QuanfaKungFu("test", 0, new TestingAttackKungFuParameters())).build();
        PlayerTextEvent playerTextEvent = PlayerTextEvent.playerClicked(source, clicked);
        String text = playerTextEvent.toPacket().getText().getText();
        assertTrue(text.startsWith("名称: clicked"));
    }
}