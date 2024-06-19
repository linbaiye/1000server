package org.y1000.kungfu.breath;

import lombok.Builder;
import org.y1000.entities.creatures.event.CreatureSoundEvent;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.PlayerAttributeEvent;
import org.y1000.entities.players.event.PlayerGainExpEvent;
import org.y1000.exp.ExperienceUtil;
import org.y1000.kungfu.AbstractKungFu;
import org.y1000.kungfu.EventResourceParameters;
import org.y1000.kungfu.KungFuType;
import org.y1000.message.serverevent.EntityEvent;
import org.y1000.util.UnaryAction;

public final class BreathKungFu extends AbstractKungFu {
    private final EventResourceParameters parameters;

    private int timer;

    private final int sound;

    public static final int DEFAULT_TIMER_MILLIS = 5000;

    @Builder
    public BreathKungFu(String name, int exp,
                        EventResourceParameters parameters,
                        String sound) {
        super(name, exp);
        this.parameters = parameters;
        this.sound = Integer.parseInt(sound);
        setTimer();
    }

    private void setTimer() {
        timer = DEFAULT_TIMER_MILLIS;
    }

    @Override
    public KungFuType kungFuType() {
        return KungFuType.BREATHING;
    }

    private String computeSound(boolean male) {
        var snd = sound;
        if (level() >= 9000) {
            snd = sound + 4;
        } else if (level() >= 5000) {
            snd = sound + 2;
        }
        return String.valueOf(!male ? snd + 1: snd);
    }

    private int computeResource(int playerCurrentResource, int inParameter) {
        if (inParameter == 0) {
            return 0;
        }
        int max = playerCurrentResource / (6 + (12000 - level()) * 14 / 12000);
        return max * inParameter / 100;
    }

    public void update(Player player, int delta, UnaryAction<? super EntityEvent> eventSender) {
        timer -= delta;
        if (timer > 0) {
            return;
        }
        setTimer();
        if (!canRegenerateResources(player)) {
            return;
        }
        eventSender.invoke(new CreatureSoundEvent(player, computeSound(player.isMale())));
        player.gainLife(computeResource(player.currentLife(), parameters.life()));
        player.gainPower(computeResource(player.power(), parameters.power()));
        player.gainInnerPower(computeResource(player.innerPower(), parameters.innerPower()));
        player.gainOuterPower(computeResource(player.outerPower(), parameters.outerPower()));
        eventSender.invoke(new PlayerAttributeEvent(player));
        if (gainExp(ExperienceUtil.DEFAULT_EXP)) {
            eventSender.invoke(new PlayerGainExpEvent(player, name(), level()));
        }
    }

    public boolean canRegenerateResources(Player player) {
        if (parameters.power() > 0 && player.power() < player.maxPower()) {
            return true;
        }
        if (parameters.innerPower() > 0 && player.innerPower() < player.maxInnerPower()) {
            return true;
        }
        if (parameters.outerPower() > 0 && player.outerPower() < player.maxOuterPower()) {
            return true;
        }
        return parameters.life() > 0 && player.currentLife() < player.maxLife();
    }
}
