package org.y1000.message.serverevent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.y1000.message.ServerMessage;
import org.y1000.network.gen.LoginPacket;
import org.y1000.network.gen.Packet;
import org.y1000.entities.Entity;
import org.y1000.entities.players.Player;
import org.y1000.message.serverevent.EntityEvent;
import org.y1000.message.serverevent.EntityEventHandler;
import org.y1000.message.serverevent.PlayerEventHandler;
import org.y1000.util.Coordinate;

@Builder
@AllArgsConstructor
public class JoinedRealmEvent implements EntityEvent, ServerMessage {

    private final Player player;

    private final Coordinate coordinate;

    @Override
    public Packet toPacket() {
        LoginPacket.Builder builder = LoginPacket.newBuilder()
                .setX(coordinate.x())
                .setY(coordinate.y())
                .setId(id());
        player.weapon().ifPresent(weapon -> builder.setWeaponShapeId(weapon.shapeId()));
        player.attackKungFu().ifPresent(attackKungFu -> builder.setAttackKungFuName(attackKungFu.name()).setAttackKungFuLevel(attackKungFu.level()));
        player.footKungFu().ifPresent(footKungFu -> builder.setFootKungFuLevel(footKungFu.level()).setFootKungFuName(footKungFu.name()));
        return Packet.newBuilder().setLoginPacket(
                builder.build()
        ).build();
    }


    @Override
    public void accept(EntityEventHandler visitor) {
        if (visitor instanceof PlayerEventHandler playerEventHandler) {
            playerEventHandler.handle(this);
        }
    }

    public Player player() {
        return (Player)source();
    }

    @Override
    public Entity source() {
        return player;
    }
}
