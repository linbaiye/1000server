package org.y1000.entities.players;

import org.mockito.Mockito;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.TestingEventListener;
import org.y1000.message.clientevent.ClientToggleKungFuEvent;
import org.y1000.realm.Realm;
import org.y1000.realm.RealmMap;

import static org.mockito.Mockito.when;

public abstract class AbstractPlayerUnitTestFixture extends AbstractUnitTestFixture  {
    protected PlayerImpl player;

    protected TestingEventListener eventListener;

    protected RealmMap mockedMap;
    protected Realm mockedRealm;


    protected void enableFootKungFu() {
        player.handleClientEvent(new ClientToggleKungFuEvent(1, 8));
    }

    protected void enableBowKungFu() {
        player.handleClientEvent(new ClientToggleKungFuEvent(1, 6));
    }

    protected void enableProtectKungFu() {
        player.handleClientEvent(new ClientToggleKungFuEvent(1, 10));
    }

    protected void setup() {
        player = playerBuilder().build();
        mockedMap = Mockito.mock(RealmMap.class);
        mockedRealm = Mockito.mock(Realm.class);
        when(mockedRealm.map()).thenReturn(mockedMap);
        eventListener = new TestingEventListener();
        player.joinReam(mockedRealm);
        player.registerEventListener(eventListener);
    }
}
