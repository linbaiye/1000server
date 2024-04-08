package org.y1000.entities;

import org.y1000.message.ServerEvent;

import java.util.List;

public interface Entity {
    long id();

    List<ServerEvent> update(long delta);

}
