package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.creatures.AbstractCreatureMoveState;
import org.y1000.entities.creatures.Creature;
import org.y1000.entities.creatures.State;
import org.y1000.entities.players.kungfu.FootKungFu;
import org.y1000.message.*;
import org.y1000.message.clientevent.CharacterMovementEvent;
import org.y1000.message.clientevent.ClientEventVisitor;
import org.y1000.message.input.*;

import java.util.Optional;


@Slf4j
public final class PlayerMoveState extends AbstractCreatureMoveState<PlayerImpl> implements
        PlayerState, ClientEventVisitor {

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
            handleInput(player);
        } else {
            player.changeState(new PlayerIdleState(player.getStateMillis(State.IDLE)));
            player.emitEvent(new InputResponseMessage(currentInput.sequence(), SetPositionEvent.ofCreature(player)));
        }
    }

    @Override
    public void afterAttacked(PlayerImpl player, Creature attacker) {
        player.changeState(this);
    }

    public static PlayerMoveState move(PlayerImpl player, AbstractRightClick trigger) {
        Optional<FootKungFu> footMagic = player.footKungFu();
        State state = footMagic.map(magic -> magic.canFly() ? State.FLY : State.RUN)
                .orElse(State.WALK);
        return new PlayerMoveState(trigger, player.getStateMillis(state), state);
    }


    @Override
    public void visit(PlayerImpl player, CharacterMovementEvent movementEvent) {
        movementEvent.resetOrMove(player);
    }
}
