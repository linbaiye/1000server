package org.y1000.entities.players.event;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.Player;
import org.y1000.item.BuffPill;
import org.y1000.message.serverevent.Visibility;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.UpdateBuffPacket;

public final class UpdateBuffEvent extends AbstractPlayerEvent {

    private static final int GAIN = 1;

    private static final int FADE = 2;

    private final Packet packet;

    private UpdateBuffEvent(Player source, BuffPill pill) {
        super(source, Visibility.SELF);
        if (pill == null) {
            packet = Packet.newBuilder()
                    .setUpdateBuff(UpdateBuffPacket.newBuilder().setType(FADE))
                    .build();
        } else {
            packet = Packet.newBuilder()
                    .setUpdateBuff(UpdateBuffPacket.newBuilder().setType(GAIN)
                            .setSeconds(pill.getLastingSeconds())
                            .setDescription(pill.description())
                            .setIcon(pill.getIconId())
                            .setColor(pill.color())
                    ).build();
        }
    }


    public static UpdateBuffEvent gain(Player player, BuffPill pill) {
        Validate.notNull(player);
        Validate.notNull(pill);
        return new UpdateBuffEvent(player, pill);
    }

    public static UpdateBuffEvent fade(Player player) {
        Validate.notNull(player);
        return new UpdateBuffEvent(player, null);
    }

    @Override
    protected Packet buildPacket() {
        return packet;
    }
}
