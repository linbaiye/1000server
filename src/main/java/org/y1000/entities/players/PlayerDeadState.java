package org.y1000.entities.players;

//public final class PlayerDeadState extends IAbstractCreatureState<PlayerImpl> implements IPlayerState {
//
//    public PlayerDeadState(int totalMillis) {
//        super(totalMillis);
//    }
//
//    @Override
//    public OldPlayerStateEnum stateEnum() {
//        return OldPlayerStateEnum.DIE;
//    }
//
//    @Override
//    public void update(PlayerImpl player, int delta) {
//        if (elapse(delta)) {
//            player.changeState(PlayerStillState.idle(player));
//            player.emitEvent(new PlayerReviveEvent(player));
//        }
//    }
//
//    @Override
//    public boolean attackable() {
//        return false;
//    }
//
//    @Override
//    public NpcStateEnum state() {
//        return NpcStateEnum.Die;
//    }
//
//    public static PlayerDeadState die(PlayerImpl player) {
//        return new PlayerDeadState(player.getStateMillis(OldPlayerStateEnum.DIE) + 30000);
//    }
//
//}
