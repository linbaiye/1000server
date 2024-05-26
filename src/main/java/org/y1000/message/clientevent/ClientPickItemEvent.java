package org.y1000.message.clientevent;

import lombok.Getter;

public record ClientPickItemEvent(long id) implements ClientEvent {
}
