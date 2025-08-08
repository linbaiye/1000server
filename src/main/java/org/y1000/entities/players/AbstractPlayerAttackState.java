package org.y1000.entities.players;

import org.y1000.entities.ActiveEntity;
import org.y1000.item.Equipment;
import org.y1000.kungfu.FootKungFu;
import org.y1000.kungfu.attack.AttackKungFu;
import org.y1000.kungfu.breath.BreathKungFu;
import org.y1000.entities.players.event.PlayerChangeStateEvent;

public abstract class AbstractPlayerAttackState extends AbstractPlayerState {

    public AbstractPlayerAttackState(PlayerImpl player, PlayerStateEnum stateEnum, int stateMillis) {
        super(player, stateEnum, stateMillis);
    }

    @Override
    public void attack(ActiveEntity target) {
        player().tryAcceptAttack(target);
    }

    @Override
    public void handleAfterHurt() {
        player().changeState(PlayerStandState.fightStand(player()));
        player().sendEvent(PlayerChangeStateEvent.allVisible(player()));
    }

    @Override
    public void equip(int slot, Equipment equipment) {
        var attack = player().attackKungFu();
        player().tryEquipFromSlot(slot, equipment);
        if (!attack.nameEquals(player().attackKungFu())) {
            player().changeState(PlayerStandState.fightStand(player()));
            player().sendEvent(PlayerChangeStateEvent.allVisible(player()));
        }
    }

    @Override
    public void tryToggleAttackKungFu(AttackKungFu attackKungFu) {
        if (player().tryChangeAttackKungFu(attackKungFu)) {
            player().changeState(PlayerStandState.fightStand(player()));
            player().sendEvent(PlayerChangeStateEvent.allVisible(player()));
        }
    }

    @Override
    public void tryToggleFootKungFu(FootKungFu footKungFu) {
        if (elapsedMillis() <= 300)
            return;
        player().stopCombat();
        player().toggleFootAndSync(footKungFu);
        player().changeState(PlayerStandState.idle(player()));
        player().sendEvent(PlayerChangeStateEvent.allVisible(player()));
    }

    @Override
    public void tryToggleBreathKungFu(BreathKungFu breathKungFu) {
        player().stopCombat();
        player().toggleBreathAndSync(breathKungFu);
        player().changeState(PlayerSitDownState.sit(player()));
        player().sendEvent(PlayerChangeStateEvent.allVisible(player()));
    }
}
