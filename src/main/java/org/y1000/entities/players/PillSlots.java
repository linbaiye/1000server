package org.y1000.entities.players;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.creatures.event.EntitySoundEvent;
import org.y1000.entities.players.event.PlayerAttributeEvent;
import org.y1000.item.Pill;
import org.y1000.message.PlayerTextEvent;

public final class PillSlots {

    private static class PillSlot {
        private int counter;
        private int timeLeft;
        private final Pill pill;

        private PillSlot(Pill pill) {
            this.pill = pill;
            this.counter = pill.useCount();
            this.timeLeft = pill.useInterval();
        }

        public void update(long delta) {
            timeLeft -= delta;
        }

        public boolean isTimeToRegen() {
            return timeLeft <= 0;
        }

        public boolean isEffective() {
            return counter > 0;
        }

        public void decEffectiveCounter() {
            counter--;
        }

        public void resetTimer() {
            timeLeft = pill.useInterval();
        }

        public Pill pill() {
            return pill;
        }
    }

    private static final int PillSlotSize = 3;

    private final PillSlot[] pillSlots = new PillSlot[PillSlotSize];

    public void usePill(Player player, Pill pill) {
        Validate.notNull(player);
        for (int i = 0; i < pillSlots.length; i++) {
            if (pillSlots[i] == null) {
                player.emitEvent(PlayerTextEvent.havePill(player, pill.name()));
                pill.eventSound().ifPresent(s -> player.emitEvent(new EntitySoundEvent(player, s)));
                pillSlots[i] = new PillSlot(pill);
                return;
            }
        }
        player.emitEvent(PlayerTextEvent.noMorePill(player));
    }

    public void update(Player player, long delta) {
        Validate.notNull(player);
        boolean needSync = false;
        for (int i = 0; i < pillSlots.length; i++) {
            if (pillSlots[i] == null) {
                continue;
            }
            PillSlot pillSlot = pillSlots[i];
            pillSlot.update(delta);
            if (pillSlot.isTimeToRegen()) {
                Pill pill = pillSlot.pill();
                player.gainLife(pill.life());
                player.gainHeadLife(pill.headLife());
                player.gainArmLife(pill.armLife());
                player.gainLegLife(pill.legLife());
                player.gainPower(pill.power());
                player.gainInnerPower(pill.innerPower());
                player.gainOuterPower(pill.outerPower());
                pillSlot.decEffectiveCounter();
                pillSlot.resetTimer();
                needSync = true;
            }
            if (!pillSlot.isEffective()) {
                pillSlots[i] = null;
            }
        }
        if (needSync)
            player.emitEvent(new PlayerAttributeEvent(player));
    }
}
