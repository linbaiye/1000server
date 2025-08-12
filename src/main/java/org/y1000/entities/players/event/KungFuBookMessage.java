package org.y1000.entities.players.event;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.Player;
import org.y1000.kungfu.KungFu;
import org.y1000.network.gen.KungFuBookPacket;
import org.y1000.network.gen.KungFuPacket;
import org.y1000.network.gen.Packet;

public final class KungFuBookMessage extends Abstract2PlayerMessageEvent {
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

    private static KungFuBookMessage forPlayer(Player player, boolean forceful) {
        Validate.notNull(player);
        KungFuBookPacket.Builder builder = KungFuBookPacket.newBuilder();
        player.kungFuBook().foreachUnnamed((slot, k) -> builder.addUnnamedKungFuList(toPacket(slot, k)));
        player.kungFuBook().foreachBasic((slot, k) -> builder.addBasicKungFuList(toPacket(slot, k)));
        builder.setForceful(forceful);
        return new KungFuBookMessage(player, Packet.newBuilder().setKungFuBook(builder.build()).build());
    }

    public static KungFuBookMessage forceful(Player player) {
        return forPlayer(player, true);
    }

    public static KungFuBookMessage quietly(Player player) {
        return forPlayer(player, false);
    }
}
