//package org.y1000.entities.players.fight;
//
//import lombok.extern.slf4j.Slf4j;
//import org.slf4j.Logger;
//import org.y1000.entities.creatures.OldPlayerStateEnum;
//import org.y1000.entities.players.PlayerImpl;
//
//@Slf4j
//public final class PlayerCooldownState extends AbstractFightingState {
//
//    public PlayerCooldownState(int totalMillis) {
//        super(totalMillis);
//    }
//
//    @Override
//    public OldPlayerStateEnum stateEnum() {
//        return OldPlayerStateEnum.FightStand;
//    }
//
//    @Override
//    public void update(PlayerImpl player, int delta) {
//        if (elapse(delta)) {
//            player.attackKungFu().attackAgain(player);
//        }
//    }
//
//    @Override
//    public Logger logger() {
//        return log;
//    }
//}
