package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.kungfu.KungFu;
import org.y1000.realm.PlayerEventHandler;
import org.y1000.realm.event.BroadcastTextEvent;

public final class PlayerKungFuFullEvent implements PlayerEvent {
    private final String text;
    private final Player player;

    public PlayerKungFuFullEvent(Player source, KungFu kungFu) {
        text = source.viewName() +  "，恭喜你，" + kungFu.name() + "修炼值已到顶点！";
        this.player = source;
    }

    public BroadcastTextEvent toBroadcastEvent() {
        return BroadcastTextEvent.leftUp(text);
    }

    @Override
    public Player source() {
        return player;
    }

    @Override
    public void accept(PlayerEventHandler handler) {
        handler.sendBroadcast(toBroadcastEvent());
    }
}
