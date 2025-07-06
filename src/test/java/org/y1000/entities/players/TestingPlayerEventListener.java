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

    public Optional<PlayerEvent> removeFirst() {
        return eventList.isEmpty()? Optional.empty() : Optional.of(eventList.remove(0));
    }


    @Override
    public void onEvent(PlayerEvent event) {
        eventList.add(event);
    }
}
