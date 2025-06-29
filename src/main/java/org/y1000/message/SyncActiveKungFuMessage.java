package org.y1000.message;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.Player;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.SyncActiveKungFuPacket;

public final class SyncActiveKungFuMessage extends AbstractPlayerMessage {

    private SyncActiveKungFuMessage(Player player, Packet packet) {
        super(player, packet);
    }


    public static SyncActiveKungFuMessage of(Player player) {
        Validate.notNull(player);
        SyncActiveKungFuPacket.Builder builder = SyncActiveKungFuPacket.newBuilder();
        builder.setAttackKungFu(player.attackKungFu().name()).setId(player.id());
        player.footKungFu().ifPresent(footKungFu -> builder.setFootKungFu(footKungFu.name()).setFootKungFuCanFly(footKungFu.canFly()));
        player.protectKungFu().ifPresent(protectKungFu -> builder.setProtectionKungFu(protectKungFu.name()));
        player.assistantKungFu().ifPresent(assistantKungFu -> builder.setAssistantKungFu(assistantKungFu.name()));
        player.breathKungFu().ifPresent(kungFu -> builder.setBreathKungFu(kungFu.name()));
        return new SyncActiveKungFuMessage(player, Packet.newBuilder().setActiveKungFuList(builder).build());
    }
}
