package org.y1000.entities;

import org.y1000.message.I2ClientMessage;

import java.util.List;

public interface Entity {
    long id();

    List<I2ClientMessage> update(long delta, long timeMilli);
}
