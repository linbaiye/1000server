package org.y1000.entities.creatures.npc;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.AttributeProvider;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.ViolentCreature;
import org.y1000.entities.creatures.event.NpcShiftEvent;
import org.y1000.entities.creatures.monster.NpcAnimationEnum;
import org.y1000.entities.creatures.npc.AI.INpcAI;
import org.y1000.entities.creatures.npc.spell.NpcSpell;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
public final class NineTailFoxHuman extends AbstractNpc {
    public NineTailFoxHuman(long id, Coordinate coordinate, Direction direction, String name, Map<NpcAnimationEnum, Integer> stateMillis, AttributeProvider attributeProvider, RealmMap realmMap, List<NpcSpell> spells, INpcAI ai) {
        super(id, coordinate, direction, name, stateMillis, attributeProvider, realmMap, spells, ai);
    }

    @Override
    public void update(int delta) {

    }

    @Override
    void hurt(ViolentCreature attacker) {

    }

    @Override
    protected Logger log() {
        return null;
    }

    @Override
    public void startIdleAI() {

    }

//    @Builder
//    public NineTailFoxHuman(long id, Coordinate coordinate, Direction direction, String name,
//                            Map<NpcAnimationEnum, Integer> stateMillis, AttributeProvider attributeProvider,
//                            RealmMap realmMap, SubmissiveWanderingAI ai) {
//        super(id, coordinate, direction, name, stateMillis, attributeProvider, realmMap, Collections.emptyList(), ai);
//    }
//
//    @Override
//    public void update(int delta) {
//        npcState().update(delta);
//    }
//
//
//    @Override
//    void hurt(ViolentCreature attacker) {
//        doHurtAction(attacker, getStateMillis(NpcAnimationEnum.Hurt));
//    }
//
//    @Override
//    protected Logger log() {
//        return log;
//    }
//
//
//    public void shift() {
//        emitEvent(new NpcShiftEvent("九尾狐变身", this));
//    }
//
//
//
//    @Override
//    public void startIdleAI() {
//        getAI().start(this);
//    }

}
