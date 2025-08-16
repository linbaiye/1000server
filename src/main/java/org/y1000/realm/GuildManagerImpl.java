package org.y1000.realm;

import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.y1000.entities.RemoveEntityMessage;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.*;
import org.y1000.guild.GuildMembership;
import org.y1000.guild.Guild;
import org.y1000.item.Item;
import org.y1000.item.ItemSdb;
import org.y1000.item.ItemType;
import org.y1000.kungfu.KungFuFactory;
import org.y1000.kungfu.KungFuSdb;
import org.y1000.input.ApplyGuildKungFuInput;
import org.y1000.kungfu.attack.AttackKungFu;
import org.y1000.repository.GuildRepository;
import org.y1000.repository.ItemRepository;
import org.y1000.util.Coordinate;

import java.util.*;


/**
 * Realms that support guild operations.
 */
@Slf4j
public final class GuildManagerImpl extends AbstractActiveEntityManager<Guild> implements GuildManager {


    private final EntityIdGenerator entityIdGenerator;

    private final RealmEventSender crossRealmEventSender;

    private final RealmMap realmMap;

    private final GuildRepository guildRepository;

    private final ItemRepository itemRepository;

    private final EntityManagerFactory entityManagerFactory;

    private final int realmId;

    private final KungFuSdb kungFuSdb;

    private final KungFuFactory kungFuFactory;

    private final ItemSdb itemSdb;

    private final Map<Player, Guild> creatingStones;

    private final Set<Player> removeCreating;

    public GuildManagerImpl(ItemSdb itemSdb,
                            EntityIdGenerator entityIdGenerator,
                            MessageSender eventSender,
                            RealmEventSender crossRealmEventSender,
                            RealmMap realmMap,
                            GuildRepository guildRepository,
                            ItemRepository itemRepository,
                            EntityManagerFactory entityManagerFactory,
                            int realmId,
                            KungFuSdb kungFuSdb,
                            KungFuFactory kungFuFactory,
                            AOIManager aoiManager) {
        super(aoiManager, eventSender);
        Validate.notNull(entityIdGenerator);
        Validate.notNull(eventSender);
        Validate.notNull(crossRealmEventSender);
        Validate.notNull(realmMap);
        Validate.notNull(guildRepository);
        Validate.notNull(itemRepository);
        Validate.notNull(entityManagerFactory);
        Validate.notNull(kungFuSdb);
        Validate.notNull(kungFuFactory);
        this.entityIdGenerator = entityIdGenerator;
        this.crossRealmEventSender = crossRealmEventSender;
        this.realmMap = realmMap;
        this.guildRepository = guildRepository;
        this.itemRepository = itemRepository;
        this.entityManagerFactory = entityManagerFactory;
        this.realmId = realmId;
        this.kungFuSdb = kungFuSdb;
        this.kungFuFactory = kungFuFactory;
        this.itemSdb = itemSdb;
        creatingStones = new HashMap<>();
        removeCreating = new HashSet<>();
    }


    private Guild create(String name, Coordinate coordinate) {
        return new Guild(entityIdGenerator.next(), coordinate, 1000000, 1000000, name, null, itemSdb.getShape("门派石"));
    }

    private void doAdd(Guild guild) {
        add(guild);
        var snapshot = guild.captureSnapshot();
        getAoiManager().filterVisibleEntities(guild, Player.class)
                .forEach(player -> player.sendMessage(snapshot));
    }

    @Override
    public void init(Realm realm) {
        guildRepository.findByRealm(realm, entityIdGenerator).forEach(this::doAdd);
    }

    private String checkGuildKungFuSpecification(ApplyGuildKungFuInput request) {
        Validate.notNull(request);
        if (StringUtils.isBlank(request.getName())) {
            return "请输入正确名字";
        }
        if (request.getName().length() > 8) {
            return "名字最长8字符";
        }
        if (kungFuSdb.contains(request.getName())) {
            return "名字已被占用";
        }
        if (!request.getType().isMelee()) {
            return "武功只能是刀、剑、拳、槌、枪";
        }
        if (request.getSpeed() < 1 || request.getSpeed() > 99) {
            return "速度需在1-99之间";
        }
        if (request.getRecovery() < 1 || request.getRecovery() > 99) {
            return "恢复需在1-99之间";
        }
        if (request.getAvoid() < 1 || request.getAvoid() > 99) {
            return "闪躲需在1-99之间";
        }
        if (request.getHeadDamage() < 10 || request.getHeadDamage() > 70) {
            return "头攻需在10-70之间";
        }
        if (request.getArmDamage() < 10 || request.getArmDamage() > 70) {
            return "手攻需在10-70之间";
        }
        if (request.getBodyDamage() < 10 || request.getBodyDamage() > 70) {
            return "身攻需在10-70之间";
        }
        if (request.getHeadArmor() < 10 || request.getHeadArmor() > 70) {
            return "头防需在10-70之间";
        }
        if (request.getArmArmor() < 10 || request.getArmArmor() > 70) {
            return "手防需在10-70之间";
        }
        if (request.getBodyArmor() < 10 || request.getBodyArmor() > 70) {
            return "身防需在10-70之间";
        }
        if (request.getLegArmor() < 10 || request.getLegArmor() > 70) {
            return "脚防需在10-70之间";
        }
        if (request.getPowerToSwing() < 5 || request.getPowerToSwing() > 35) {
            return "武功消耗需在5-35之间";
        }
        if (request.getInnerPowerToSwing() < 5 || request.getInnerPowerToSwing() > 35) {
            return "内功消耗需在5-35之间";
        }
        if (request.getOuterPowerToSwing() < 5 || request.getOuterPowerToSwing() > 35) {
            return "外功消耗需在5-35之间";
        }
        if (request.getLifeToSwing() < 5 || request.getLifeToSwing() > 35) {
            return "活力消耗需在5-35之间";
        }
        if (request.getSpeed() + request.getBodyDamage() != 100) {
            return "速度和身攻之和需要等于100";
        }
        if (request.getRecovery() + request.getAvoid() != 100) {
            return "恢复和闪躲之和需要等于100";
        }
        if (request.getHeadDamage() + request.getArmDamage() + request.getLegDamage()
                + request.getBodyArmor() + request.getHeadArmor() + request.getArmArmor()
                + request.getLegArmor() != 228) {
            return "头攻+手攻+脚攻+身防+头防+手防+脚防需要等于228";
        }
        if (request.getOuterPowerToSwing() + request.getLifeToSwing() + request.getPowerToSwing() +
                request.getInnerPowerToSwing() != 80) {
            return "外功消耗+内功消耗+武功消耗+活力消耗需要等于80";
        }
        if (guildRepository.countGuildKungFu(request.getName()) > 0) {
            return "武功名已存在";
        }
        return null;
    }


    @Override
    public void shutdown() {
        getEntities().forEach(guildRepository::update);
    }

    /*private void handleLifebarEvent(EntityLifebarEvent lifebarEvent) {
        if (lifebarEvent.source() instanceof GuildStone guildStone) {
            eventSender.notifyVisiblePlayers(lifebarEvent.source(), lifebarEvent);
            eventSender.notifyVisiblePlayers(lifebarEvent.source(),
                    TextMessage.leftside(guildStone.idName() + ": " + lifebarEvent.getCurrent() + "/" + lifebarEvent.getMax()));
        }
    }*/

    private String checkNotNullAndGuildRange(Player source, Player target) {
//        Validate.notNull(source);
//        Validate.notNull(target);
//        if (source.guildMembership().isEmpty()) {
//            return "你还没有门派。";
//        }
//        GuildMembership sourceMembership = source.guildMembership().get();
//        Optional<GuildStone> first = getEntities().stream().filter(guildStone -> guildStone.getGuildId() == sourceMembership.guildId())
//                .findFirst();
//        if (first.isEmpty() || first.get().coordinate().directDistance(source.coordinate()) > 2) {
//            return "门派石距离太远。";
//        }
//        if (source.coordinate().directDistance(target.coordinate()) > 2) {
//            return "玩家距离太远。";
//        }
        return null;
    }



    @Override
    public void inviteMember(Player source, Player target) {
        var ret = checkNotNullAndGuildRange(source, target);
        if (ret != null) {
//            eventSender.notifySelf(PlayerTextMessage.systip(source, ret));
            return;
        }
        if (target.guildMembership().isPresent()) {
//            eventSender.notifySelf(PlayerTextMessage.systip(source, target.viewName() + "已有门派。"));
            return;
        }
        GuildMembership sourceMembership = source.guildMembership().orElseThrow();
        if (!sourceMembership.canInvite()) {
//            eventSender.notifySelf(PlayerTextMessage.systip(source, "门主或副门才能邀请门人。"));
            return;
        }
//        target.joinGuild(new GuildMembership(sourceMembership.guildId(),"", sourceMembership.guildName()));
//        eventSender.notifyVisiblePlayersAndSelf(target, new PlayerUpdateGuildEvent(target));
//        crossRealmEventSender.send(GuildBroadcastTextEvent.tip(sourceMembership.guildId(), target.viewName() + "加入了门派。"));
    }

    /*
    private void handleDieEvent(DynamicObjectDieEvent dieEvent) {
        if (dieEvent.source() instanceof GuildStone guildStone) {
            crossRealmEventSender.send(BroadcastTextEvent.leftUp(guildStone.idName() + " 被灭门了"));
            crossRealmEventSender.send(new DismissGuildEvent(guildStone.getPersistentId()));
            eventSender.notifyVisiblePlayers(dieEvent.source(), new RemoveEntityMessage(dieEvent.source().id()));
            remove(guildStone);
            eventSender.remove(guildStone);
            guildRepository.deleteGuildAndMembership(guildStone.getPersistentId());
        }
    }*/


    @Override
    protected Logger log() {
        return log;
    }

    @Override
    public void playerDropGuildStone(Player player, Coordinate at, int slot) {
        if (player.guildMembership().isPresent()) {
            player.sendEvent(PlayerTextMessage.systip(player, "你已有门派。"));
            return;
        }
        Item item = player.inventory().getItem(slot);
        if (item == null || item.itemType() != ItemType.GUILD_STONE)
            return;
        if (!realmMap.movable(at)) {
            player.sendEvent(PlayerTextMessage.systip(player, "该位置不可放置门派石。"));
            return;
        }
        if (at.neighbours().stream().anyMatch(c -> !realmMap.tileMovable(c))) {
            player.sendEvent(PlayerTextMessage.systip(player, "门派石八方不可有遮挡。"));
            return;
        }
        var s = creatingStones.remove(player);
        if (s != null)
            player.sendEvent(DirectMessage.of(player,  new RemoveEntityMessage(s.id())));
        var guildStone = create("你的门派石将放置于此处", at);
        creatingStones.put(player, guildStone);
        player.sendEvent(new DirectMessage(player, guildStone.captureDemoSnapshot()));
        player.sendEvent(new DirectMessage(player, ShowCreateGuildWindowMessage.show(guildStone.id(), slot)));
    }

    @Override
    public void confirmGuildCreation(Player player, int slot, String name) {
        Guild demo = creatingStones.remove(player);
        if (demo == null)
            return;
        Item item = player.inventory().getItem(slot);
        if (item == null || item.itemType() != ItemType.GUILD_STONE)
            return;
        if (guildRepository.countByName(name) > 0) {
            player.sendEvent(PlayerTextMessage.systip(player, "门派名称已经存在。"));
            return;
        }
        var guildStone = create(name, demo.coordinate());
        guildStone.foundedBy(player);
        guildRepository.save(guildStone);
        player.sendMessage(new RemoveEntityMessage(demo.id()));
        player.inventory().decrease(slot);
        player.sendEvent(UpdateInventorySlotMessage.update(player, slot));
        player.sendEvent(PlayerTextMessage.systip(player, "恭喜你成为了<" + name + ">门派门主。"));
        doAdd(guildStone);
    }

    @Override
    public void update(long delta) {
        getEntities().forEach(guildStone -> guildStone.update((int)delta));
        removeCreating.clear();
        creatingStones.forEach((p, s) -> {
            if (p.isLeftRealm() || p.coordinate().directDistance(s.coordinate()) >= 5) {
                p.sendMessage(new RemoveEntityMessage(s.id()));
                removeCreating.add(p);
            }
        });
        removeCreating.forEach(creatingStones::remove);
    }

    @Override
    public void cancelGuildCreation(Player player) {
        Guild remove = creatingStones.remove(player);
        if (remove != null)
            player.sendMessage(new RemoveEntityMessage(remove.id()));
    }


    private Guild findPlayerGuildStone(Player player) {
        Set<Guild> guilds = getAoiManager().filterVisibleEntities(player, Guild.class);
        for (Guild guild : guilds) {
            if (guild.has(player))
                return guild;
        }
        return null;
    }

    // 处理 @申请门武 命令.
    @Override
    public void handleApplyGuildKungFuCommand(Player player) {
        Guild playerGuild = findPlayerGuildStone(player);
        if (playerGuild == null) {
            player.sendEvent(PlayerTextMessage.systip(player, "需在门派石附近。"));
            return;
        }
        if (!playerGuild.isFounder(player)) {
            player.sendEvent(PlayerTextMessage.systip(player, "你不是门主。"));
            return;
        }
        if (playerGuild.guildKungFu().isPresent()) {
            player.sendEvent(PlayerTextMessage.systip(player, "门派已有门武。"));
            return;
        }
        player.sendMessage(ApplyKungFuWindowMessage.open());
    }

    @Override
    public void applyGuildKungFu(Player player, ApplyGuildKungFuInput params) {
        String s = checkGuildKungFuSpecification(params);
        if (s != null) {
            player.sendMessage(ApplyKungFuWindowMessage.message(s));
            return;
        }
        Guild playerGuild = findPlayerGuildStone(player);
        if (playerGuild == null || !playerGuild.isFounder(player) || playerGuild.guildKungFu().isPresent())
            return;
        kungFuFactory.registerAttackKungFuParameters(params);
        AttackKungFu kungFu = (AttackKungFu) kungFuFactory.create(params.getName());
        playerGuild.registerGuildKungFu(kungFu);
        guildRepository.update(playerGuild);
        player.sendEvent(PlayerTextMessage.systip(player, "门武申请成功。"));
        player.sendMessage(ApplyKungFuWindowMessage.close());
    }

    private Player findNearbyPlayer(Player player, String name) {
        if (player.viewName().equals(name))
            return player;
        Set<Player> players = getAoiManager().filterVisibleEntities(player, Player.class);
        return players.stream().filter(p -> p.viewName().equals(name)).findFirst().orElse(null);
    }

    @Override
    public void grantGuildKungFu(Player player, String toPlayer) {
        Guild playerGuild = findPlayerGuildStone(player);
        if (playerGuild == null) {
            player.sendEvent(PlayerTextMessage.systip(player, "需在门派石附近。"));
            return;
        }
        if (!playerGuild.canGrantKungFu(player)) {
            player.sendEvent(PlayerTextMessage.systip(player, "你不是门主或副门主。"));
            return;
        }
        AttackKungFu attackKungFu = playerGuild.guildKungFu().orElse(null);
        if (attackKungFu == null) {
            player.sendEvent(PlayerTextMessage.systip(player, "<" + playerGuild.guildName() + ">还没有门武。"));
            return;
        }
        var t = findNearbyPlayer(player, toPlayer);
        if (t == null) {
            player.sendEvent(PlayerTextMessage.systip(player, "被传授玩家需在门派石附近。"));
            return;
        }
        if (!playerGuild.has(t)) {
            player.sendEvent(PlayerTextMessage.systip(player, "被传授玩家不是门人。"));
            return;
        }
        if (t.learnGuildKungFu(attackKungFu))
            player.sendEvent(PlayerTextMessage.systip(player, "成功传授门武给<" + toPlayer + ">。"));
        else
            player.sendEvent(PlayerTextMessage.systip(player, "传授失败。"));
    }

}
