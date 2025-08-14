package org.y1000.entities;

import lombok.Builder;
import org.y1000.guild.GuildStone;
import org.y1000.network.I2ClientMessage;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.ShowItemPacket;
import org.y1000.util.Coordinate;

public final class GroundItemSnapshot implements I2ClientMessage {
    private final String name;
    private final Integer number;
    private Packet packet;

    private final int color;

    private final int icon;
    private final long id;
    private final Coordinate coordinate;
    private final boolean stone;
    private final boolean demo;


    @Builder
    public GroundItemSnapshot(long id, Coordinate coordinate, String name, Integer number, int color, int icon, boolean guildStone, boolean demo) {
        this.id = id;
        this.coordinate = coordinate;
        this.name = name;
        this.number = number;
        this.color = color;
        this.icon = icon;
        this.stone = guildStone;
        this.demo = demo;
    }

    public static GroundItemSnapshot ofDemo(GuildStone guildStone) {
        return new GroundItemSnapshot(guildStone.id(), guildStone.coordinate(), guildStone.guildName(), 1, 0, guildStone.getIcon(),true,true);
    }

    public static GroundItemSnapshot of(GuildStone guildStone) {
        return new GroundItemSnapshot(guildStone.id(), guildStone.coordinate(), guildStone.guildName(), 1, 0, guildStone.getIcon(),true,false);
    }

    @Override
    public Packet toPacket() {
        if (packet != null) {
            return packet;
        }
        ShowItemPacket.Builder showItemBuidler = ShowItemPacket.newBuilder()
                .setCoordinateX(coordinate.x())
                .setCoordinateY(coordinate.y())
                .setName(name)
                .setColor(color)
                .setIcon(icon)
                .setGuildStone(stone)
                .setDemo(demo)
                .setId(id);
        if (number != null) {
            showItemBuidler.setNumber(number);
        }
        packet = Packet.newBuilder().setShowItem(showItemBuidler).build();
        return packet;
    }
}
