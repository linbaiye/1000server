package org.y1000.message.serverevent;

import org.apache.commons.lang3.Validate;
import org.y1000.message.AbstractServerMessage;
import org.y1000.message.ValueEnum;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.TextMessagePacket;

public class TextMessage extends AbstractServerMessage {
    private final String text;
    private final TextType type;
    private final Location location;
    private final ColorType colorType;

    private final String fromPlayer;


    public TextMessage(String text, TextType type, Location location, ColorType colorType) {
        this(text, type, location, colorType, null);
    }

    public TextMessage(String text, TextType type, Location location,
                       ColorType colorType,
                       String sourcePlayer) {
        if (type == TextMessage.TextType.CUSTOM) {
            Validate.isTrue(text != null && text.length() <= 60);
        }
        Validate.notNull(type);
        Validate.notNull(location);
        Validate.notNull(colorType);
        this.text = text;
        this.type = type;
        this.location = location;
        this.colorType = colorType;
        this.fromPlayer = sourcePlayer;
    }

    @Override
    protected Packet buildPacket() {
        TextMessagePacket.Builder buider = TextMessagePacket.newBuilder().setType(type.value())
                .setColorType(colorType.value())
                .setLocation(location.value());
        if (text != null) {
            buider.setText(text);
        }
        if (fromPlayer != null) {
            buider.setFromPlayer(fromPlayer);
        }
        return Packet.newBuilder()
                .setText(buider)
                .build();
    }

    public enum Location implements ValueEnum {
        DOWN(1),

        LEFT(2),

        LEFT_UP(3),

        CENTER(4),
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

    public enum ColorType implements ValueEnum {
        FIRST_GRADE(1),
        SECOND_GRADE(2),
        THIRD_GRADE(3),
        FOURTH_GRADE(4),
        FIVE_GRADE(5),
        SIX_GRADE(6),
        SEVEN_GRADE(7),
        EIGHT_GRADE(8),
        NINE_GRADE(9),
        TEN_GRADE(10),
        SAY(11),
        PRIVATE_CHAT(12),
        SYSTEM_TIP(13),
        ;

        private final int val;

        ColorType(int val) {
            this.val = val;
        }

        @Override
        public int value() {
            return val;
        }
    }

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

        NINE_TAIL_FOX_SHIFT(15),

        PLAYER_SHOUT(16),

        PLAYER_WHISPER(18),

        NOT_ENOUGH_HEAD_LIFE(19),

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
    public static TextMessage leftside(String text) {
        return new TextMessage(text, TextMessage.TextType.CUSTOM, TextMessage.Location.LEFT, ColorType.SAY);
    }
}
