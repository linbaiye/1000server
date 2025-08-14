package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.PlayerTextMessage;
import org.y1000.input.*;
import org.y1000.item.Item;
import org.y1000.item.ItemType;
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
        super(id, realmMap, itemManager, npcManager, playerManager, dynamicObjectManager, teleportManager, crossRealmEventSender, mapSdb, playerRepository);
        addEntityManager(guildManager);
        this.guildManager = guildManager;
    }

    @Override
    Logger log() {
        return log;
    }



    @Override
    void handleGuildCreation(Player source, ClientFoundGuildEvent event) {
        guildManager.foundGuild(source, event.coordinate(), event.name(), event.inventorySlot());
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
        else if (event.isTeachKungFu())
            sameRealmManagement(manager, event.target(), guildManager::teachGuildKungFu);
    }

    @Override
    public void update() {
        doUpdateEntities();
    }

    @Override
    public void playerDropGuildStone(Player player, Coordinate at, int slot) {
        Item item = player.inventory().getItem(slot);
        if (item == null || item.itemType() != ItemType.GUILD_STONE)
            return;
        player.sendEvent(PlayerTextMessage.systip(player, "确定在此处创立门派吗？"));
        var guildStone = guildManager.create("", at);
        playerManager().sendMessage(player, guildStone.captureSnapshot());
        //GuildStone guildStone = new GuildStone()
        //player.sendEvent();
    }

    @Override
    protected void handleLogin(Login login) {
    }

    @Override
    public void init() {
        doInit();
        guildManager.init();
    }

    @Override
    public void shutdown() {
        playerManager().shutdown();
        guildManager.shutdown();
    }
}
