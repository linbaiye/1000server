package org.y1000;

import org.y1000.entities.TypedEntityEvent;

import java.util.ArrayList;
import java.util.List;

public class TestingEventListener {

    private final List<TypedEntityEvent> entityEvents = new ArrayList<>();


    public int eventSize() {
        return entityEvents.size();
    }

    public boolean isEmpty() {
        return entityEvents.isEmpty();
    }


    public void clearEvents() {
        entityEvents.clear();
    }


}
