package org.y1000.realm.event;

import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.PlayerTextMessage;
import org.y1000.input.ApplyGuildKungFuInput;
import org.y1000.network.Connection;
import org.y1000.util.Coordinate;

public interface RealmEventHandler {

    void teleportIn(Player player, Coordinate toCoordinate, Connection connection);

    void broadcastText(BroadcastTextEvent event);

    void deliverPrivateChat(DeliveryPrivateChatEvent event);

    void deliverPrivateChatResult(long playerId, String reply);

    void handleProxiedLogin(long playerId, Coordinate toCoordinate, Connection connection);

    default void playerDropGuildStone(Player player, Coordinate at, int slot) {
        player.sendEvent(PlayerTextMessage.systip(player, "此地禁止创立门派。"));
    }

    default void confirmGuildCreation(Player player, int slot, String name) {

    }

    default void cancelGuildCreation(Player player) {

    }

    default void handleApplyKungFuCommand(Player player) {
        player.sendEvent(PlayerTextMessage.systip(player, "需在门派石附近。"));
    }

    default void applyGuildKungFu(Player player, ApplyGuildKungFuInput params) {

    }

    default void grantGuildKungFu(Player player, String toPlayer) {
        player.sendEvent(PlayerTextMessage.systip(player, "需在门派石附近。"));
    }

    default void guildInvite(Player player, String inviteeName) {
        player.sendEvent(PlayerTextMessage.systip(player, "需在门派石附近。"));
    }

    default void quitGuild(Player player) {
        player.sendEvent(PlayerTextMessage.systip(player, "需在门派石附近。"));
    }

    default void kickGuildMember(Player player, String kickee) {
        player.sendEvent(PlayerTextMessage.systip(player, "需在门派石附近。"));
    }
}
