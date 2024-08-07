package org.y1000.message;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.AbstractPlayerEvent;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.TextMessagePacket;

public final class PlayerTextEvent extends AbstractPlayerEvent {

    public enum Location implements ValueEnum{
        DOWN(1),

        LEFT(2),
        ;
        private final int v;

        Location(int v) {
            this.v = v;
        }

        @Override
        public int value() {
            return v;
        }
    }

    private final String text;

    private final TextType type;
    private final Location location;


    public enum TextType implements ValueEnum {
        FARAWAY(1),

        CANT_ATTACK(2),

        INVENTORY_FULL(3),

        TRADE_REJECTED(4),

        NO_WEAPON(5),

        NO_LIFE(6),

        NO_POWER(7),

        NO_INNER_POWER(8),

        NO_OUTER_POWER(9),

        NOT_ENOUGH_ARM_LIFE(10),

        OUT_OF_AMMO(11),

        NO_MORE_PILL(12),

        MULTI_TRADE(13),

        KUNGFU_LEVEL_LOW(14),


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
        this(source, text, type, Location.DOWN);
    }

    public PlayerTextEvent(Player source, String text, TextType type, Location location) {
        super(source, true);
        if (type == TextType.CUSTOM) {
            Validate.isTrue(text != null && text.length() <= 30);
        }
        this.text = text;
        this.type = type;
        this.location = location;
    }



    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {
        playerEventHandler.visit(this);
    }

    @Override
    protected Packet buildPacket() {
        TextMessagePacket.Builder buider = TextMessagePacket.newBuilder().setType(type.value())
                .setLocation(location.value());
        if (text != null) {
            buider.setText(text);
        }
        return Packet.newBuilder()
                .setText(buider)
                .build();
    }

    public static PlayerTextEvent leftside(Player player, String text) {
        return new PlayerTextEvent(player, text, TextType.CUSTOM, Location.LEFT);
    }

    public static PlayerTextEvent havePill(Player player, String pillName) {
        return new PlayerTextEvent(player, "服用了" + pillName + "。", TextType.CUSTOM, Location.LEFT);
    }

    public static PlayerTextEvent noMorePill(Player player) {
        return new PlayerTextEvent(player, null, TextType.NO_MORE_PILL, Location.LEFT);
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
        return new PlayerTextEvent(player, null, TextType.NO_POWER, Location.LEFT);
    }

    public static PlayerTextEvent noInnerPower(Player player) {
        return new PlayerTextEvent(player, null, TextType.NO_INNER_POWER, Location.LEFT);
    }

    public static PlayerTextEvent noOuterPower(Player player) {
        return new PlayerTextEvent(player, null, TextType.NO_OUTER_POWER, Location.LEFT);
    }

    public static PlayerTextEvent insufficientLife(Player player) {
        return new PlayerTextEvent(player, null, TextType.NO_LIFE, Location.LEFT);
    }

    public static PlayerTextEvent armLifeTooLowToExp(Player player) {
        return new PlayerTextEvent(player, null, TextType.NOT_ENOUGH_ARM_LIFE);
    }

    public static PlayerTextEvent outOfAmmo(Player player) {
        return new PlayerTextEvent(player, null, TextType.OUT_OF_AMMO);
    }

    public static PlayerTextEvent rejectTrade(Player player) {
        return new PlayerTextEvent(player, null, TextType.TRADE_REJECTED);
    }

    public static PlayerTextEvent multiTrade(Player player) {
        return new PlayerTextEvent(player, null, TextType.MULTI_TRADE);
    }

    public static PlayerTextEvent kungFuLevelLow(Player player) {
        return new PlayerTextEvent(player, null, TextType.KUNGFU_LEVEL_LOW);
    }

    public static PlayerTextEvent pickedItem(Player player, String name, int number) {
        return new PlayerTextEvent(player, "获得 " + name + " " + number + "个", TextType.CUSTOM, Location.LEFT);
    }

}
