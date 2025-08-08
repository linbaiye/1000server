package org.y1000.entities.players;


import org.y1000.item.Equipment;
import org.y1000.kungfu.attack.AttackKungFu;
import org.y1000.entities.players.event.PlayerChangeStateEvent;

final class PlayerStandUpState extends AbstractPlayerState {


    private final boolean toFight;

    private PlayerStandUpState(PlayerImpl player, boolean toFight) {
        super(player, PlayerStateEnum.StandUp, 750);
        this.toFight = toFight;
    }

    public PlayerStandUpState(PlayerImpl player) {
        this(player, false);
    }
    public static PlayerStandUpState toCombat(PlayerImpl player) {
        return new PlayerStandUpState(player, true);
    }

    @Override
    public void update(int delta) {
        if (elapse(delta)) {
            changeToNext();
        }
    }

    @Override
    public void equip(int slot, Equipment equipment) {
        player().tryEquipFromSlot(slot, equipment);
    }

    private void changeToNext() {
        if (toFight)
            player().changeState(PlayerStandState.fightStand(player()));
        else
            player().changeState(PlayerStandState.idle(player()));
        player().sendEvent(PlayerChangeStateEvent.allVisible(player()));
    }

    @Override
    public void handleAfterHurt() {
        changeToNext();
    }

    @Override
    public void tryToggleAttackKungFu(AttackKungFu attackKungFu) {
        player().tryChangeAttackKungFu(attackKungFu);
    }

    @Override
    protected PlayerImpl player() {
        return super.player();
    }
}
