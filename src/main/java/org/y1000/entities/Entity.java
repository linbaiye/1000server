package org.y1000.entities;

import org.y1000.message.I2ClientMessage;
import org.y1000.message.Message;

import java.util.List;
import java.util.Optional;

public interface Entity {
    long id();

    List<I2ClientMessage> update(long delta);
}
