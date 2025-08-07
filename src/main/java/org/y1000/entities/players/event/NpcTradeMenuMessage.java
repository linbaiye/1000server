package org.y1000.entities.players.event;

import org.y1000.entities.creatures.npc.AbstractNpcTradeAbility;
import org.y1000.entities.players.Player;
import org.y1000.network.gen.NpcItemPacket;
import org.y1000.network.gen.NpcTradeMenuPacket;
import org.y1000.network.gen.Packet;


public class NpcTradeMenuMessage extends Abstract2PlayerMessageEvent {

    public NpcTradeMenuMessage(Player player, Packet packet) {
        super(player, packet);
    }


    private static NpcTradeMenuMessage of(Player player, AbstractNpcTradeAbility ability, boolean sale) {
        var builder =  NpcTradeMenuPacket.newBuilder()
                .setGreetings(ability.getGreetings())
                .setId(ability.getId())
                .setImage(ability.getImage())
                .setSprite(ability.getSprite())
                .setSale(sale)
                .setName(ability.getViewName());
        ability.getItems().forEach(i -> builder.addItems(NpcItemPacket.newBuilder()
                .setIcon(i.getIcon()).setName(i.name()).setPrice(i.price())
                .setColor(i.getColor()).setCanStack(i.canStack())
                .build()));
        return new NpcTradeMenuMessage(player, Packet.newBuilder().setTradeMenuPacket(builder.build()).build());
    }

    public static NpcTradeMenuMessage buy(Player player, AbstractNpcTradeAbility ability) {
        return of(player, ability, false);
    }

    public static NpcTradeMenuMessage sale(Player player, AbstractNpcTradeAbility ability) {
        return of(player, ability, true);
    }
}

