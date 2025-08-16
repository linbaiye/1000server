package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.PlayerTextMessage;
import org.y1000.input.*;
import org.y1000.repository.PlayerRepository;
import org.y1000.sdb.MapSdb;
import org.y1000.util.Coordinate;

import java.util.function.BiConsumer;

@Slf4j
class GuildableRealm extends AbstractRealm {
    private final GuildManager guildManager;


    public GuildableRealm(int id, RealmMap realmMap,
                          GroundItemManager itemManager,
                          NpcManager npcManager,
                          PlayerManager playerManager,
                          DynamicObjectManager dynamicObjectManager,
                          TeleportManager teleportManager,
                          RealmEventSender crossRealmEventSender,
                          MapSdb mapSdb,
                          GuildManager guildManager,
                          PlayerRepository playerRepository) {
        super(id, realmMap, itemManager, npcManager, playerManager,
                dynamicObjectManager, teleportManager, crossRealmEventSender, mapSdb, playerRepository);
        addEntityManager(guildManager);
        this.guildManager = guildManager;
    }

    @Override
    Logger log() {
        return log;
    }

    private void sameRealmManagement(Player source, String target, BiConsumer<Player, Player> handler) {
        playerManager().allPlayers().stream()
                .filter(player -> player.viewName().equals(target))
                .findFirst()
                .ifPresentOrElse(t -> handler.accept(source, t),
                        () -> source.sendEvent(PlayerTextMessage.systip(source, "玩家不在线或不在身边。")));
    }


    private void handleManagement(Player manager, ClientManageGuildEvent event) {
        if (event.isInvite())
            sameRealmManagement(manager, event.target(), guildManager::inviteMember);
    }

    @Override
    public void update() {
        doUpdateEntities();
    }

    @Override
    public void playerDropGuildStone(Player player, Coordinate at, int slot) {
        guildManager.playerDropGuildStone(player, at, slot);
    }

    @Override
    protected void handleLogin(Login login) {
        acceptLogin(login.playerId(), login.connection(), null);
    }

    @Override
    public void init() {
        doInit();
        guildManager.init(this);
    }

    @Override
    public void confirmGuildCreation(Player player, int slot, String name) {
        guildManager.confirmGuildCreation(player, slot, name);
    }

    @Override
    public void cancelGuildCreation(Player player) {
        guildManager.cancelGuildCreation(player);
    }

    @Override
    public void handleApplyKungFuCommand(Player player) {
        guildManager.handleApplyGuildKungFuCommand(player);
    }

    @Override
    public void applyGuildKungFu(Player player, ApplyGuildKungFuInput params) {
        guildManager.applyGuildKungFu(player, params);
    }

    @Override
    public void grantGuildKungFu(Player player, String toPlayer) {
        guildManager.grantGuildKungFu(player, toPlayer);
    }



    @Override
    public void shutdown() {
        playerManager().shutdown();
        guildManager.shutdown();
    }
}
