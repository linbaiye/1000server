package org.y1000.entities.players.event;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.Player;
import org.y1000.realm.PlayerEventHandler;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.SyncActiveKungFuPacket;

public final class SyncActiveKungMessage extends AbstractMessagePlayerEvent {

    private SyncActiveKungMessage(Player player, Packet packet) {
        super(player, packet);
    }


    public static SyncActiveKungMessage of(Player player) {
        Validate.notNull(player);
        SyncActiveKungFuPacket.Builder builder = SyncActiveKungFuPacket.newBuilder();
        builder.setAttackKungFu(player.attackKungFu().name()).setId(player.id()).setAttackKungFuLevel(player.attackKungFu().level());
        player.footKungFu().ifPresent(footKungFu -> builder.setFootKungFu(footKungFu.name()).setFootKungFuCanFly(footKungFu.canFly()));
        player.protectKungFu().ifPresent(protectKungFu -> builder.setProtectionKungFu(protectKungFu.name()));
        player.assistantKungFu().ifPresent(assistantKungFu -> builder.setAssistantKungFu(assistantKungFu.name()));
        player.breathKungFu().ifPresent(kungFu -> builder.setBreathKungFu(kungFu.name()));
        return new SyncActiveKungMessage(player, Packet.newBuilder().setActiveKungFuList(builder).build());
    }

    @Override
    public void accept(PlayerEventHandler handler) {
        handler.sendTo(source(), this);
    }
}
