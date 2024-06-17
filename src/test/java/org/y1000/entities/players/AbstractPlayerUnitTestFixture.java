package org.y1000.entities.players;

import org.junit.jupiter.api.BeforeEach;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.TestingEventListener;
import org.y1000.message.clientevent.ClientToggleKungFuEvent;

public abstract class AbstractPlayerUnitTestFixture extends AbstractUnitTestFixture  {
    protected PlayerImpl player;

    protected TestingEventListener eventListener;


    protected void enableFootKungFu() {
        player.handleClientEvent(new ClientToggleKungFuEvent(1, 8));
    }

    void setup() {
        player = playerBuilder().build();
        eventListener = new TestingEventListener();
        player.registerEventListener(eventListener);
    }
}
