package org.y1000.realm;

import org.y1000.entities.creatures.State;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.OpenTradeWindowEvent;
import org.y1000.entities.players.event.PlayerStartTradeEvent;
import org.y1000.entities.trade.Trade;
import org.y1000.message.PlayerTextEvent;
import org.y1000.message.serverevent.EntityEvent;
import org.y1000.message.serverevent.EntityEventListener;
import org.y1000.util.UnaryAction;

import java.util.HashMap;
import java.util.Map;

public final class TradeManager implements EntityEventListener {

    private final Map<Player, Trade> ongoingTrades;


    public TradeManager() {
        ongoingTrades = new HashMap<>();
    }

    public boolean hasOngoingTrade(Player player) {
        return ongoingTrades.containsKey(player);
    }

    public void handle(PlayerStartTradeEvent event, Player target,
                       UnaryAction<EntityEvent> eventSender) {
        if (event.player().stateEnum() == State.DIE ||
                target.stateEnum() == State.DIE ||
                hasOngoingTrade(event.player()) ||
                hasOngoingTrade(target)) {
            return;
        }
        if (!target.tradeEnabled()) {
            eventSender.invoke(PlayerTextEvent.tradeDisabled(event.player()));
            return;
        }
        if (target.coordinate().directDistance(event.source().coordinate()) > 2) {
            eventSender.invoke(PlayerTextEvent.tooFarAway(event.player()));
            return;
        }
        Trade trade = new Trade(event.player(), target);
        ongoingTrades.put(event.player(), trade);
        ongoingTrades.put(target, trade);
        eventSender.invoke(event);
        eventSender.invoke(new OpenTradeWindowEvent(target, event.slot()));
    }

    @Override
    public void onEvent(EntityEvent entityEvent) {

    }
}
