package org.y1000.message.serverevent;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.creatures.npc.InteractableNpc;
import org.y1000.entities.creatures.npc.MerchantItem;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.AbstractPlayerEvent;
import org.y1000.network.gen.MerchantMenuPacket;
import org.y1000.network.gen.NpcInteractionMenuPacket;
import org.y1000.network.gen.NpcItemPacket;
import org.y1000.network.gen.Packet;

import java.util.List;
import java.util.stream.Collectors;

public final class InteractionMenuEvent extends AbstractPlayerEvent {

    private final Packet packet;

    private InteractionMenuEvent(Player source,
                                Packet packet) {
        super(source, Visibility.SELF);
        this.packet = packet;
    }

    @Override
    protected Packet buildPacket() {
        return packet;
    }

    public static InteractionMenuEvent mainMenu(Player source, InteractableNpc npc, List<String> interactabilities) {
        Validate.notNull(source);
        Validate.notNull(npc);
        Validate.notEmpty(interactabilities);
        var packet = Packet.newBuilder()
                .setInteractionMenu(NpcInteractionMenuPacket.newBuilder()
                        .setId(npc.id())
                        .setText(npc.mainMenuDialog())
                        .setShape(npc.shape())
                        .setAvatarIdx(npc.avatarImageId())
                        .setViewName(npc.viewName())
                        .addAllInteractions(interactabilities)
                ).build();
        return new InteractionMenuEvent(source, packet);
    }


    public static InteractionMenuEvent sellingMenu(Player source, InteractableNpc npc,
                                                   String dialog, List<MerchantItem> items) {
        return merchantMenu(source, npc, dialog, items, true);
    }


    private static InteractionMenuEvent merchantMenu(Player source, InteractableNpc npc,
                                                     String dialog, List<MerchantItem> items, boolean sell) {
        Validate.notNull(source);
        Validate.notNull(npc);
        Validate.notEmpty(items);
        var list = items.stream().map(i -> NpcItemPacket.newBuilder()
                .setCanStack(i.canStack())
                .setName(i.name())
                .setColor(i.getColor())
                .setIcon(i.getIcon())
                .setPrice(i.price())
                .build()).collect(Collectors.toList());
        var packet = Packet.newBuilder()
                .setMerchantMenu(MerchantMenuPacket.newBuilder()
                        .setId(npc.id())
                        .setText(dialog != null ? dialog : "")
                        .setShape(npc.shape())
                        .setAvatarIdx(npc.avatarImageId())
                        .setViewName(npc.viewName())
                        .setSell(sell)
                        .addAllItems(list)
                ).build();
        return new InteractionMenuEvent(source, packet);
    }

    public static InteractionMenuEvent buyingMenu(Player source, InteractableNpc npc,
                                                   String dialog, List<MerchantItem> items) {
        return merchantMenu(source, npc, dialog, items, false);
    }

}
