package org.y1000.entities.creatures.npc;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.y1000.entities.AttributeProvider;
import org.y1000.entities.creatures.NpcType;
import org.y1000.entities.creatures.PlayerStateEnum;
import org.y1000.message.AbstractCreatureSnapshot;
import org.y1000.message.NpcSnapshot;
import org.y1000.quest.Quest;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@Slf4j
public final class SubmissiveMerchantQuester extends AbstractSubmissiveMerchant implements Quester {

    @Getter
    private final Quest quest;

    @Builder
    public SubmissiveMerchantQuester(long id,
                                     Coordinate coordinate, String name,
                                     Map<PlayerStateEnum, Integer> stateMillis,
                                     AttributeProvider attributeProvider,
                                     RealmMap realmMap,
                                     Merchantable merchantable,
                                     String merchantFile,
                                     Quest quest) {
        super(id, coordinate, name, stateMillis, attributeProvider, realmMap, merchantable, merchantFile);
        Validate.notNull(quest);
        this.quest = quest;
    }

    @Override
    protected Logger log() {
        return log;
    }

    @Override
    public AbstractCreatureSnapshot captureInterpolation() {
        return new NpcSnapshot(id(), coordinate(), creatureState().stateEnum(), direction(), creatureState().elapsedMillis(), viewName(),
                NpcType.MERCHANT_QUESTER, attributeProvider().animate(), attributeProvider().shape(), getMerchantFile(), Collections.singletonList(quest.getQuestName()));
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
        return obj == this || ((SubmissiveMerchantQuester) obj).id() == id();
    }

    @Override
    public NpcState creatureState() {
        return null;
    }
}
