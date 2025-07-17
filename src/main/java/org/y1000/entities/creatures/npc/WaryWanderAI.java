package org.y1000.entities.creatures.npc;

import org.y1000.entities.ActiveEntity;
import org.y1000.entities.FilterVisibleEvent;
import org.y1000.entities.players.Player;
import org.y1000.util.Coordinate;

import java.util.Comparator;
import java.util.Optional;


public class WaryWanderAI extends AbstractWanderingAI {

    private final EscapeAbility escapeAbility;

    public WaryWanderAI(Npc npc, NpcAbility from, EscapeAbility escapeAbility) {
        super(npc);
        this.escapeAbility = escapeAbility;
        changeAbility(from);
        npc.findAbility(NpcHurtAbility.class).ifPresent(a -> a.setHurtTrigger(this::onAttacked));
    }

    private Optional<Player> findNearestViewRangePlayer() {
        var event = FilterVisibleEvent.nearbyAlive(npc(), npc().getWanderRage());
        npc().sendEvent(event);
        var c = npc().coordinate();
        return event.resultStream(Player.class).min(Comparator.comparing(p -> p.coordinate().directDistance(c)));
    }

    @Override
    void onNonDieAbilityDone(NpcAbility ability) {
        findNearestViewRangePlayer().ifPresentOrElse(
                p -> npc().startAI(new EscapeAI(npc(), p, currentAbility(), escapeAbility)),
                () -> continueWander(ability));
    }

    @Override
    Coordinate getWanderOrigin() {
        return npc().coordinate();
    }

    private void onAttacked(ActiveEntity attacker, NpcHurtAbility ability) {
        applyHurtAbility(ability);
        npc().startAI(new EscapeAI(npc(), attacker, currentAbility(), escapeAbility));
    }

    @Override
    public void update(int delta) {
        updateAbility(delta);
    }

}
