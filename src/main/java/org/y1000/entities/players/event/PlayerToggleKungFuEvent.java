package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.kungfu.KungFu;
import org.y1000.kungfu.KungFuType;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.ToggleKungFuPacket;

public final class PlayerToggleKungFuEvent extends AbstractPlayerEvent {
    private final Integer level;
    private final String name;
    private final boolean quietly;
    private final int type;

    public PlayerToggleKungFuEvent(Player source, KungFu kungFu) {
        this(source, kungFu.name(), kungFu.level(), false, kungFu.kungFuType());
    }

    private PlayerToggleKungFuEvent(Player source, String name, Integer level, boolean quietly, KungFuType type) {
        super(source);
        this.name = name;
        this.level = level;
        this.quietly = quietly;
        this.type = type.value();
    }


    public static PlayerToggleKungFuEvent enable(Player player, KungFu kungFu) {
        return new PlayerToggleKungFuEvent(player, kungFu.name(), kungFu.level(), false, kungFu.kungFuType());
    }

    public static PlayerToggleKungFuEvent disable(Player player, KungFu kungFu) {
        return new PlayerToggleKungFuEvent(player, kungFu.name(), null, false, kungFu.kungFuType());
    }

    public static PlayerToggleKungFuEvent disableNoTip(Player player, KungFu kungFu) {
        return new PlayerToggleKungFuEvent(player, kungFu.name(), null, true, kungFu.kungFuType());
    }

    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {
        playerEventHandler.visit(this);
    }

    @Override
    protected Packet buildPacket() {
        ToggleKungFuPacket.Builder builder = ToggleKungFuPacket.newBuilder()
                .setName(name)
                .setQuietly(quietly)
                .setType(type)
                .setId(source().id());
        if (level != null) {
            builder.setLevel(level);
        }
        return Packet.newBuilder()
                .setToggleKungFu(builder)
                .build();
    }
}
