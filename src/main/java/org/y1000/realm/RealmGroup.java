package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public final class RealmGroup implements Runnable {

    private final List<Envelop> pendingEvents;

    private volatile boolean shutdown;

    private final Realm[] realms;

    private final RealmFactory realmFactory;

    private final RealmEventSender crossRealmEventSender;

    private final Supplier<LocalDateTime> dateTimeSupplier;

    private final Set<Integer> ids;

    public RealmGroup(List<Realm> realms,
                      RealmFactory realmFactory,
                      RealmEventSender crossRealmEventSender,
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
        ids = realms.stream().map(Realm::id).collect(Collectors.toSet());
    }

    public RealmGroup(List<Realm> realms,
                      RealmFactory realmFactory,
                      RealmEventSender crossRealmEventSender) {
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
        try {
            for (int i = 0; i < realms.length; i++) {
                if (!(realms[i] instanceof DungeonRealm dungeonRealm)) {
                    continue;
                }
                if (dungeonRealm.needToClose(now.getMinute(), now.getSecond())) {
                    dungeonRealm.close();
                    realms[i] = realmFactory.createRealm(dungeonRealm.id(), crossRealmEventSender);
                    realms[i].init();
                }
            }
        } catch (Exception e) {
            log.error("Failed to reset dungeon.", e);
        }
    }

    private Optional<Realm> find(int id) {
        return Arrays.stream(realms)
                .filter(realm -> realm.id() == id)
                .findFirst();
    }

    public synchronized void shutdown() throws InterruptedException {
        shutdown = true;
        Thread.sleep(1000);
        Stream.of(realms).forEach(Realm::shutdown);
    }

    private List<Envelop> pollPendingEvents() {
        List<Envelop> events = Collections.emptyList();
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


    private void handleRealmEvent(Envelop envelop){
        find(envelop.realmId()).ifPresent(realm -> realm.handle(envelop.event()));
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
            List<Envelop> newEvents = pollPendingEvents();
            newEvents.forEach(this::handleRealmEvent);
        }
    }

    private record Envelop(int realmId, Object event) {
    }


    public void handle(int realmId, Object event) {
        if (!ids.contains(realmId) || event == null)
            return;
        synchronized (pendingEvents) {
            pendingEvents.add(new Envelop(realmId, event));
            pendingEvents.notify();
        }
    }

    public void broadcast(Object event) {
        if (event == null) {
            return;
        }
        synchronized (pendingEvents) {
            realmIds().forEach(id -> pendingEvents.add(new Envelop(id, event)));
            pendingEvents.notify();
        }
    }


    public Set<Integer> realmIds() {
        return ids;
    }
}
