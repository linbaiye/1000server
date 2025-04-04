package org.y1000.kungfu;


import lombok.Builder;
import lombok.Getter;
import org.y1000.entities.creatures.event.EntitySoundEvent;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.PlayerAttributeEvent;
import org.y1000.entities.players.event.PlayerGainExpEvent;
import org.y1000.entities.players.event.PlayerKungFuFullEvent;
import org.y1000.exp.ExperienceUtil;
import org.y1000.event.EntityEvent;
import org.y1000.util.UnaryAction;

@Getter
public final class FootKungFu extends AbstractPeriodicalConsumingKungFu {

    private final int sound;

    private int counter;

    private final EventResourceParameters eventResourceParameters;


    @Builder
    public FootKungFu(String name, int exp,
                      FiveSecondsParameters fiveSecondsParameters,
                      KeepParameters keepParameters, String sound,
                      EventResourceParameters eventResourceParameters) {
        super(name, exp, keepParameters, fiveSecondsParameters);
        this.sound = Integer.parseInt(sound);
        this.eventResourceParameters = eventResourceParameters;
        this.counter = 0;
    }

    public boolean canFly() {
        return level() >= 8501;
    }

    private static final int INI_SKILL_DIV_EVENT = 5000;


    private int applyLevelToValue(int value) {
        return value + value * level() / INI_SKILL_DIV_EVENT;
    }


    public void tryGainExpAndUseResources(Player player, UnaryAction<EntityEvent> eventSender) {
        if (++counter < 10) {
            return;
        }
        counter = 0;
        var snd = sound;
        if (level() >= 9000) {
            snd = sound + 2;
        } else if (level() >= 5000) {
            snd = sound + 1;
        }
        eventSender.invoke(new EntitySoundEvent(player, String.valueOf(snd)));
        if (gainPermittedExp(ExperienceUtil.DEFAULT_EXP)) {
            eventSender.invoke(new PlayerGainExpEvent(player, name(), level()));
            if (isLevelFull())
                eventSender.invoke(new PlayerKungFuFullEvent(player, this));
        }
        int life = applyLevelToValue(eventResourceParameters.life());
        int useLife =  player.currentLife() > life ? life : player.currentLife() - 1;
        player.consumeLife(useLife);
        player.consumeOuterPower(applyLevelToValue(eventResourceParameters.outerPower()));
        player.consumeInnerPower(applyLevelToValue(eventResourceParameters.innerPower()));
        player.consumePower(applyLevelToValue(eventResourceParameters.power()));
        eventSender.invoke(new PlayerAttributeEvent(player));
    }

    @Override
    public KungFuType kungFuType() {
        return KungFuType.FOOT;
    }

    @Override
    public String description() {
        return getDescriptionBuilder().toString();
    }

    @Override
    public KungFu duplicate() {
        return new FootKungFu(name(), 0, getConsumingParameters(), getKeepParameters(), String.valueOf(sound), getEventResourceParameters());
    }
}
