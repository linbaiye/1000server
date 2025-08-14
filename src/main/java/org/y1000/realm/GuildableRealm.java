package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.RemoveEntityMessage;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.PlayerTextMessage;
import org.y1000.entities.players.event.ShowCreateGuildWindowMessage;
import org.y1000.entities.players.event.UpdateInventorySlotMessage;
import org.y1000.guild.GuildStone;
import org.y1000.input.*;
import org.y1000.item.Item;
import org.y1000.item.ItemType;
import org.y1000.repository.PlayerRepository;
import org.y1000.sdb.MapSdb;
import org.y1000.util.Coordinate;

import java.util.*;
import java.util.function.BiConsumer;

@Slf4j
class GuildableRealm extends AbstractRealm {
    private final GuildManager guildManager;

    private final Map<Player, GuildStone> creatingStones;

    private final Set<Player> removeCreating;

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
        creatingStones = new HashMap<>();
        removeCreating = new HashSet<>();
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
        else if (event.isTeachKungFu())
            sameRealmManagement(manager, event.target(), guildManager::teachGuildKungFu);
    }

    @Override
    public void update() {
        doUpdateEntities();
        removeCreating.clear();
        creatingStones.forEach((p, s) -> {
            if (p.isLeftRealm() || p.coordinate().directDistance(s.coordinate()) >= 5) {
                playerManager().sendMessage(p, new RemoveEntityMessage(s.id()));
                removeCreating.add(p);
                log().debug("Removed");
            }
        });
        removeCreating.forEach(creatingStones::remove);
    }

    @Override
    public void playerDropGuildStone(Player player, Coordinate at, int slot) {
        Item item = player.inventory().getItem(slot);
        if (item == null || item.itemType() != ItemType.GUILD_STONE)
            return;
        var s = creatingStones.remove(player);
        if (s != null)
            playerManager().sendMessage(player, new RemoveEntityMessage(s.id()));
        var guildStone = guildManager.create("你的门派石将放置于此处", at);
        creatingStones.put(player, guildStone);
        playerManager().sendMessage(player, guildStone.captureDemoSnapshot());
        playerManager().sendMessage(player, ShowCreateGuildWindowMessage.show(guildStone.id(), slot));
    }

    @Override
    protected void handleLogin(Login login) {
        acceptLogin(login.playerId(), login.connection(), null);
    }

    @Override
    public void init() {
        doInit();
        guildManager.init();
    }

    @Override
    public void confirmGuildCreation(Player player, int slot, String name) {
        GuildStone remove = creatingStones.remove(player);
        if (remove == null)
            return;
        Item item = player.inventory().getItem(slot);
        if (item == null || item.itemType() != ItemType.GUILD_STONE)
            return;
        player.inventory().decrease(slot);
        playerManager().sendMessage(player, new RemoveEntityMessage(remove.id()));
        var guildStone = guildManager.create(name, remove.coordinate());
        playerManager().sendMessage(player, guildStone.captureSnapshot());
        player.sendEvent(UpdateInventorySlotMessage.update(player, slot));
        player.sendEvent(PlayerTextMessage.systip(player, "恭喜你成为了<" + name + ">门派门主。"));
    }

    @Override
    public void cancelGuildCreation(Player player) {
        GuildStone remove = creatingStones.remove(player);
        if (remove != null)
            playerManager().sendMessage(player, new RemoveEntityMessage(remove.id()));
    }

    @Override
    public void shutdown() {
        playerManager().shutdown();
        guildManager.shutdown();
    }
}
