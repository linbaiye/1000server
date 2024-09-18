package org.y1000.entities.players.event;

import org.y1000.entities.Entity;
import org.y1000.entities.players.Player;
import org.y1000.kungfu.KungFu;
import org.y1000.message.PlayerTextEvent;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.realm.event.BroadcastEvent;

public final class PlayerKungFuFullEvent implements BroadcastEvent, PlayerEvent {
    private final String text;
    private final Player player;

    public PlayerKungFuFullEvent(Player source, KungFu kungFu) {
        text = source.viewName() +  " 恭喜你\n" + kungFu.name() + " 修炼值已到顶点";
        this.player = source;
    }

    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {

    }

    @Override
    public void send(Player player) {
        player.emitEvent(PlayerTextEvent.leftup(player, text));
    }

    @Override
    public Entity source() {
        return player;
    }
}
