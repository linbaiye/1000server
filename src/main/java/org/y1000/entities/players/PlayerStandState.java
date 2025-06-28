package org.y1000.entities.players;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.creatures.PlayerStateEnum;
import org.y1000.message.SetPositionEvent;
import org.y1000.message.input.MoveInput;
import org.y1000.message.input.TurnInput;

public final class PlayerStandState extends AbstractPlayerState {
    private PlayerStandState(Player player, PlayerStateEnum stateEnum, int millis) {
        super(player, stateEnum, millis);
    }

    @Override
    public void update(int delta) {
        if (elapse(delta)) {
            reset();
        }
    }

    public void move(MoveInput moveInput) {
        if (stateEnum() == PlayerStateEnum.IDLE) {
            player().changeState(PlayerMoveState.noneFightWalk(player(), moveInput));
        } else {
            player().changeState(PlayerMoveState.fightWalk(player(), moveInput));
        }
    }

    public void turn(TurnInput turnInput) {
        player().changeDirection(turnInput.direction());
        player().emitEvent(SetPositionEvent.of(player()));
    }

    public static PlayerStandState idle(Player player) {
        Validate.notNull(player);
        return new PlayerStandState(player, PlayerStateEnum.IDLE, player.getStateMillis(PlayerStateEnum.IDLE));
    }

    public static PlayerStandState fightStand(Player player) {
        Validate.notNull(player);
        return new PlayerStandState(player, PlayerStateEnum.FightStand, player.getStateMillis(PlayerStateEnum.FightStand));
    }
}
