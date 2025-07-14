package org.y1000.entities.creatures.npc;

import org.y1000.entities.ActiveEntity;
import org.y1000.entities.creatures.npc.event.FilterNearbyPlayerEvent;
import org.y1000.entities.players.Player;
import org.y1000.util.Coordinate;

import java.util.Optional;
import java.util.Set;


public class WaryWanderAI extends AbstractWanderingAI {

    public WaryWanderAI(Npc npc, NpcAbility from) {
        super(npc);
        changeAbility(from);
        npc.findAbility(NpcHurtAbility.class).ifPresent(a -> a.setHurtTrigger(this::onAttacked));
    }

    private Optional<Player> findNearestViewRangePlayer() {
        FilterNearbyPlayerEvent event = FilterNearbyPlayerEvent.withinDistance(npc(), npc().getWanderRage());
        npc().sendEvent(event);
        Set<Player> players = event.players();
        return players.stream().min((o1, o2) -> o1.coordinate().directDistance(npc().coordinate()) - o2.coordinate().directDistance(npc().coordinate()));
    }

    @Override
    void onNonDieAbilityDone(NpcAbility ability) {
        findNearestViewRangePlayer().ifPresentOrElse(p -> npc().startAI(new EscapeAI(npc(), p, currentAbility())),
                () -> continueWander(ability));
    }

    @Override
    Coordinate getWanderOrigin() {
        return npc().coordinate();
    }

    private void onAttacked(ActiveEntity attacker, NpcHurtAbility ability) {
        applyHurtAbility(ability);
    }

    @Override
    public void update(int delta) {
        updateAbility(delta);
    }

}
