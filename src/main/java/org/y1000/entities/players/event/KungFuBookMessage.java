package org.y1000.entities.players.event;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.Player;
import org.y1000.event.EntityEvent;
import org.y1000.kungfu.KungFu;
import org.y1000.message.AbstractPlayerMessage;
import org.y1000.message.serverevent.Abstract2ClientEvent;
import org.y1000.network.gen.KungFuBookPacket;
import org.y1000.network.gen.KungFuPacket;
import org.y1000.network.gen.Packet;

public final class KungFuBookMessage extends AbstractPlayerMessage  {
    private KungFuBookMessage(Player player, Packet packet) {
        super(player, packet);
    }


    private static KungFuPacket toPacket(int slot, KungFu kungFu) {
        return KungFuPacket.newBuilder()
                .setName(kungFu.name())
                .setSlot(slot)
                .setLevel(kungFu.level())
                .setIcon(kungFu.icon())
                .build();
    }

    public static KungFuBookMessage forPlayer(Player player) {
        Validate.notNull(player);
        KungFuBookPacket.Builder builder = KungFuBookPacket.newBuilder();
        player.kungFuBook().foreachUnnamed((slot, k) -> builder.addUnnamedKungFuList(toPacket(slot, k)));
        player.kungFuBook().foreachBasic((slot, k) -> builder.addBasicKungFuList(toPacket(slot, k)));
        return new KungFuBookMessage(player, Packet.newBuilder().setKungFuBook(builder.build()).build());
    }

}
