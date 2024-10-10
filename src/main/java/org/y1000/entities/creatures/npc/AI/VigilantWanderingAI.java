package org.y1000.entities.creatures.npc.AI;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.creatures.ViolentCreature;
import org.y1000.entities.creatures.event.SeekPlayerEvent;
import org.y1000.entities.creatures.monster.Monster;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.entities.creatures.npc.NpcHurtState;
import org.y1000.entities.creatures.npc.ViolentNpc;
import org.y1000.entities.players.Player;
import org.y1000.util.Coordinate;

import java.util.Comparator;
import java.util.function.Function;

@Slf4j
public final class VigilantWanderingAI extends AbstractMonsterWanderingAI {

    private void escape(ViolentNpc violentNpc, ViolentCreature enemy) {
        violentNpc.changeAndStartAI(new EscapeAI(enemy));
    }

    @Override
    protected void onMonsterActionDone(Monster npc) {
        if (npc.state() instanceof NpcHurtState hurtState) {
            escape(npc, hurtState.attacker());
            return;
        }
        SeekPlayerEvent event = new SeekPlayerEvent(npc);
        npc.emitEvent(event);
        Function<Player, Integer> distance = (player) -> player.coordinate().directDistance(npc.coordinate());
        event.getPlayers().stream().filter(player -> distance.apply(player) < npc.viewWidth())
                .min(Comparator.comparing(distance))
                .ifPresentOrElse(p -> escape(npc,p), () -> continueWander(npc));
    }

    @Override
    protected Coordinate random(Npc npc) {
        return npc.wanderingArea().randomOutSpawnScope(npc.coordinate());
    }

    @Override
    protected Logger log() {
        return log;
    }
}
