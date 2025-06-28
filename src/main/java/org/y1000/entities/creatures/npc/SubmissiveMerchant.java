package org.y1000.entities.creatures.npc;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.AttributeProvider;
import org.y1000.entities.creatures.NpcType;
import org.y1000.entities.creatures.PlayerStateEnum;
import org.y1000.message.AbstractCreatureSnapshot;
import org.y1000.message.NpcSnapshot;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.*;

@Slf4j
public final class SubmissiveMerchant extends AbstractSubmissiveMerchant {

    @Builder
    public SubmissiveMerchant(long id,
                              Coordinate coordinate,
                              String name,
                              Map<PlayerStateEnum, Integer> stateMillis,
                              AttributeProvider attributeProvider,
                              Merchantable merchantable,
                              String fileName,
                              RealmMap realmMap) {
        super(id, coordinate, name, stateMillis, attributeProvider, realmMap, merchantable, fileName);
    }

    @Override
    public AbstractCreatureSnapshot captureInterpolation() {
        return new NpcSnapshot(id(), coordinate(), creatureState().stateEnum(), direction(), creatureState().elapsedMillis(), viewName(),
                NpcType.MERCHANT, attributeProvider().animate(), attributeProvider().shape(), getMerchantFile());
    }


    @Override
    protected Logger log() {
        return log;
    }
}
