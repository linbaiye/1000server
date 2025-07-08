package org.y1000.entities.players.event;

import org.y1000.entities.creatures.npc.event.NpcLifeBarEvent;
import org.y1000.entities.players.Player;
import org.y1000.network.gen.Packet;

public class PlayerLifeBarEvent extends Abstract2VisibleMessageEvent {

    public PlayerLifeBarEvent(Player player, Packet packet) {
        super(player, packet);
    }

    public static PlayerLifeBarEvent of(Player player) {
        return new PlayerLifeBarEvent(player, NpcLifeBarEvent.damagedPacket(player.id(), player.currentLife(), player.maxLife()));
    }
}
