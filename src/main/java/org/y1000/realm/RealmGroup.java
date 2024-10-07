package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.y1000.realm.event.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public final class RealmGroup implements Runnable {

    private final List<RealmEvent> pendingEvents;

    private volatile boolean shutdown;

    private final Realm[] realms;

    private final RealmFactory realmFactory;

    private final CrossRealmEventSender crossRealmEventSender;

    private final Supplier<LocalDateTime> dateTimeSupplier;

    private LocalDateTime nextResetTime;

    public RealmGroup(List<Realm> realms,
                      RealmFactory realmFactory,
                      CrossRealmEventSender crossRealmEventSender,
                      Supplier<LocalDateTime> dateTimeSupplier) {
        Validate.isTrue(realms != null && !realms.isEmpty());
        Validate.notNull(realmFactory);
        Validate.notNull(crossRealmEventSender);
        Validate.notNull(dateTimeSupplier);
        this.realms = realms.toArray(new Realm[0]);
        this.realmFactory = realmFactory;
        pendingEvents = new ArrayList<>();
        shutdown = false;
        this.crossRealmEventSender = crossRealmEventSender;
        this.dateTimeSupplier = dateTimeSupplier;
        LocalDateTime now = dateTimeSupplier.get();
        this.nextResetTime = now.getMinute() < 30 ? now.withMinute(29).withSecond(58).withNano(0) :
                now.withMinute(48).withSecond(58).withNano(0);
        log.debug("Set next reset time to {}.", this.nextResetTime);
    }

    public RealmGroup(List<Realm> realms,
                      RealmFactory realmFactory,
                      CrossRealmEventSender crossRealmEventSender) {
        this(realms, realmFactory, crossRealmEventSender, LocalDateTime::now);
    }

    private void updateRealm(Realm realm) {
        try {
            realm.update();
        } catch (Exception e) {
            log.error("Caught exception when updating realm {}.", realm.id(), e);
        }
    }

    private void resetDungeonsIfTimeUp() {
        LocalDateTime now = dateTimeSupplier.get();
        if (now.isBefore(nextResetTime)) {
            return;
        }
        log.debug("Trying to reset realms {}.", Arrays.stream(realms).toList());
        try {
            for (int i = 0; i < realms.length; i++) {
                if (!(realms[i] instanceof AbstractDungeonRealm dungeonRealm)) {
                    continue;
                }
                if (!dungeonRealm.isHalfHourInterval() && now.getMinute() != 59) {
                    continue;
                }
                dungeonRealm.close();
                realms[i] = realmFactory.createRealm(dungeonRealm.id(), crossRealmEventSender);
                realms[i].init();
            }
        } catch (Exception e) {
            log.error("Failed to reset dungeon.", e);
        }
        nextResetTime = nextResetTime.plusMinutes(30);
        log.debug("Set next reset time to {}.", this.nextResetTime);
    }

    private Optional<Realm> find(int id) {
        return Arrays.stream(realms)
                .filter(realm -> realm.id() == id)
                .findFirst();
    }

    public synchronized void shutdown() throws InterruptedException {
        shutdown = true;
        Thread.sleep(100);
        Stream.of(realms).forEach(Realm::shutdown);
    }

    private List<RealmEvent> pollPendingEvents() {
        List<RealmEvent> events = Collections.emptyList();
        try {
            synchronized (pendingEvents) {
                if (pendingEvents.isEmpty()) {
                    pendingEvents.wait(RealmImpl.STEP_MILLIS);
                }
                if (!pendingEvents.isEmpty()) {
                    events = new ArrayList<>(pendingEvents);
                    pendingEvents.clear();
                }
                pendingEvents.notify();
            }
        } catch (Exception e) {
            log.error("Exception when polling events.", e);
        }
        return events;
    }


    private void handleRealmEvent(RealmEvent realmEvent){
        if (realmEvent instanceof PlayerRealmEvent playerRealmEvent) {
            find(playerRealmEvent.toRealmId()).ifPresent(realm -> realm.handle(playerRealmEvent));
        } else if (realmEvent instanceof RealmTriggerEvent letterEvent) {
            find(letterEvent.toRealmId()).ifPresent(realm -> realm.handle(letterEvent));
        } else {
            Arrays.stream(realms).forEach(realm -> realm.handle(realmEvent));
        }
    }

    @Override
    public void run() {
        try {
            log.info("Start initializing realms {}.", Arrays.stream(realms).toList());
            Arrays.stream(realms).forEach(Realm::init);
        } catch (Exception e) {
            log.error("Failed to init realms.", e);
            return;
        }
        while (!shutdown) {
            Arrays.stream(realms).forEach(this::updateRealm);
            resetDungeonsIfTimeUp();
            List<RealmEvent> newEvents = pollPendingEvents();
            newEvents.forEach(this::handleRealmEvent);
        }
    }

    public void handle(RealmEvent realmEvent) {
        if (realmEvent == null) {
            return;
        }
        synchronized (pendingEvents) {
            pendingEvents.add(realmEvent);
            pendingEvents.notify();
        }
    }

    public Set<Integer> realmIds() {
        return Stream.of(realms).map(Realm::id)
                .collect(Collectors.toSet());
    }
}
