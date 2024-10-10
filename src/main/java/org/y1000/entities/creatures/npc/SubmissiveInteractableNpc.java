package org.y1000.entities.creatures.npc;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.NpcType;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.npc.AI.SubmissiveWanderingAI;
import org.y1000.entities.players.Player;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.Map;
import java.util.Optional;

@Slf4j
public final class SubmissiveInteractableNpc extends AbstractSubmissiveNpc implements InteractableNpc {

    private final NpcInteractor interactor;

    public SubmissiveInteractableNpc(long id,
                                     Coordinate coordinate,
                                     String name,
                                     Map<State, Integer> stateMillis,
                                     NonMonsterNpcAttributeProvider attributeProvider,
                                     RealmMap realmMap,
                                     NpcInteractor interactor) {
        super(id, coordinate, Direction.DOWN, name, stateMillis, attributeProvider, realmMap, null, new SubmissiveWanderingAI());
        Validate.notNull(interactor);
        this.interactor = interactor;
    }

    @Override
    protected Logger log() {
        return log;
    }

    @Override
    protected NpcType getType() {
        return NpcType.INTERACTABLE;
    }

    @Override
    public void onClicked(Player player) {
        if (isDead())
            return;
        interactor.onNpcClicked(player, this, attributeProvider().shape(), ((NonMonsterNpcAttributeProvider)attributeProvider()).image());
    }

    @Override
    public void interact(Player player, String name) {
        if (isDead())
            return;
        interactor.onInteractabilityClicked(player, this, name);
    }

    @Override
    public Optional<NpcInteractability> findAbility(String name) {
        return interactor.findAbility(name);
    }
}
