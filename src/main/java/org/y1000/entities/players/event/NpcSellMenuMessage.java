package org.y1000.entities.players.event;

import org.y1000.entities.creatures.npc.NpcSellAbility;
import org.y1000.entities.players.Player;
import org.y1000.network.gen.NpcSellMenuPacket;
import org.y1000.network.gen.Packet;

public class NpcSellMenuMessage extends Abstract2PlayerMessageEvent {

    public NpcSellMenuMessage(Player player, Packet packet) {
        super(player, packet);
    }


    public static NpcSellMenuMessage of(long id, NpcSellAbility ability) {
        NpcSellMenuPacket build = NpcSellMenuPacket.newBuilder()
                .setGreetings(ability.getGreetings())
                .setId(id)
                .setImage(ability.getImage())
                .setSprite(ability.getSprite())
                .setName(ability.getName())
                .build();
    }
}

