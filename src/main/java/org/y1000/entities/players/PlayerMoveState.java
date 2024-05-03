package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.creatures.AbstractCreatureMoveState;
import org.y1000.entities.players.kungfu.FootKungFu;
import org.y1000.message.*;
import org.y1000.message.clientevent.CharacterMovementEvent;
import org.y1000.message.clientevent.ClientEventVisitor;
import org.y1000.message.input.*;

import java.util.Optional;


@Slf4j
public final class PlayerMoveState extends AbstractCreatureMoveState<PlayerImpl> implements
        PlayerState, ClientEventVisitor {

    private static final int MILLIS_TO_WALK_ONE_UNIT = 900;
    private static final int MILLIS_TO_RUN_ONE_UNIT = 450;

    private final AbstractRightClick currentInput;

    public PlayerMoveState(AbstractRightClick currentInput, int millisPerUnit, State state) {
        super(state, millisPerUnit, currentInput.direction());
        this.currentInput = currentInput;
    }

    private void handleInput(PlayerImpl player) {
        player.takeClientEvent().ifPresent(e -> e.accept(player, this));
    }

    @Override
    public void update(PlayerImpl player, int deltaMillis) {
        if (elapsedMillis() >= millisPerUnit()) {
            handleInput(player);
            return;
        }
        walkMillis(player, deltaMillis);
        if (elapsedMillis() < millisPerUnit()) {
            return;
        }
        if (tryChangeCoordinate(player, player.realmMap())) {
            log.debug("Moved to coordinate {}", player.coordinate());
            handleInput(player);
        } else {
            player.changeState(new PlayerIdleState());
            player.emitEvent(new InputResponseMessage(currentInput.sequence(), SetPositionEvent.fromPlayer(player)));
        }
    }


    public static PlayerMoveState move(Player player, AbstractRightClick trigger) {
        Optional<FootKungFu> footMagic = player.footKungFu();
        return footMagic.map(magic -> new PlayerMoveState(trigger, MILLIS_TO_RUN_ONE_UNIT, magic.canFly()? State.FLY : State.RUN))
                .orElse(new PlayerMoveState(trigger, MILLIS_TO_WALK_ONE_UNIT, State.WALK));
    }

    @Override
    public void visit(PlayerImpl player, CharacterMovementEvent movementEvent) {
        movementEvent.resetOrMove(player);
    }
}
