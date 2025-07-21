package org.y1000.entities.players.event;

import org.y1000.entities.creatures.npc.MerchantItem;
import org.y1000.entities.creatures.npc.NpcSellAbility;
import org.y1000.entities.players.Player;
import org.y1000.network.gen.NpcItemPacket;
import org.y1000.network.gen.NpcSellMenuPacket;
import org.y1000.network.gen.Packet;

import java.util.List;

public class NpcSellMenuMessage extends Abstract2PlayerMessageEvent {

    public NpcSellMenuMessage(Player player, Packet packet) {
        super(player, packet);
    }


    public static NpcSellMenuMessage of(Player player, NpcSellAbility ability) {
        NpcSellMenuPacket.Builder builder = NpcSellMenuPacket.newBuilder()
                .setGreetings(ability.getGreetings())
                .setId(ability.getId())
                .setImage(ability.getImage())
                .setSprite(ability.getSprite())
                .setName(ability.getName());
        ability.getItems().forEach(i -> builder.addItems(NpcItemPacket.newBuilder()
                        .setIcon(i.getIcon()).setName(i.name()).setPrice(i.price())
                        .setColor(i.getColor()).setCanStack(i.canStack())
                .build()));
        return new NpcSellMenuMessage(player, Packet.newBuilder().setNpcSellMenu(builder.build()).build());
    }
}

