package org.y1000.entities.teleport;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.Entity;
import org.y1000.message.AbstractEntityInterpolation;
import org.y1000.realm.event.PlayerRealmEvent;
import org.y1000.sdb.CreateGateSdb;
import org.y1000.util.UnaryAction;
import java.util.Objects;

public final class StaticTeleport extends AbstractTeleport implements Entity  {

    private final int shapeId;

    private final String name;

    public StaticTeleport(long id, String idName, CreateGateSdb createGateSdb,
                          UnaryAction<PlayerRealmEvent> teleportEventHandler,
                          int realmId) {
        super(id, idName, createGateSdb, teleportEventHandler, realmId);
        shapeId = createGateSdb.getShape(idName);
        name = createGateSdb.getViewName(idName);
        Validate.notEmpty(name);
    }

    public String viewName() {
        return name;
    }

    @Override
    public AbstractEntityInterpolation captureInterpolation() {
        return new TeleportInterpolation(this);
    }

    public int shape() {
        return shapeId;
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
