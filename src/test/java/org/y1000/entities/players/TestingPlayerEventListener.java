package org.y1000.entities.players;

import org.y1000.TestingEventListener;
import org.y1000.entities.players.event.PlayerEvent;
import org.y1000.message.PlayerEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TestingPlayerEventListener implements PlayerEventListener  {

    private final List<PlayerEvent> eventList = new ArrayList<>();

    public static final TestingPlayerEventListener Instance =  new TestingPlayerEventListener();


    public void clear() {
        eventList.clear();
    }

    public <T extends PlayerEvent> T removeFirst(Class<T> type) {
        return type.cast(eventList.remove(0));
    }

    @Override
    public void onEvent(PlayerEvent event) {
        eventList.add(event);
    }

    public <T extends PlayerEvent> Optional<T> findFirst(Class<T> type) {
        return eventList.stream().filter(e -> type.isAssignableFrom(e.getClass()))
                .findFirst().map(type::cast);
    }
}
