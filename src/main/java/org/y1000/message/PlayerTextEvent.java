package org.y1000.message;

import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.AbstractPlayerEvent;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.message.serverevent.TextMessage;
import org.y1000.network.gen.Packet;

public final class PlayerTextEvent extends AbstractPlayerEvent {

    private final TextMessage textMessage;


    private PlayerTextEvent(Player source, String text, TextMessage.TextType type) {
        this(source, text, type, TextMessage.Location.DOWN);
    }

    private PlayerTextEvent(Player source, String text, TextMessage.TextType type, TextMessage.Location location) {
        this(source, text, type, location, TextMessage.ColorType.SAY);
    }

    public PlayerTextEvent(Player source, String text, TextMessage.TextType type, TextMessage.Location location, TextMessage.ColorType colorType) {
        super(source, true);
        textMessage = new TextMessage(text, type, location, colorType);
    }


    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {
        playerEventHandler.visit(this);
    }

    @Override
    protected Packet buildPacket() {
        return textMessage.toPacket();
    }


    public static PlayerTextEvent havePill(Player player, String pillName) {
        return new PlayerTextEvent(player, "服用了" + pillName + "。", TextMessage.TextType.CUSTOM, TextMessage.Location.LEFT);
    }

    public static PlayerTextEvent noMorePill(Player player) {
        return new PlayerTextEvent(player, null, TextMessage.TextType.NO_MORE_PILL, TextMessage.Location.LEFT);
    }

    public static PlayerTextEvent tooFarAway(Player player) {
        return new PlayerTextEvent(player, null, TextMessage.TextType.FARAWAY);
    }


    public static PlayerTextEvent inventoryFull(Player player) {
        return new PlayerTextEvent(player, null, TextMessage.TextType.INVENTORY_FULL);
    }

    public static PlayerTextEvent noWeapon(Player player) {
        return new PlayerTextEvent(player, null, TextMessage.TextType.NO_WEAPON);
    }

    public static PlayerTextEvent noPower(Player player) {
        return new PlayerTextEvent(player, null, TextMessage.TextType.NO_POWER, TextMessage.Location.LEFT);
    }

    public static PlayerTextEvent noInnerPower(Player player) {
        return new PlayerTextEvent(player, null, TextMessage.TextType.NO_INNER_POWER, TextMessage.Location.LEFT);
    }

    public static PlayerTextEvent noOuterPower(Player player) {
        return new PlayerTextEvent(player, null, TextMessage.TextType.NO_OUTER_POWER, TextMessage.Location.LEFT);
    }

    public static PlayerTextEvent insufficientLife(Player player) {
        return new PlayerTextEvent(player, null, TextMessage.TextType.NO_LIFE, TextMessage.Location.LEFT);
    }

    public static PlayerTextEvent armLifeTooLowToExp(Player player) {
        return new PlayerTextEvent(player, null, TextMessage.TextType.NOT_ENOUGH_ARM_LIFE);
    }

    public static PlayerTextEvent headLifeTooLow(Player player) {
        return new PlayerTextEvent(player, null, TextMessage.TextType.NOT_ENOUGH_HEAD_LIFE);
    }

    public static PlayerTextEvent outOfAmmo(Player player) {
        return new PlayerTextEvent(player, null, TextMessage.TextType.OUT_OF_AMMO);
    }

    public static PlayerTextEvent rejectTrade(Player player) {
        return new PlayerTextEvent(player, null, TextMessage.TextType.TRADE_REJECTED);
    }

    public static PlayerTextEvent multiTrade(Player player) {
        return new PlayerTextEvent(player, null, TextMessage.TextType.MULTI_TRADE);
    }

    public static PlayerTextEvent kungFuLevelLow(Player player) {
        return new PlayerTextEvent(player, null, TextMessage.TextType.KUNGFU_LEVEL_LOW);
    }

    public static PlayerTextEvent pickedItem(Player player, String name, int number) {
        return new PlayerTextEvent(player, name + " 获得 " + number + "个。", TextMessage.TextType.CUSTOM, TextMessage.Location.LEFT);
    }

    public static PlayerTextEvent whisper(Player player, String content) {
        return new PlayerTextEvent(player, content, TextMessage.TextType.PLAYER_WHISPER, TextMessage.Location.DOWN, TextMessage.ColorType.PRIVATE_CHAT);
    }

    public static PlayerTextEvent leftup(Player player, String text) {
        return new PlayerTextEvent(player,text, TextMessage.TextType.CUSTOM, TextMessage.Location.LEFT_UP, TextMessage.ColorType.SAY);
    }

    public static PlayerTextEvent systemNotification(Player player, String content) {
        return new PlayerTextEvent(player, content, TextMessage.TextType.CUSTOM, TextMessage.Location.CENTER, TextMessage.ColorType.SAY);
    }

    public static PlayerTextEvent systemTip(Player player, String content) {
        return new PlayerTextEvent(player, content, TextMessage.TextType.CUSTOM, TextMessage.Location.DOWN, TextMessage.ColorType.SYSTEM_TIP);
    }

    public static PlayerTextEvent forbidGuildCreation(Player player) {
        return systemTip(player, "此地禁止创立门派。");
    }

    public static PlayerTextEvent playerClicked(Player source, Player clicked) {
        StringBuilder stringBuilder = new StringBuilder("名称: ")
                .append(clicked.viewName()).append("\r\n");
        source.guildMembership().ifPresent(m -> m.append(stringBuilder));
        stringBuilder.append("使用武功: ").append(clicked.attackKungFu().name());
        clicked.protectKungFu().ifPresent(p -> stringBuilder.append(" ").append(p.name()));
        clicked.footKungFu().ifPresent(f -> stringBuilder.append(" ").append(f.name()));
        clicked.assistantKungFu().ifPresent(a -> stringBuilder.append(" ").append(a.name()));
        clicked.breathKungFu().ifPresent(b -> stringBuilder.append(" ").append(b.name()));
        return new PlayerTextEvent(source, stringBuilder.toString(), TextMessage.TextType.CUSTOM, TextMessage.Location.DOWN, TextMessage.ColorType.SYSTEM_TIP);
    }

}
