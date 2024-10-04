package org.y1000.entities.creatures.event;

import lombok.Getter;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.ActiveEntity;
import org.y1000.entities.Entity;
import org.y1000.entities.players.Player;
import org.y1000.event.EntityEvent;

import java.util.Collections;
import java.util.Set;

public class SeekPlayerEvent implements EntityEvent {
    private final ActiveEntity entity;

    @Getter
    private Set<Player> players;

    public SeekPlayerEvent(ActiveEntity entity) {
        Validate.notNull(entity);
        this.entity = entity;
        players = Collections.emptySet();
    }

    public void setPlayers(Set<Player> players) {
        if (players!= null)
            this.players = players;
    }


    @Override
    public Entity source() {
        return entity;
    }

}
