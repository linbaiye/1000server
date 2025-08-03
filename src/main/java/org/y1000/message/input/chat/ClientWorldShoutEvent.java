package org.y1000.message.input.chat;

import org.apache.commons.lang3.StringUtils;
import org.y1000.entities.players.Player;
import org.y1000.message.serverevent.TextMessage;
import org.y1000.realm.event.BroadcastTextEvent;
import org.y1000.realm.event.IRealmEvent;

public record ClientWorldShoutEvent(String content) implements ClientRealmChatEvent {

    @Override
    public IRealmEvent toRealmEvent(Player player) {
        return null;
    }

    private TextMessage.ColorType computeLevel(Player player) {
        int total = player.maxLife() + player.maxPower() + player.maxInnerPower()
                + player.maxOuterPower() + player.age() / 2;
        int i = total / 100;
        if (i < 330) {
            return TextMessage.ColorType.FIRST_GRADE;
        } else if (i < 370) {
            return TextMessage.ColorType.SECOND_GRADE;
        } else if (i < 410) {
            return TextMessage.ColorType.THIRD_GRADE;
        } else if (i < 450) {
            return TextMessage.ColorType.FOURTH_GRADE;
        } else if (i < 490) {
            return TextMessage.ColorType.FIVE_GRADE;
        } else if (i < 530) {
            return TextMessage.ColorType.SIX_GRADE;
        } else if (i < 570) {
            return TextMessage.ColorType.SEVEN_GRADE;
        } else if (i < 610) {
            return TextMessage.ColorType.EIGHT_GRADE;
        } else if (i < 650) {
            return TextMessage.ColorType.NINE_GRADE;
        } else {
            return TextMessage.ColorType.TEN_GRADE;
        }
    }

    @Override
    public boolean canSend(Player player) {
        return player != null && !player.isDead()
                && player.currentLife() >= 5000;
    }

    public static boolean isFormatCorrect(String text) {
        if (StringUtils.isEmpty(text)) {
            return false;
        }
        if (!text.startsWith("!") && !text.startsWith("！")) {
            return false;
        }
        return !StringUtils.isEmpty(text.substring(1).trim());
    }

    public static ClientWorldShoutEvent parse(String text) {
        if (!isFormatCorrect(text))
            return null;
        return new ClientWorldShoutEvent(text.substring(1));
    }
}
