//package org.y1000.entities.players;
//
//import org.slf4j.Logger;
//import org.y1000.entities.Direction;
//import org.y1000.entities.players.event.RewindEvent;
//import org.y1000.message.InputResponseMessage;
//import org.y1000.entities.players.event.PlayerMoveEvent;
//import org.y1000.message.SetPositionEvent;
//import org.y1000.message.input.ClientMovementEvent;
//import org.y1000.message.input.input.AbstractRightClick;
//import org.y1000.message.input.input.RightMouseRelease;
//import org.y1000.util.Coordinate;
//
//public interface MovableState {
//
//    Logger logger();
//
//    IPlayerState rewindState(PlayerImpl player);
//
//    IPlayerState moveState(PlayerImpl player, Direction direction);
//
//
//    private void handleRightClick(PlayerImpl player, AbstractRightClick rightClick) {
////        Coordinate targetCoordinate = player.coordinate().moveBy(rightClick.direction());
////        if (!player.realmMap().movable(targetCoordinate)) {
////            logger().debug("Destination {} conflicted, rewind player {} back to {}", targetCoordinate, player.id(), player.coordinate());
////            player.changeDirection(rightClick.direction());
////            rewind(player, rightClick.sequence());
////        } else {
////            IPlayerState playerState = player.footKungFu().map(footKungFu ->
////                    (IPlayerState) IPlayerMoveState.moveBy(player, rightClick.direction()))
////                    .orElse(moveState(player, rightClick.direction()));
////            player.changeState(playerState);
////            player.emitEvent(new InputResponseMessage(rightClick.sequence(), PlayerMoveEvent.movingBy(player, rightClick.direction())));
////        }
//    }
//
//    private void rewind(PlayerImpl player, long seq) {
////        IPlayerState newState = player.footKungFu().map(footKungFu ->
////                (IPlayerState) PlayerStillState.idle(player)).orElse(rewindState(player));
////        player.changeState(newState);
////        player.emitEvent(new InputResponseMessage(seq, RewindEvent.of(player)));
//    }
//
//
//    default void move(PlayerImpl player, ClientMovementEvent event) {
////        if (!event.happenedAt().equals(player.coordinate())) {
////            logger().debug("Rewind because of coordinate mismatch, client: {}, server: {}.", event.happenedAt(), player.coordinate());
////            rewind(player, event.moveInput().sequence());
////            return;
////        }
////        if (event.moveInput() instanceof AbstractRightClick rightClick) {
////            handleRightClick(player, rightClick);
////        } else if (event.moveInput() instanceof RightMouseRelease release) {
////            player.emitEvent(new InputResponseMessage(release.sequence(), SetPositionEvent.of(player)));
////        }
//    }
//}
