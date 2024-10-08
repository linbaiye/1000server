package org.y1000.message.clientevent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.entities.players.AbstractPlayerUnitTestFixture;
import org.y1000.entities.players.Player;
import org.y1000.message.PlayerTextEvent;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ClientClickEventTest extends AbstractPlayerUnitTestFixture  {

    @BeforeEach
    void setUp() {
        setup();
    }

    @Test
    void handleClickPlayerEvent() {
        Player clicked = playerBuilder().id(nextId()).build();
        Player source = playerBuilder().id(nextId()).build();
        source.registerEventListener(eventListener);
        new ClientClickEvent(clicked.id()).handle(source, clicked);
        assertNotNull(eventListener.removeFirst(PlayerTextEvent.class));
    }

}