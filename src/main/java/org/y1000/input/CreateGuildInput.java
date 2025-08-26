package org.y1000.input;

import org.y1000.entities.players.Player;
import org.y1000.entities.players.PlayerInputHandler;
import org.y1000.network.gen.CreateGuildInputPacket;
import org.y1000.realm.event.GuildCreationEvent;
import org.y1000.realm.event.RealmEvent;

public record CreateGuildInput(boolean confirmed, int slotId, String name) implements SelfHandleInput {

    public static CreateGuildInput fromPacket(CreateGuildInputPacket packet){
        return new CreateGuildInput(packet.getConfirm(),  packet.getFromSlot(), packet.getName());
    }

    private RealmEvent toRealmEvent(Player player) {
        return new GuildCreationEvent(player, confirmed, slotId, name);
    }

    @Override
    public void accept(PlayerInputHandler handler) {
        handler.proxyToRealm(this::toRealmEvent);
    }
}
