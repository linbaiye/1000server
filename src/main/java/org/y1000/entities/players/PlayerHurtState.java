package org.y1000.entities.players;
import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.ActiveEntity;
import org.y1000.entities.players.equipment.Equipment;
import org.y1000.kungfu.FootKungFu;
import org.y1000.kungfu.attack.AttackKungFu;
import org.y1000.entities.players.event.PlayerChangeStateEvent;

@Slf4j
final class PlayerHurtState extends AbstractPlayerState implements PlayerState {

    private final PlayerState interruptedState;

    static final int StateMillis = 280;

    private PlayerHurtState(PlayerImpl player, PlayerState afterHurt) {
        super(player, PlayerStateEnum.Hurt, StateMillis);
        this.interruptedState = afterHurt;
    }

    @Override
    public void handleAfterHurt() {
        interruptedState.handleAfterHurt();
    }

    @Override
    public void update(int delta) {
        if (player().tryCombatStrike(delta))
            return;
        if (elapse(delta))
            interruptedState.handleAfterHurt();
    }

    @Override
    public void tryToggleAttackKungFu(AttackKungFu attackKungFu) {
        player().tryChangeAttackKungFu(attackKungFu);
    }

    @Override
    public void equip(int slot, Equipment equipment) {
        player().tryEquipFromSlot(slot, equipment);
    }

    @Override
    public void tryToggleFootKungFu(FootKungFu footKungFu) {
        player().stopCombat();
        player().toggleFootAndSync(footKungFu);
        player().footKungFu().ifPresent(k -> {
            player().changeState(PlayerStandState.idle(player()));
            player().sendEvent(PlayerChangeStateEvent.allVisible(player()));
        });
    }

    @Override
    public void attack(ActiveEntity entity) {
        player().tryAcceptAttack(entity);
    }

    public static PlayerHurtState create(PlayerImpl player, PlayerState interruptedState) {
        if (interruptedState instanceof PlayerHurtState hurtState) {
            return new PlayerHurtState(player, hurtState.interruptedState);
        } else {
            return new PlayerHurtState(player, interruptedState);
        }
    }
}
