package org.y1000.entities.teleport;

import lombok.Builder;
import org.y1000.entities.Entity;
import org.y1000.entities.creatures.monster.PassiveMonster;
import org.y1000.entities.players.Player;
import org.y1000.message.AbstractEntityInterpolation;
import org.y1000.realm.event.RealmEvent;
import org.y1000.sdb.CreateGateSdb;
import org.y1000.util.Coordinate;
import org.y1000.util.UnaryAction;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class StaticTeleport extends AbstractTeleport implements Entity  {


    public StaticTeleport(long id, String idName, CreateGateSdb createGateSdb, UnaryAction<RealmEvent> teleportEventHandler) {
        super(id, idName, createGateSdb, teleportEventHandler);
    }

    @Override
    public AbstractEntityInterpolation captureInterpolation() {
        return null;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        return obj == this || ((StaticTeleport) obj).id() == id();
    }

}
