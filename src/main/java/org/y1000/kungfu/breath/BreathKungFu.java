package org.y1000.kungfu.breath;

import lombok.Builder;
import org.y1000.entities.creatures.event.EntitySoundEvent;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.PlayerAttributeEvent;
import org.y1000.entities.players.event.PlayerGainExpEvent;
import org.y1000.entities.players.event.PlayerKungFuFullEvent;
import org.y1000.exp.ExperienceUtil;
import org.y1000.kungfu.AbstractKungFu;
import org.y1000.kungfu.EventResourceParameters;
import org.y1000.kungfu.KungFu;
import org.y1000.kungfu.KungFuType;
import org.y1000.event.EntityEvent;
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

    @Override
    public String description() {
        return getDescriptionBuilder().toString();
    }

    @Override
    public KungFu duplicate() {
        return new BreathKungFu(name(), 0, parameters, String.valueOf(sound));
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

    private int computeResource(int playerMaxResource, int inParameter) {
        if (inParameter == 0) {
            return 0;
        }
        int max = playerMaxResource / (6 + (12000 - level()) * 14 / 12000);
        return 5 * max * inParameter / 100 ;
    }

    public void update(Player player, int delta, UnaryAction<? super EntityEvent> eventSender) {
        timer -= delta;
        if (timer > 0) {
            return;
        }
        setTimer();
        /*if (!canRegenerateResources(player)) {
            return;
        }*/
        eventSender.invoke(new EntitySoundEvent(player, computeSound(player.isMale())));
        player.gainLife(computeResource(player.maxLife(), parameters.life()));
        player.gainPower(computeResource(player.maxPower(), parameters.power()));
        player.gainInnerPower(computeResource(player.maxInnerPower(), parameters.innerPower()));
        player.gainOuterPower(computeResource(player.maxOuterPower(), parameters.outerPower()));
        eventSender.invoke(new PlayerAttributeEvent(player));
        if (gainPermittedExp(ExperienceUtil.DEFAULT_EXP)) {
            eventSender.invoke(new PlayerGainExpEvent(player, name(), level()));
            if (isLevelFull())
                eventSender.invoke(new PlayerKungFuFullEvent(player, this));
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
