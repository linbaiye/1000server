package org.y1000.entities.creatures.npc;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.y1000.entities.AttributeProvider;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.monster.NpcActionEnum;
import org.y1000.quest.Quest;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.Collections;
import java.util.Map;

@Slf4j
public final class ViolentQuester extends AbstractViolentNpc implements Quester {


    @Getter
    private final Quest quest;

    @Builder
    public ViolentQuester(long id, Coordinate coordinate,
                              Direction direction,
                              String name,
                              Map<NpcActionEnum, Integer> stateMillis,
                              AttributeProvider attributeProvider,
                              RealmMap realmMap,
                              Quest quest) {
        super(id, coordinate, direction, name, stateMillis, attributeProvider, realmMap, new ViolentNpcWanderingAI(coordinate), null, Collections.emptyList());
        Validate.notNull(quest);
        this.quest = quest;
    }

    @Override
    protected Logger log() {
        return log;
    }


    @Override
    public void startIdleAI() {
        changeAndStartAI(new ViolentNpcWanderingAI(spawnCoordinate()));
    }
}
