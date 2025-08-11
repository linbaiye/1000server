package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.realm.PlayerEventHandler;
import org.y1000.realm.event.BroadcastTextEvent;

public class PlayerShoutEvent implements PlayerEvent {

    private final Player source;
    private final String text;
    private final ShoutColor color ;

    private record ShoutColor(String color, String bgColor) {
        public static ShoutColor of(String color, String bg) {
            return new ShoutColor(color, bg);
        }
    }

    /*private readonly IDictionary<ColorType, string> _textColors = new Dictionary<ColorType, string>()
    {
        { ColorType.FIRST_GRADE, "#928573" },
        { ColorType.SECOND_GRADE, "#d0bea8" },
        { ColorType.THIRD_GRADE, "#fcf9f3" },
        { ColorType.FOURTH_GRADE, "#b19241" },
        { ColorType.FIVE_GRADE, "#ba6c23" },
        { ColorType.SIX_GRADE, "#fceeaf" },
        { ColorType.SEVEN_GRADE, "#adff2f" },
        { ColorType.EIGHT_GRADE, "#87cefa" },
        { ColorType.NINE_GRADE, "#d8bfd8" },
        { ColorType.TEN_GRADE, "#ffff00" },
        { ColorType.PRIVATE_CHAT, "#e139b2" },
        { ColorType.SYSTEM_TIP, "#ffff00" },
    };

    private readonly IDictionary<ColorType, Color> _bgColors = new Dictionary<ColorType, Color>()
    {
        { ColorType.FIRST_GRADE, new Color("151e2f") },
        { ColorType.SECOND_GRADE, new Color("151e2f") },
        { ColorType.THIRD_GRADE, new Color("151e2f") },
        { ColorType.FOURTH_GRADE, new Color("0d2758") },
        { ColorType.FIVE_GRADE, new Color("0d2758") },
        { ColorType.SIX_GRADE, new Color("0d2758") },
        { ColorType.SEVEN_GRADE, new Color("296d12") },
        { ColorType.EIGHT_GRADE, new Color("004080") },
        { ColorType.NINE_GRADE, new Color("553872") },
        { ColorType.TEN_GRADE, new Color("999900") },
    };*/

    private static final ShoutColor[] Colors = new ShoutColor[]{
            ShoutColor.of("#928573", "151e2f"),
            ShoutColor.of("#d0bea8", "151e2f"),
            ShoutColor.of("#fcf9f3", "151e2f"),
            ShoutColor.of("#b19241", "0d2758"),
            ShoutColor.of("#ba6c23", "0d2758"),
            ShoutColor.of("#fceeaf", "0d2758"),
            ShoutColor.of("#adff2f", "296d12"),
            ShoutColor.of("#87cefa", "004080"),
            ShoutColor.of("#d8bfd8", "553872"),
            ShoutColor.of("#ffff00", "999900"),
    };

    private final static int[] Level = new int[]{330, 370, 410, 450, 490, 530, 570, 610, 650};

    private PlayerShoutEvent(Player source,
                             String text,
                             ShoutColor color) {
        this.source = source;
        this.text = text;
        this.color = color;
    }

    public static int ComputeShoutLevel(Player player) {
        int total = player.totalAttribute();
        int l = total / 100;
        for (int i = 0; i < Level.length; i++) {
            if (l < Level[i])
                return i + 1;
        }
        return Level.length + 1;
    }

    public static PlayerShoutEvent test(Player player, String text, int level) {
        return new PlayerShoutEvent(player, text, Colors[level]);
    }

    @Override
    public void accept(PlayerEventHandler handler) {
        source().consumeLife(2000);
        handler.sendCrossRealmEvent(BroadcastTextEvent.bottom(text, color.color(), color.bgColor()));
    }

    @Override
    public Player source() {
        return source;
    }
}
