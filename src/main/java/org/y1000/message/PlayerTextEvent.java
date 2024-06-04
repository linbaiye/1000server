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
        Validate.isTrue(text != null && text.length() <= 30);
        this.text = text;
        this.type = type;
    }

    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {
        playerEventHandler.visit(this);
    }

    @Override
    protected Packet buildPacket() {
        return Packet.newBuilder()
                .setText(TextMessagePacket.newBuilder().setText(text))
                .build();
    }

    public static PlayerTextEvent tooFarAway(Player player) {
        return new PlayerTextEvent(player, "距离过远", TextType.FARAWAY);
    }


    public static PlayerTextEvent unableToAttack(Player player) {
        return new PlayerTextEvent(player, "无法攻击", TextType.CANT_ATTACK);
    }


    public static PlayerTextEvent inventoryFull(Player player) {
        return new PlayerTextEvent(player, "物品栏已满", TextType.INVENTORY_FULL);
    }

    public static PlayerTextEvent tradeDisabled(Player player) {
        return new PlayerTextEvent(player, "对方拒绝交易", TextType.TRADE_REJECTED);
    }

    public static PlayerTextEvent noWeapon(Player player) {
        return new PlayerTextEvent(player, "没有对应的武器", TextType.NO_WEAPON);
    }
}
