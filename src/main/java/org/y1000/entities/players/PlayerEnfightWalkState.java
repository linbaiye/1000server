package org.y1000.entities.players;

import org.y1000.entities.Direction;
import org.y1000.entities.Entity;
import org.y1000.entities.creatures.AbstractCreatureMoveState;
import org.y1000.entities.creatures.State;
import org.y1000.entities.players.event.RewindEvent;
import org.y1000.message.clientevent.ClientEventVisitor;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.util.Coordinate;

public class PlayerEnfightWalkState extends AbstractCreatureMoveState<PlayerImpl> implements ClientEventVisitor {

    private final Entity target;

    private PlayerEnfightWalkState(State state, Coordinate start, Direction towards, int millisPerUnit,
                                   Entity target) {
        super(state, start, towards, millisPerUnit);
        this.target = target;
    }
    private void handleInput(PlayerImpl player) {
        player.takeClientEvent().ifPresent(e -> e.accept(player, this));
    }

    @Override
    public void update(PlayerImpl player, int delta) {
        if (elapsedMillis() >= getTotalMillis()) {
            handleInput(player);
            return;
        }
        walkMillis(player, delta);
        if (elapsedMillis() < getTotalMillis()) {
            return;
        }
        if (tryChangeCoordinate(player, player.realmMap())) {
            handleInput(player);
            return;
        }
        player.changeCoordinate(getStart());
        player.clearEventQueue();
        player.emitEvent(RewindEvent.of(player));
    }
}
