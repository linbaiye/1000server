package org.y1000.realm;

import org.y1000.entities.ActiveEntity;

public abstract class AbstractMovableEntityManager<T extends ActiveEntity> extends AbstractActiveEntityManager<T> {

    protected AbstractMovableEntityManager(AOIManager aoiManager, MessageSender messageSender) {
        super(aoiManager, messageSender);
    }

}
