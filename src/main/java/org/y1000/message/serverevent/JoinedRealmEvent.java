package org.y1000.message.serverevent;

import org.y1000.entities.players.event.AbstractPlayerEvent;
import org.y1000.entities.players.event.PlayerAttributeEvent;
import org.y1000.entities.players.event.PlayerTeleportEvent;
import org.y1000.item.StackItem;
import org.y1000.kungfu.KungFu;
import org.y1000.item.Item;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.message.PlayerInfo;
import org.y1000.network.gen.*;
import org.y1000.entities.players.Player;
import org.y1000.realm.Realm;
import org.y1000.util.Coordinate;

public final class JoinedRealmEvent extends AbstractPlayerEvent{

    private final Inventory playerInventory;

    private final TeleportPacket teleportPacket;

    public JoinedRealmEvent(Player player, Coordinate coordinate, Inventory playerInventory, Realm realm) {
        super(player);
        this.playerInventory = playerInventory;
        this.teleportPacket = PlayerTeleportEvent.teleportPacket(realm, coordinate);
    }

    public JoinedRealmEvent(Player player) {
        this(player, player.coordinate(), player.inventory(), player.getRealm());
    }


    public static InventoryItemPacket toPacket(int index, Item item) {
        InventoryItemPacket.Builder builder = InventoryItemPacket.newBuilder()
                .setName(item.name())
                .setColor(item.color())
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
    protected Packet buildPacket() {
        var player = player();
        LoginPacket.Builder builder = LoginPacket.newBuilder()
                .setAttackKungFuName(player.attackKungFu().name())
                .setInfo(PlayerInfo.toPacket(player))
                .setAttribute(PlayerAttributeEvent.makeAttributePacket(player))
                .setTeleport(teleportPacket)
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
    public void accept(PlayerEventVisitor playerEventHandler) {
        playerEventHandler.visit(this);
    }
}
