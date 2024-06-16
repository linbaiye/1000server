package org.y1000.message.serverevent;

import org.y1000.entities.players.event.PlayerAttributeEvent;
import org.y1000.kungfu.KungFu;
import org.y1000.item.Item;
import org.y1000.item.StackItem;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.message.PlayerInfo;
import org.y1000.message.ServerMessage;
import org.y1000.network.gen.InventoryItemPacket;
import org.y1000.network.gen.KungFuPacket;
import org.y1000.network.gen.LoginPacket;
import org.y1000.network.gen.Packet;
import org.y1000.entities.PhysicalEntity;
import org.y1000.entities.players.Player;
import org.y1000.util.Coordinate;

public final class JoinedRealmEvent implements EntityEvent, ServerMessage {

    private final Player player;

    private final Coordinate coordinate;

    private final Inventory playerInventory;

    public JoinedRealmEvent(Player player, Coordinate coordinate, Inventory playerInventory) {
        this.player = player;
        this.coordinate = coordinate;
        this.playerInventory = playerInventory;
    }

    private InventoryItemPacket toPacket(int index, Item item) {
        InventoryItemPacket.Builder builder = InventoryItemPacket.newBuilder()
                .setName(item.name())
                .setSlotId(index);
        if (item instanceof StackItem stackItem) {
            builder.setNumber(stackItem.number());
        }
        return builder.build();
    }

    private KungFuPacket toPacket(int index, KungFu kungFu) {
        return KungFuPacket.newBuilder()
                .setName(kungFu.name())
                .setLevel(kungFu.level())
                .setType(kungFu.kungFuType().value())
                .setSlot(index)
                .build();
    }

    @Override
    public Packet toPacket() {
        LoginPacket.Builder builder = LoginPacket.newBuilder()
                .setX(coordinate.x())
                .setY(coordinate.y())
                .setAttackKungFuName(player.attackKungFu().name())
                .setInfo(PlayerInfo.toPacket(player))
                .setAttribute(PlayerAttributeEvent.makeAttributePacket(player))
                ;
        player.footKungFu().ifPresent(footKungFu -> builder.setFootKungFuName(footKungFu.name()));
        player.protectKungFu().ifPresent(protectKungFu -> builder.setProtectionKungFu(protectKungFu.name()));
        player.assistantKungFu().ifPresent(assistantKungFu -> builder.setAssistantKungFu(assistantKungFu.name()));
        playerInventory.foreach((index, item) -> builder.addInventoryItems(toPacket(index, item)));
        player.kungFuBook().foreachUnnamed((slot, kungFu) -> builder.addUnnamedKungFuList(toPacket(slot, kungFu)));
        player.kungFuBook().foreachBasic((slot, kungFu) -> builder.addBasicKungFuList(toPacket(slot, kungFu)));
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
