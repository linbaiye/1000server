package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.y1000.realm.event.RealmEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public final class RealmGroup implements Runnable {

    private final List<Realm> realmList;

    private final List<RealmEvent> pendingEvents;

    private volatile boolean shutdown;

    public RealmGroup(List<Realm> realms) {
        realmList = realms;
        pendingEvents = new ArrayList<>();
        shutdown = false;
    }

    private void updateRealm(Realm realm) {
        try {
            realm.update();
        } catch (Exception e) {
            log.error("Caught exception when updating realm {}.", realm.id(), e);
        }
    }

    private Realm find(int id) {
        return realmList.stream()
                .filter(realm -> realm.id() == id).findAny()
                .orElse(null);
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

    @Override
    public void run() {
        try {
            realmList.forEach(Realm::init);
        } catch (Exception e) {
            log.error("Failed to init realms.", e);
        }
        while (!shutdown) {
            realmList.forEach(this::updateRealm);
            List<RealmEvent> realmEvents = pollPendingEvents();
            realmEvents.forEach(e -> {
                Realm realm = find(e.realmId());
                realm.handle(e);
            });
        }
    }

    public void handle(RealmEvent realmEvent) {
        if (realmEvent == null ||
                realmList.stream().noneMatch(realm -> realm.id() == realmEvent.realmId())) {
            return;
        }
        synchronized (pendingEvents) {
            pendingEvents.add(realmEvent);
            pendingEvents.notify();
        }
    }

    public Set<Integer> realmIds() {
        return realmList.stream().map(Realm::id)
                .collect(Collectors.toSet());
    }
}
