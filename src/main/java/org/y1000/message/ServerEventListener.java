package org.y1000.message;

public interface ServerEventListener<E extends ServerEvent> {
    void OnEvent(E event);
}
