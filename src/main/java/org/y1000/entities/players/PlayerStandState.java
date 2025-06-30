package org.y1000.entities.players;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.creatures.OldPlayerStateEnum;
import org.y1000.message.SetPositionEvent;
import org.y1000.message.input.MoveInput;
import org.y1000.message.input.TurnInput;

public final class PlayerStandState extends AbstractPlayerState {
    private PlayerStandState(Player player, OldPlayerStateEnum stateEnum, int millis) {
        super(player, stateEnum, millis);
    }

    @Override
    public void update(int delta) {
        if (elapse(delta)) {
            reset();
        }
    }

    public void move(MoveInput moveInput) {
        if (stateEnum() == OldPlayerStateEnum.IDLE) {
            player().changeState(PlayerMoveState.noneFightMove(player(), moveInput));
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
        return new PlayerStandState(player, OldPlayerStateEnum.IDLE, player.getStateMillis(OldPlayerStateEnum.IDLE));
    }

    public static PlayerStandState fightStand(Player player) {
        Validate.notNull(player);
        return new PlayerStandState(player, OldPlayerStateEnum.FightStand, player.getStateMillis(OldPlayerStateEnum.FightStand));
    }
}
