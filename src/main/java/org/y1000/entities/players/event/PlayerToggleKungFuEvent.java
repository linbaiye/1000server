package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.kungfu.KungFu;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.ToggleKungFuPacket;

public class PlayerToggleKungFuEvent extends AbstractPlayerEvent {
    private final Integer level;
    private final String name;
    public PlayerToggleKungFuEvent(Player source, KungFu kungFu) {
        super(source);
        name = kungFu.name();
        level = kungFu.level();
    }

    public PlayerToggleKungFuEvent(Player source, String name) {
        super(source);
        this.name = name;
        this.level = null;
    }


    public static PlayerToggleKungFuEvent enable(Player player, KungFu kungFu) {
        return new PlayerToggleKungFuEvent(player, kungFu);
    }

    public static PlayerToggleKungFuEvent disable(Player player, KungFu kungFu) {
        return new PlayerToggleKungFuEvent(player, kungFu.name());
    }

    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {
        playerEventHandler.visit(this);
    }

    @Override
    protected Packet buildPacket() {
        ToggleKungFuPacket.Builder builder = ToggleKungFuPacket.newBuilder().setName(name)
                .setId(source().id());
        if (level != null) {
            builder.setLevel(level);
        }
        return Packet.newBuilder()
                .setToggleKungFu(builder)
                .build();
    }
}
