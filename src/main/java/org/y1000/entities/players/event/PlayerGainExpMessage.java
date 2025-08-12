package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.kungfu.KungFu;
import org.y1000.kungfu.attack.AttackKungFu;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.PlayerGainExpPacket;

public final class PlayerGainExpMessage extends Abstract2PlayerMessageEvent {


    public PlayerGainExpMessage(Player player, Packet packet) {
        super(player, packet);
    }

    private static Packet buildPacket(boolean kungFu, String name, int level, boolean attack) {
        return Packet.newBuilder()
                .setGainExp(PlayerGainExpPacket.newBuilder()
                        .setKungFu(kungFu)
                        .setName(name)
                        .setLevel(level)
                        .setAttack(attack)
                        .build())
                .build();
    }

    public static PlayerGainExpMessage of(Player player, KungFu kungFu) {
        return new PlayerGainExpMessage(player, buildPacket(true, kungFu.name(), kungFu.level(), kungFu instanceof AttackKungFu));
    }

    public static PlayerGainExpMessage nonKungFu(Player player, String name) {
        return new PlayerGainExpMessage(player, buildPacket(false, name, 0, false));
    }
}
