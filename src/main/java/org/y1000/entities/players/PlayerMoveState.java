package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.players.event.PlayerMovedEvent;
import org.y1000.entities.players.event.PlayerSetPositionAndStateEvent;
import org.y1000.item.Equipment;
import org.y1000.kungfu.FootKungFu;
import org.y1000.kungfu.attack.AttackKungFu;
import org.y1000.entities.players.event.PlayerMoveEvent;
import org.y1000.entities.players.event.PlayerChangeStateEvent;
import org.y1000.message.input.MoveInput;
import org.y1000.util.Coordinate;


@Slf4j
final class PlayerMoveState extends AbstractPlayerState {

    private final MoveAction moveAction;

    private final MoveInput currentInput;

    private MoveInput newInput;


    private PlayerMoveState(PlayerImpl player,
            MoveInput input,
            MoveAction moveAction) {
        super(player, PlayerStateEnum.Move, moveAction.getMillis());
        this.moveAction = moveAction;
        currentInput = input;
    }

    private void changeToStand() {
        MoveAction currentAction = computeMoveAction(player(), moveAction);
        if (currentAction == MoveAction.FightWalk) {
            player().changeState(PlayerStandState.fightStand(player()));
        } else {
            player().changeState(PlayerStandState.idle(player()));
        }
    }

    public MoveAction moveAction() {
        return moveAction;
    }

    private boolean resetIfNotMovable(Coordinate destination) {
        if (!player().movable(destination)) {
            changeToStand();
            player().sendEvent(PlayerSetPositionAndStateEvent.of(player()));
            return true;
        }
        return false;
    }

    @Override
    public void update(int delta) {
        if (elapsedMillis() == 0) {
            // Not able to keep after this move, disable it in advance.
            if (player().footKungFu().map(k -> !k.canKeep(player())).orElse(false))
                player().disableFootKungFuAndSync();
        }
        if (!elapse(delta))
            return;
        if (resetIfNotMovable(currentInput.destination())) {
            return;
        }
        player().footKungFu().ifPresent(footKungFu -> {
            boolean canFlyBefore = footKungFu.canFly();
            footKungFu.tryGainExpAndUseResources(player());
            if (!canFlyBefore && footKungFu.canFly()) {
                player().syncActiveKungFuList();
            }
        });
        player().changeCoordinate(currentInput.destination());
        player().sendEvent(new PlayerMovedEvent(player()));
        if (newInput == null) {
            changeToStand();
            player().sendEvent(PlayerChangeStateEvent.noSelf(player()));
            return;
        }
        if (!player().coordinate().equals(newInput.from())) {
            changeToStand();
            player().sendEvent(PlayerSetPositionAndStateEvent.of(player()));
            return;
        }
        player().changeDirection(newInput.direction());
        if (resetIfNotMovable(newInput.destination())) {
            return;
        }
        MoveAction newAction = computeMoveAction(player(), moveAction);
        player().changeState(new PlayerMoveState(player(), newInput, newAction));
        player().sendEvent(PlayerMoveEvent.moveBy(player(), newAction));
    }

    @Override
    public void handleAfterHurt() {
        player().changeState(this);
        player().sendEvent(PlayerMoveEvent.restore(player(), moveAction, elapsedMillis()));
    }

    @Override
    public void tryMove(MoveInput moveInput) {
        newInput = moveInput;
    }

    @Override
    public void equip(int slot, Equipment equipment) {
        player().tryEquipFromSlot(slot, equipment);
    }

    private static MoveAction computeMoveAction(PlayerImpl player, MoveAction current) {
        return player.footKungFu().map(k -> k.canFly() ? MoveAction.Fly : MoveAction.Run)
                .orElse(current);
    }

    private static MoveAction computeNonFightMoveAction(PlayerImpl player) {
        return player.footKungFu().map(k -> k.canFly() ? MoveAction.Fly : MoveAction.Run)
                .orElse(MoveAction.Walk);
    }

    static PlayerMoveState noneFightMove(PlayerImpl player, MoveInput input) {
        return new PlayerMoveState(player, input, computeNonFightMoveAction(player));
    }

    static PlayerMoveState fightWalk(PlayerImpl player, MoveInput moveInput) {
        return new PlayerMoveState(player, moveInput, MoveAction.FightWalk);
    }

    @Override
    public void tryToggleFootKungFu(FootKungFu footKungFu) {
        player().stopCombat();
        player().toggleFootAndSync(footKungFu);
    }

    @Override
    public void tryToggleAttackKungFu(AttackKungFu attackKungFu) {
        player().tryChangeAttackKungFu(attackKungFu);
    }
}
