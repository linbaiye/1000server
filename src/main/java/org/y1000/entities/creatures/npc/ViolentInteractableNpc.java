package org.y1000.entities.creatures.npc;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.NpcType;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.npc.AI.NpcAI;
import org.y1000.entities.creatures.npc.AI.ViolentNpcWanderingAI;
import org.y1000.entities.creatures.npc.interactability.NpcInteractability;
import org.y1000.entities.creatures.npc.interactability.NpcInteractor;
import org.y1000.entities.players.Player;
import org.y1000.message.AbstractEntityInterpolation;
import org.y1000.message.NpcInterpolation;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

@Slf4j
public final class ViolentInteractableNpc extends AbstractViolentNpc implements InteractableNpc {
    private final NpcInteractor interactor;
    @Builder
    public ViolentInteractableNpc(long id,
                                     Coordinate coordinate,
                                     String name,
                                     Map<State, Integer> stateMillis,
                                     NonMonsterNpcAttributeProvider attributeProvider,
                                     RealmMap realmMap,
                                     NpcInteractor interactor,
                                     NpcAI ai) {
        super(id, coordinate, Direction.DOWN, name, stateMillis, attributeProvider, realmMap, ai, null, null);
        Validate.notNull(interactor);
        this.interactor = interactor;
    }

    @Override
    public AbstractEntityInterpolation captureInterpolation() {
        return new NpcInterpolation(id(), coordinate(), state().stateEnum(), direction(), state().elapsedMillis(), viewName(),
                NpcType.INTERACTABLE, attributeProvider().animate(), attributeProvider().shape());
    }

    @Override
    protected Logger log() {
        return log;
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

    @Override
    public Optional<NpcInteractability> findFirstInteractability(Predicate<? super NpcInteractability> predicate) {
        return interactor.findFirstInteractbility(predicate);
    }

    @Override
    public void startIdleAI() {
        changeAndStartAI(new ViolentNpcWanderingAI(spawnCoordinate()));
    }
}
