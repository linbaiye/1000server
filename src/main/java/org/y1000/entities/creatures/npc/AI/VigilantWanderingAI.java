package org.y1000.entities.creatures.npc.AI;

/*
@Slf4j
public final class VigilantWanderingAI extends AbstractMonsterWanderingAI {

    private void escape(ViolentNpc violentNpc, ViolentCreature enemy) {
        violentNpc.changeAndStartAI(new EscapeAI(enemy));
    }

    @Override
    protected void onMonsterActionDone(Monster npc) {
        if (npc.npcState() instanceof NpcHurtState hurtState) {
//            escape(npc, hurtState.attacker());
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
    protected Coordinate random(INpc npc) {
        return npc.wanderingArea().randomOutSpawnScope(npc.coordinate());
    }

    @Override
    protected Logger log() {
        return log;
    }
}*/
