package org.y1000.message;

import org.y1000.entities.players.Player;
import org.y1000.network.gen.PlayerInfoPacket;

public class PlayerInfo {

    public static PlayerInfoPacket toPacket(Player player) {
        PlayerInfoPacket.Builder builder = PlayerInfoPacket.newBuilder()
                .setId(player.id())
                .setName(player.name())
                .setMale(player.isMale());
        player.weapon().ifPresent(weapon -> builder.setWeaponName(weapon.name()));
        player.hat().ifPresent(hat -> builder.setHatName(hat.name()));
        player.chest().ifPresent(chest -> builder.setChestName(chest.name()));
        player.hair().ifPresent(h -> builder.setHairName(h.name()));
        player.wrist().ifPresent(w -> builder.setWristName(w.name()));
        player.boot().ifPresent(b -> builder.setBootName(b.name()));
        player.trouser().ifPresent(t -> builder.setTrouserName(t.name()));
        player.clothing().ifPresent(c -> builder.setClothingName(c.name()));
        return builder.build();
    }
}
