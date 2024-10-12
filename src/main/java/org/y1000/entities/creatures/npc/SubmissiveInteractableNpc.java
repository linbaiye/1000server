package org.y1000.entities.creatures.npc;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.NpcType;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.npc.AI.SubmissiveWanderingAI;
import org.y1000.entities.creatures.npc.interactability.NpcInteractability;
import org.y1000.entities.creatures.npc.interactability.NpcInteractor;
import org.y1000.entities.players.Player;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.Map;
import java.util.Optional;

@Slf4j
public final class SubmissiveInteractableNpc extends AbstractSubmissiveNpc implements InteractableNpc {

    private final NpcInteractor interactor;

    @Builder
    public SubmissiveInteractableNpc(long id,
                                     Coordinate coordinate,
                                     String name,
                                     Map<State, Integer> stateMillis,
                                     NonMonsterNpcAttributeProvider attributeProvider,
                                     RealmMap realmMap,
                                     NpcInteractor interactor,
                                     SubmissiveWanderingAI ai) {
        super(id, coordinate, Direction.DOWN, name, stateMillis, attributeProvider, realmMap, null, ai);
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
        interactor.onNpcClicked(player, this);
    }

    @Override
    public void interact(Player player, String name) {
        interactor.onInteractabilityClicked(player, this, name);
    }

    @Override
    public String shape() {
        return attributeProvider().shape();
    }

    @Override
    public int avatarImageId() {
        return ((NonMonsterNpcAttributeProvider)attributeProvider()).image();
    }

    @Override
    public String mainMenuDialog() {
        return interactor.getMainText();
    }
}
