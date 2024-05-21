package org.y1000.message.serverevent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.y1000.entities.item.Item;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.message.ServerMessage;
import org.y1000.network.gen.InventoryItemPacket;
import org.y1000.network.gen.LoginPacket;
import org.y1000.network.gen.Packet;
import org.y1000.entities.PhysicalEntity;
import org.y1000.entities.players.Player;
import org.y1000.util.Coordinate;

@Builder
@AllArgsConstructor
public class JoinedRealmEvent implements EntityEvent, ServerMessage {

    private final Player player;

    private final Coordinate coordinate;


    private InventoryItemPacket toPacket(int index, Item item) {
        return InventoryItemPacket.newBuilder()
                .setItemType(item.type().value())
                .setName(item.name())
                .setSlotId(index)
                .setId(item.id())
                .build();
    }


    @Override
    public Packet toPacket() {
        LoginPacket.Builder builder = LoginPacket.newBuilder()
                .setX(coordinate.x())
                .setY(coordinate.y())
                .setId(source().id())
                .setAttackKungFuLevel(player.attackKungFu().level())
                .setAttackKungFuName(player.attackKungFu().name());
        player.weapon().ifPresent(weapon -> builder.setWeaponName(weapon.name()));
        player.footKungFu().ifPresent(footKungFu -> builder.setFootKungFuLevel(footKungFu.level()).setFootKungFuName(footKungFu.name()));
        player.inventory().foreach((index, item) -> builder.addInventoryItems(toPacket(index, item)));
        return Packet.newBuilder().setLoginPacket(builder.build()).build();
    }


    @Override
    public void accept(EntityEventVisitor visitor) {
        if (visitor instanceof PlayerEventVisitor playerEventHandler) {
            playerEventHandler.visit(this);
        }
    }

    public Player player() {
        return (Player)source();
    }

    @Override
    public PhysicalEntity source() {
        return player;
    }
}
