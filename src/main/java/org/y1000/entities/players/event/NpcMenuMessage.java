package org.y1000.entities.players.event;

import org.y1000.entities.creatures.npc.NpcInteractDialogAbility;
import org.y1000.entities.players.Player;
import org.y1000.network.gen.NpcMenuPacket;
import org.y1000.network.gen.Packet;

import java.util.List;

public class NpcMenuMessage extends Abstract2PlayerMessageEvent {

    public NpcMenuMessage(Player player, Packet packet) {
        super(player, packet);
    }

    public static NpcMenuMessage populate(Player player, NpcInteractDialogAbility ability, List<String> actions) {
        NpcMenuPacket menuPacket  = NpcMenuPacket.newBuilder()
                .setId(ability.getId())
                .setImage(ability.getImage())
                .setGreetings(ability.getGreetings())
                .setName(ability.getName())
                .setSprite(ability.getSprite())
                .addAllSupportedActions(actions)
                .build();
        return new NpcMenuMessage(player, Packet.newBuilder().setNpcMenuPacket(menuPacket).build());
    }
}
