package org.y1000.message;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.AbstractPlayerEvent;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.TextMessagePacket;

public final class PlayerTextEvent extends AbstractPlayerEvent {

    private final String text;

    private final TextType type;

    public enum TextType implements ValueEnum {
        FARAWAY(1),

        CANT_ATTACK(2),

        INVENTORY_FULL(3),

        TRADE_REJECTED(4),

        NO_WEAPON(5),

        NO_LIFE(6),

        NO_POWER(7),

        NO_INNER_POWER(8),

        NO_OUTER_POWER(8),

        CUSTOM(1000000);
        ;

        private final int v;

        TextType(int v) {
            this.v = v;
        }

        @Override
        public int value() {
            return v;
        }
    }


    public PlayerTextEvent(Player source, String text, TextType type) {
        super(source);
        if (type == TextType.CUSTOM) {
            Validate.isTrue(text != null && text.length() <= 30);
        }
        this.text = text;
        this.type = type;
    }

    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {
        playerEventHandler.visit(this);
    }

    @Override
    protected Packet buildPacket() {
        TextMessagePacket.Builder buider = TextMessagePacket.newBuilder().setType(type.value());
        if (text != null) {
            buider.setText(text);
        }
        return Packet.newBuilder()
                .setText(buider)
                .build();
    }

    public static PlayerTextEvent tooFarAway(Player player) {
        return new PlayerTextEvent(player, null, TextType.FARAWAY);
    }


    public static PlayerTextEvent unableToAttack(Player player) {
        return new PlayerTextEvent(player, null, TextType.CANT_ATTACK);
    }


    public static PlayerTextEvent inventoryFull(Player player) {
        return new PlayerTextEvent(player, null, TextType.INVENTORY_FULL);
    }

    public static PlayerTextEvent tradeDisabled(Player player) {
        return new PlayerTextEvent(player, null, TextType.TRADE_REJECTED);
    }

    public static PlayerTextEvent noWeapon(Player player) {
        return new PlayerTextEvent(player, null, TextType.NO_WEAPON);
    }

    public static PlayerTextEvent noPower(Player player) {
        return new PlayerTextEvent(player, null, TextType.NO_POWER);
    }

    public static PlayerTextEvent noInnerPower(Player player) {
        return new PlayerTextEvent(player, null, TextType.NO_INNER_POWER);
    }

    public static PlayerTextEvent noOuterPower(Player player) {
        return new PlayerTextEvent(player, null, TextType.NO_OUTER_POWER);
    }

    public static PlayerTextEvent noLife(Player player) {
        return new PlayerTextEvent(player, null, TextType.NO_LIFE);
    }
}
