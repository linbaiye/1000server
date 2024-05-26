package org.y1000.message.serverevent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.y1000.item.Item;
import org.y1000.item.StackItem;
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
public final class JoinedRealmEvent implements EntityEvent, ServerMessage {

    private final Player player;

    private final Coordinate coordinate;

    private final Inventory playerInventory;

    private InventoryItemPacket toPacket(int index, Item item) {
        InventoryItemPacket.Builder builder = InventoryItemPacket.newBuilder()
                .setName(item.name())
                .setSlotId(index);
        if (item instanceof StackItem stackItem) {
            builder.setNumber(stackItem.number());
        }
        return builder.build();
    }


    @Override
    public Packet toPacket() {
        LoginPacket.Builder builder = LoginPacket.newBuilder()
                .setX(coordinate.x())
                .setY(coordinate.y())
                .setId(source().id())
                .setName(player().name())
                .setMale(player.isMale())
                .setAttackKungFuLevel(player.attackKungFu().level())
                .setAttackKungFuName(player.attackKungFu().name())
                .setAttackKungFuType(player.attackKungFu().getType().value());
        player.weapon().ifPresent(weapon -> builder.setWeaponName(weapon.name()));
        player.footKungFu().ifPresent(footKungFu -> builder.setFootKungFuLevel(footKungFu.level()).setFootKungFuName(footKungFu.name()));
        playerInventory.foreach((index, item) -> builder.addInventoryItems(toPacket(index, item)));
        player.hat().ifPresent(hat -> builder.setHatName(hat.name()));
        player.chest().ifPresent(chest -> builder.setChestName(chest.name()));
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
