package org.y1000.kungfu;


import lombok.Builder;
import lombok.Getter;
import org.y1000.entities.creatures.event.CreatureSoundEvent;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.PlayerGainExpEvent;
import org.y1000.exp.Experience;
import org.y1000.message.serverevent.EntityEvent;
import org.y1000.util.UnaryAction;

@Getter
public final class FootKungFu extends AbstractConsumingResourcesKungFu {

    private final int sound;

    private int counter;

    @Builder
    public FootKungFu(String name, int exp,
                      FiveSecondsParameters fiveSecondsParameters,
                      KeepParameters keepParameters, String sound) {
        super(name, exp, keepParameters, fiveSecondsParameters);
        this.sound = Integer.parseInt(sound);
        this.counter = 0;
    }

    public boolean canFly() {
        return level() >= 8501;
    }

    public void tryGainExp(Player player, UnaryAction<EntityEvent> eventSender) {
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
        eventSender.invoke(new CreatureSoundEvent(player, String.valueOf(snd)));
        if (gainExp(Experience.DEFAULT_EXP)) {
            eventSender.invoke(new PlayerGainExpEvent(player, name(), level()));
        }
    }

    @Override
    public KungFuType kungFuType() {
        return KungFuType.FOOT;
    }
}
