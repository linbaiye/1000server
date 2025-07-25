package org.y1000.realm;

import org.y1000.entities.objects.DynamicObjectEvent;

public interface DynamicObjectEventListener {
    void onEvent(DynamicObjectEvent event);
}
