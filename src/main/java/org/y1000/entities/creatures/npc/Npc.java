package org.y1000.entities.creatures.npc;

import lombok.Getter;
import org.y1000.entities.AbstractActiveEntity;
import org.y1000.entities.Direction;
import org.y1000.message.I2ClientMessage;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.*;

public class Npc extends AbstractActiveEntity {

    private final List<NpcAction> abilities;
    private final NpcMessageListener listener;
    private Coordinate coordinate;

    @Getter
    private final RealmMap realmMap;

    private NpcAI ai;

    private final String viewName;

    @Getter
    private final Coordinate spawnCoordinate;


    public Npc(long id,
               String viewName,
               Coordinate coordinate,
               List<NpcAction> actions,
               NpcMessageListener listener,
               RealmMap realmMap) {
        super(id);
        this.abilities = actions;
        this.listener = listener;
        this.realmMap = realmMap;
        this.viewName = viewName;
        this.spawnCoordinate = coordinate;
        this.coordinate = coordinate;
        realmMap.occupy(this);
    }

    public void sendMessage(NpcMessage message) {
        listener.onMessage(message);
    }

    public <A extends NpcAction> Optional<A> findAction(Class<A> type) {
        return abilities.stream()
                .filter(a -> type.isAssignableFrom(a.getClass()))
                .findFirst().map(type::cast);
    }


    public void changeAI(NpcAI newAi) {
        ai = newAi;
    }

    @Override
    public void update(int delta) {
        ai.update(delta);
    }

    @Override
    public Coordinate coordinate() {
        return coordinate;
    }

    public Direction direction() {
        return Direction.DOWN;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
        realmMap.occupy(this);
    }

    @Override
    public I2ClientMessage captureSnapshot() {
        return null;
    }
}
