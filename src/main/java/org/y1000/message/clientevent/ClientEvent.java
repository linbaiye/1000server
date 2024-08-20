package org.y1000.message.clientevent;

public interface ClientEvent {
    
    default boolean withinRealm() {
        return true;
    }

}
