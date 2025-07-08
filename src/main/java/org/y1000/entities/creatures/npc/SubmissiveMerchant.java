package org.y1000.entities.creatures.npc;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.AttributeProvider;
import org.y1000.entities.creatures.monster.NpcAnimationEnum;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.*;

@Slf4j
public final class SubmissiveMerchant extends AbstractSubmissiveMerchant {

    @Builder
    public SubmissiveMerchant(long id,
                              Coordinate coordinate,
                              String name,
                              Map<NpcAnimationEnum, Integer> stateMillis,
                              AttributeProvider attributeProvider,
                              Merchantable merchantable,
                              String fileName,
                              RealmMap realmMap) {
        super(id, coordinate, name, stateMillis, attributeProvider, realmMap, merchantable, fileName);
    }

    @Override
    protected Logger log() {
        return log;
    }
}
