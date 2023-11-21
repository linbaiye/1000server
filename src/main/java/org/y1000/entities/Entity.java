package org.y1000.entities;

import org.y1000.message.Message;

import java.util.Optional;

public interface Entity {
    long id();

    Optional<Message> update(long delta);
}
