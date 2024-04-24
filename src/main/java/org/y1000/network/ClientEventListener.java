package org.y1000.network;

import org.y1000.message.clientevent.ClientEvent;

public interface ClientEventListener {

    void OnEvent(ClientEvent clientEvent);
}
