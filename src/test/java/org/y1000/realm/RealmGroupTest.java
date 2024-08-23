package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.players.Player;
import org.y1000.realm.event.RealmTeleportEvent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@Slf4j
class RealmGroupTest extends AbstractRealmUnitTextFixture {

    private RealmGroup realmGroup;
    private RealmFactory realmFactory;
    private CrossRealmEventSender eventHandler;
    private List<Realm> realms;
    private LocalDateTime dateTime;

    private CountDownLatch countDownLatch;

    private Realm latchRealm;

    @BeforeEach
    void setUp() {
        setup();
        realmFactory = Mockito.mock(RealmFactory.class);
        when(realmFactory.createRealm(anyInt(), any(CrossRealmEventSender.class))).thenAnswer(invocationOnMock -> {
            Realm realm = Mockito.mock(Realm.class);
            when(realm.id()).thenReturn(invocationOnMock.getArgument(0));
            return realm;
        });
        eventHandler = Mockito.mock(CrossRealmEventSender.class);
        realms = new ArrayList<>();
        latchRealm = Mockito.mock(Realm.class);
        countDownLatch = new CountDownLatch(3);
        doAnswer(invocationOnMock -> {
            countDownLatch.countDown();
            return null;
        }).when(latchRealm).update();
    }


    @Test
    void init() throws InterruptedException {
        Realm realm = Mockito.mock(Realm.class);
        realms.add(realm);
        realms.add(latchRealm);
        dateTime = LocalDateTime.now().withMinute(1).withSecond(0);
        realmGroup = new RealmGroup(realms, realmFactory, eventHandler, () -> dateTime);
        new Thread(realmGroup).start();
        countDownLatch.await(30, TimeUnit.SECONDS);
        realmGroup.shutdown();
        verify(realm, times(1)).init();
    }

    @Test
    void resetHalfHourDungeonWhenNotTime() throws InterruptedException {
        Player player = playerBuilder().build();
        when(playerManager.allPlayers()).thenReturn(Collections.singleton(player));
        when(playerManager.contains(player)).thenReturn(true);
        dateTime = LocalDateTime.now().withMinute(29).withSecond(57);
        realms.add(createHalfHourDungeon(() -> dateTime));
        realms.add(latchRealm);
        realmGroup = new RealmGroup(realms, realmFactory, eventHandler, () -> dateTime);
        new Thread(realmGroup).start();
        countDownLatch.await(30, TimeUnit.SECONDS);
        realmGroup.shutdown();
        verify(crossRealmEventSender, times(0)).send(any(RealmTeleportEvent.class));
    }

    @Test
    void resetHalfHourDungeon() throws InterruptedException {
        Player player = playerBuilder().build();
        when(playerManager.allPlayers()).thenReturn(Collections.singleton(player));
        when(playerManager.contains(player)).thenReturn(true);
        dateTime = LocalDateTime.now().withMinute(29).withSecond(58);
        realms.add(createHalfHourDungeon(() -> dateTime));
        realms.add(latchRealm);
        realmGroup = new RealmGroup(realms, realmFactory, eventHandler, () -> dateTime);
        when(realmFactory.createRealm(anyInt(), any(CrossRealmEventSender.class))).thenReturn(createHalfHourDungeon(() -> dateTime));
        new Thread(realmGroup).start();
        countDownLatch.await(30, TimeUnit.SECONDS);
        realmGroup.shutdown();
        verify(crossRealmEventSender, times(1)).send(any(RealmTeleportEvent.class));
        verify(realmFactory, times(1)).createRealm(anyInt(), any(CrossRealmEventSender.class));
    }

    @Test
    void resetOneHourDungeon() throws InterruptedException {
        Player player = playerBuilder().build();
        when(playerManager.allPlayers()).thenReturn(Collections.singleton(player));
        when(playerManager.contains(player)).thenReturn(true);
        dateTime = LocalDateTime.now().withMinute(59).withSecond(59);
        realms.add(createOneHourDungeon(() -> dateTime));
        realms.add(latchRealm);
        realmGroup = new RealmGroup(realms, realmFactory, eventHandler, () -> dateTime);
        when(realmFactory.createRealm(anyInt(), any(CrossRealmEventSender.class))).thenReturn(createOneHourDungeon(() -> dateTime));
        new Thread(realmGroup).start();
        countDownLatch.await(30, TimeUnit.SECONDS);
        realmGroup.shutdown();
        verify(crossRealmEventSender, times(1)).send(any(RealmTeleportEvent.class));
        verify(realmFactory, times(1)).createRealm(anyInt(), any(CrossRealmEventSender.class));
    }
}