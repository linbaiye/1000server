package org.y1000.kungfu;


import lombok.Builder;
import lombok.Getter;
import org.y1000.entities.players.event.PlayerSoundEvent;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.PlayerAttributeEvent;
import org.y1000.exp.ExperienceUtil;

@Getter
public final class FootKungFu extends AbstractPeriodicalConsumingKungFu {

    private final int sound;

    private int counter;

    private final EventResourceParameters eventResourceParameters;


    @Builder
    public FootKungFu(String name, int exp,
                      FiveSecondsParameters fiveSecondsParameters,
                      KeepParameters keepParameters, String sound,
                      EventResourceParameters eventResourceParameters, int icon) {
        super(name, exp, keepParameters, fiveSecondsParameters, icon);
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


    public void tryGainExpAndUseResources(Player player) {
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
        player.sendEvent(PlayerSoundEvent.toAll(player, String.valueOf(snd)));
        gainExp(player, ExperienceUtil.DEFAULT_EXP);
        int life = applyLevelToValue(eventResourceParameters.life());
        int useLife =  player.currentLife() > life ? life : player.currentLife() - 1;
        player.consumeLife(useLife);
        player.consumeOuterPower(applyLevelToValue(eventResourceParameters.outerPower()));
        player.consumeInnerPower(applyLevelToValue(eventResourceParameters.innerPower()));
        player.consumePower(applyLevelToValue(eventResourceParameters.power()));
        player.sendEvent(PlayerAttributeEvent.of(player));
    }

    @Override
    public KungFuType kungFuType() {
        return KungFuType.FOOT;
    }

    @Override
    public String detailText() {
        return getDescriptionBuilder().toString();
    }

    @Override
    public KungFu duplicate() {
        return new FootKungFu(name(), 0, getConsumingParameters(), getKeepParameters(), String.valueOf(sound), getEventResourceParameters(), icon());
    }
}
