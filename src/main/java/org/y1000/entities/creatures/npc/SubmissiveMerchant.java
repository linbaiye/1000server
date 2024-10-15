package org.y1000.entities.creatures.npc;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.AttributeProvider;
import org.y1000.entities.creatures.NpcType;
import org.y1000.entities.creatures.State;
import org.y1000.message.AbstractCreatureInterpolation;
import org.y1000.message.NpcInterpolation;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.*;

@Slf4j
public final class SubmissiveMerchant extends AbstractSubmissiveMerchant {

    @Builder
    public SubmissiveMerchant(long id,
                              Coordinate coordinate,
                              String name,
                              Map<State, Integer> stateMillis,
                              AttributeProvider attributeProvider,
                              Merchantable merchantable,
                              String fileName,
                              RealmMap realmMap) {
        super(id, coordinate, name, stateMillis, attributeProvider, realmMap, merchantable, fileName);
    }

    @Override
    public AbstractCreatureInterpolation captureInterpolation() {
        return new NpcInterpolation(id(), coordinate(), state().stateEnum(), direction(), state().elapsedMillis(), viewName(),
                NpcType.MERCHANT, attributeProvider().animate(), attributeProvider().shape(), getMerchantFile());
    }


    @Override
    protected Logger log() {
        return log;
    }
}
