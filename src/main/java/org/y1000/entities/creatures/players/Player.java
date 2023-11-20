package org.y1000.entities.creatures.players;

import org.y1000.entities.creatures.Creature;
import org.y1000.message.*;
import org.y1000.util.Coordinate;
import org.y1000.realm.RealmMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Player implements Creature {

    private Coordinate myCoordinate;
    private long lastUpdate;

    public List<Message> handle(List<Message> messages, RealmMap map) {
        List<Message> result = new ArrayList<>();
        for (Message message : messages) {
            if (message instanceof MoveMessage moveMessage)
                result.add(handleMoveMessage(moveMessage, map));
        }
        return Collections.emptyList();
    }

    private Message handleMoveMessage(MoveMessage moveMessage, RealmMap map) {
        var nextCoordinate = myCoordinate.moveBy(moveMessage.direction());
        if (!map.movable(nextCoordinate)) {
            return new SetCoordinateMessage(moveMessage.messageId(), myCoordinate);
        }
        return new ConfirmMessage(moveMessage.messageId());
    }

    @Override
    public long id() {
        return 0;
    }

    @Override
    public Coordinate coordinate() {
        return myCoordinate;
    }
}
