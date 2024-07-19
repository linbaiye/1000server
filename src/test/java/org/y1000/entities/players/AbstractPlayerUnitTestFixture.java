package org.y1000.entities.players;

import org.mockito.Mockito;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.TestingEventListener;
import org.y1000.kungfu.KungFu;
import org.y1000.kungfu.TestingAttackKungFuParameters;
import org.y1000.kungfu.attack.QuanfaKungFu;
import org.y1000.message.clientevent.ClientToggleKungFuEvent;
import org.y1000.realm.Realm;
import org.y1000.realm.RealmMap;

import static org.mockito.Mockito.when;

public abstract class AbstractPlayerUnitTestFixture extends AbstractUnitTestFixture  {
    protected PlayerImpl player;

    protected TestingEventListener eventListener;

    protected RealmMap mockedMap;
    protected Realm mockedRealm;


    protected void clickBasicFootKungFu() {
        player.handleClientEvent(new ClientToggleKungFuEvent(1, 8));
    }

    protected void enableBowKungFu() {
        player.handleClientEvent(new ClientToggleKungFuEvent(1, 6));
    }

    protected void enableProtectKungFu() {
        player.handleClientEvent(new ClientToggleKungFuEvent(1, 10));
    }

    protected int addBasicKungFu(KungFu kungFu) {
        int slot = player.kungFuBook().findBasicSlot(kungFu.name());
        if (slot == 0) {
            slot = player.kungFuBook().addToBasic(kungFu);
        }
        return slot;
    }

    protected void enableTestingKungFu() {
        int slot = addBasicKungFu(new QuanfaKungFu("test", 0, TestingAttackKungFuParameters.builder()
                .avoidance(0).armArmor(1).bodyArmor(1).legArmor(1).headArmor(1)
                .attackSpeed(100)
                .bodyDamage(1000).headDamage(1000).armDamage(1000).legDamage(1000)
                .build()));
        enableBasicKungFu(slot);
    }

    protected void enableBasicKungFu(int slot) {
        player.handleClientEvent(new ClientToggleKungFuEvent(2, slot));
    }

    protected void enableAssistant8KungFu() {
        int slot = player.kungFuBook().findBasicSlot("灵动八方");
        if (slot == 0) {
            slot = player.kungFuBook().addToBasic(kungFuFactory.create("灵动八方"));
        }
        player.handleClientEvent(new ClientToggleKungFuEvent(2, slot));
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
