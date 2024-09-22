package org.y1000.message;

import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.PlayerNameColorEvent;
import org.y1000.network.gen.PlayerInfoPacket;

public final class PlayerInfo {

    public static PlayerInfoPacket toPacket(Player player) {
        PlayerInfoPacket.Builder builder = PlayerInfoPacket.newBuilder()
                .setId(player.id())
                .setName(player.viewName())
                .setNameColor(PlayerNameColorEvent.toColor(player.team()))
                .setMale(player.isMale());
        player.weapon().ifPresent(weapon -> builder.setWeaponName(weapon.name()));
        player.hat().ifPresent(hat -> {
            builder.setHatName(hat.name());
            builder.setHatColor(hat.color());
        });
        player.chest().ifPresent(chest -> {
            builder.setChestName(chest.name());
            builder.setChestColor(chest.color());
        });
        player.hair().ifPresent(h -> {
            builder.setHairName(h.name());
            builder.setHairColor(h.color());
        });
        player.wrist().ifPresent(w -> {
            builder.setWristName(w.name());
            builder.setWristColor(w.color());
        });
        player.boot().ifPresent(b -> {
            builder.setBootName(b.name());
            builder.setBootColor(b.color());
        });
        player.trouser().ifPresent(t -> {
            builder.setTrouserName(t.name());
            builder.setTrouserColor(t.color());
        });
        player.clothing().ifPresent(c -> {
            builder.setClothingName(c.name());
            builder.setClothingColor(c.color());
        });
        return builder.build();
    }
}
