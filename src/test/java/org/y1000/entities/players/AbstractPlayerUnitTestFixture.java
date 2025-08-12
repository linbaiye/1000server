package org.y1000.entities.players;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.TestingEventListener;
import org.y1000.entities.Direction;
import org.y1000.entities.players.event.PlayerEvent;
import org.y1000.kungfu.KungFu;
import org.y1000.kungfu.TestingAttackKungFuParameters;
import org.y1000.kungfu.attack.QuanfaKungFu;
import org.y1000.realm.Realm;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

public abstract class AbstractPlayerUnitTestFixture extends AbstractUnitTestFixture  {
    protected PlayerImpl player;

    protected TestingEventListener testingEventListener;

    protected RealmMap mockedMap;

    protected Realm mockedRealm;

    protected final TestingPlayerEventListener eventListener = TestingPlayerEventListener.Instance;


    protected int addBasicKungFu(KungFu kungFu) {
        int slot = player.kungFuBook().findBasicSlot(kungFu.name());
        if (slot == 0) {
            slot = player.kungFuBook().addToBasic(kungFu);
        }
        return slot;
    }

    protected void mockPlayer() {
        player = Mockito.mock(PlayerImpl.class);
        doAnswer(invocationOnMock -> {
            eventListener.onEvent(invocationOnMock.getArgument(0));
            return null;
        }).when(player).sendEvent(any(PlayerEvent.class));
        when(player.coordinate()).thenReturn(Coordinate.xy(1, 1));
        when(player.direction()).thenReturn(Direction.UP);
    }

    protected void recreatePlayer(PlayerImpl.PlayerImplBuilder builder) {
        player = builder.build();
        testingEventListener = new TestingEventListener();
        mockedRealm = mockAllFlatRealm();
        eventListener.clear();
        player.joinRealm(mockedRealm, eventListener);
    }

    protected void setup() {
        player = playerBuilder().build();
        testingEventListener = new TestingEventListener();
        mockedRealm = mockAllFlatRealm();
        eventListener.clear();
        player.joinRealm(mockedRealm, eventListener);
    }
}
