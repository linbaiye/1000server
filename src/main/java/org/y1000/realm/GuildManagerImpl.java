package org.y1000.realm;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.y1000.entities.EntityLifebarEvent;
import org.y1000.entities.objects.DynamicObjectDieEvent;
import org.y1000.entities.objects.DynamicObjectFactory;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.PlayerUpdateGuildEvent;
import org.y1000.event.EntityEvent;
import org.y1000.guild.GuildMembership;
import org.y1000.guild.GuildStone;
import org.y1000.item.Item;
import org.y1000.item.ItemType;
import org.y1000.kungfu.KungFuSdb;
import org.y1000.message.PlayerTextEvent;
import org.y1000.message.RemoveEntityMessage;
import org.y1000.message.clientevent.ClientCreateGuildKungFuEvent;
import org.y1000.message.serverevent.TextMessage;
import org.y1000.message.serverevent.UpdateInventorySlotEvent;
import org.y1000.persistence.AttackKungFuParametersProvider;
import org.y1000.realm.event.BroadcastTextEvent;
import org.y1000.realm.event.DismissGuildEvent;
import org.y1000.repository.GuildRepository;
import org.y1000.repository.ItemRepository;
import org.y1000.repository.KungFuBookRepository;
import org.y1000.util.Coordinate;


/**
 * Realms that support guild operations.
 */
@Slf4j
public final class GuildManagerImpl extends AbstractActiveEntityManager<GuildStone> implements GuildManager {

    private final DynamicObjectFactory factory;

    private final EntityIdGenerator entityIdGenerator;

    private final EntityEventSender eventSender;

    private final CrossRealmEventSender crossRealmEventSender;

    private final RealmMap realmMap;

    private final GuildRepository guildRepository;

    private final ItemRepository itemRepository;

    private final EntityManagerFactory entityManagerFactory;

    private final int realmId;

    private final KungFuSdb kungFuSdb;

    private final KungFuBookRepository kungFuBookRepository;

    public GuildManagerImpl(DynamicObjectFactory factory,
                            EntityIdGenerator entityIdGenerator,
                            EntityEventSender eventSender,
                            CrossRealmEventSender crossRealmEventSender,
                            RealmMap realmMap,
                            GuildRepository guildRepository,
                            ItemRepository itemRepository,
                            EntityManagerFactory entityManagerFactory,
                            int realmId,
                            KungFuSdb kungFuSdb,
                            KungFuBookRepository kungFuBookRepository) {
        Validate.notNull(factory);
        Validate.notNull(entityIdGenerator);
        Validate.notNull(eventSender);
        Validate.notNull(crossRealmEventSender);
        Validate.notNull(realmMap);
        Validate.notNull(guildRepository);
        Validate.notNull(itemRepository);
        Validate.notNull(entityManagerFactory);
        Validate.notNull(kungFuSdb);
        Validate.notNull(kungFuBookRepository);
        this.factory = factory;
        this.entityIdGenerator = entityIdGenerator;
        this.eventSender = eventSender;
        this.crossRealmEventSender = crossRealmEventSender;
        this.realmMap = realmMap;
        this.guildRepository = guildRepository;
        this.itemRepository = itemRepository;
        this.entityManagerFactory = entityManagerFactory;
        this.realmId = realmId;
        this.kungFuSdb = kungFuSdb;
        this.kungFuBookRepository = kungFuBookRepository;
    }

    @Override
    public void foundGuild(Player founder, Coordinate coordinate, String name, int inventorySlot) {
        Validate.notNull(founder);
        Validate.notNull(coordinate);
        Validate.notNull(name);
        Item item = founder.inventory().getItem(inventorySlot);
        if (item == null || item.itemType() != ItemType.GUILD_STONE) {
            log().error("Invalid guild creation from player {} detected.", founder.id());
            return;
        }
        if (coordinate.directDistance(founder.coordinate()) > 4) {
            log().error("Invalid guild creation from player {} detected.", founder.id());
            return;
        }
        if (founder.guildMembership().isPresent()) {
            founder.emitEvent(PlayerTextEvent.systemTip(founder, "你已有门派。"));
            return;
        }
        if (!realmMap.movable(coordinate)) {
            founder.emitEvent(PlayerTextEvent.systemTip(founder, "该位置不可放置门派石。"));
            return;
        }
        if (coordinate.neighbours().stream().anyMatch(c -> !realmMap.tileMovable(c))) {
            founder.emitEvent(PlayerTextEvent.systemTip(founder, "门派石八方不可有遮挡。"));
            return;
        }
        int i = guildRepository.countByName(name);
        if (i > 0) {
            founder.emitEvent(PlayerTextEvent.systemTip(founder, "此门派名称已存在。"));
            return;
        }
        var reason = factory.checkCreateGuildStone(name);
        if (reason != null) {
            founder.emitEvent(PlayerTextEvent.systemTip(founder, reason));
            return;
        }
        GuildStone guildstone = factory.createGuildStone(entityIdGenerator.next(), name, realmId, realmMap, coordinate);
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            guildRepository.save(entityManager, guildstone, founder.id());
            GuildMembership membership = new GuildMembership(guildstone.getPersistentId(), "门主", guildstone.idName());
            founder.joinGuild(membership);
            guildRepository.upsertMembership(entityManager, founder.id(), membership);
            founder.inventory().remove(inventorySlot);
            itemRepository.save(entityManager, founder);
            transaction.commit();
            eventSender.notifySelf(UpdateInventorySlotEvent.remove(founder, inventorySlot));
            doAdd(guildstone);
            founder.emitEvent(PlayerTextEvent.systemTip(founder, "恭喜你，你已成为<" + guildstone.idName() + ">的门主。"));
            eventSender.notifyVisiblePlayersAndSelf(founder, new PlayerUpdateGuildEvent(founder));
        }
    }

    private void doAdd(GuildStone guildStone) {
        eventSender.add(guildStone);
        add(guildStone);
        eventSender.notifyVisiblePlayers(guildStone, guildStone.captureInterpolation());
        guildStone.registerEventListener(this);
    }

    @Override
    public void init() {
        guildRepository.findByRealm(realmId, entityIdGenerator, realmMap).forEach(this::doAdd);
    }


    private String checkGuildKungFuSpecification(ClientCreateGuildKungFuEvent request) {
        Validate.notNull(request);
        if (StringUtils.isBlank(request.getName())) {
            return "请输入正确名字";
        }
        if (request.getName().length() > 8) {
            return "名字最长8字符";
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
        if (kungFuBookRepository.countGuildKungFuParameter(request.getName()) > 0) {
            return "武功名字已存在";
        }
        return null;
    }

    private void saveGuildKungfuParameter(ClientCreateGuildKungFuEvent request) {
        if (checkGuildKungFuSpecification(request) != null) {
            throw new IllegalArgumentException();
        }
        String template;
        switch (request.getType()) {
            case AXE -> template = "无名槌法";
            case QUANFA -> template = "无名拳法";
            case SWORD -> template = "无名剑法";
            case BLADE -> template = "无名刀法";
            case SPEAR -> template = "无名枪术";
            default -> throw new IllegalArgumentException("Invalid type " + request.getType().name());
        }
        AttackKungFuParametersProvider provider = AttackKungFuParametersProvider.builder()
                .attackSpeed(request.getSpeed())
                .recovery(request.getRecovery())
                .avoid(request.getAvoid())
                .headDamage(request.getHeadDamage())
                .bodyDamage(request.getBodyDamage())
                .armDamage(request.getArmDamage())
                .legDamage(request.getLegDamage())
                .headArmor(request.getHeadArmor())
                .bodyArmor(request.getBodyArmor())
                .armArmor(request.getArmArmor())
                .legArmor(request.getLegArmor())
                .swingLife(request.getLifeToSwing())
                .swingPower(request.getPowerToSwing())
                .swingInnerPower(request.getInnerPowerToSwing())
                .swingOuterPower(request.getOuterPowerToSwing())
                .name(request.getName())
                .effectColor(kungFuSdb.effectColor(template))
                .type(request.getType())
                .swingSound(Integer.parseInt(kungFuSdb.getSoundSwing(template)))
                .strikeSound(Integer.parseInt(kungFuSdb.getSoundStrike(template)))
                .build();
        kungFuBookRepository.saveGuildKungFuParameter(provider);
    }

    @Override
    public void shutdown() {
        getEntities().forEach(guildRepository::update);
    }

    private void handleLifebarEvent(EntityLifebarEvent lifebarEvent) {
        if (lifebarEvent.source() instanceof GuildStone guildStone) {
            eventSender.notifyVisiblePlayers(lifebarEvent.source(), lifebarEvent);
            eventSender.notifyVisiblePlayers(lifebarEvent.source(),
                    TextMessage.leftside(guildStone.idName() + ": " + lifebarEvent.getCurrent() + "/" + lifebarEvent.getMax()));
        }
    }

    private void handleDieEvent(DynamicObjectDieEvent dieEvent) {
        if (dieEvent.source() instanceof GuildStone guildStone) {
            crossRealmEventSender.send(BroadcastTextEvent.leftUp(guildStone.idName() + " 被灭门了"));
            crossRealmEventSender.send(new DismissGuildEvent(guildStone.getPersistentId()));
            eventSender.notifyVisiblePlayers(dieEvent.source(), new RemoveEntityMessage(dieEvent.source().id()));
            remove(guildStone);
            eventSender.remove(guildStone);
            guildRepository.deleteGuildAndMembership(guildStone.getPersistentId());
            guildStone.clearListeners();
        }
    }

    @Override
    public void onEvent(EntityEvent entityEvent) {
        if (entityEvent instanceof EntityLifebarEvent lifebarEvent) {
            handleLifebarEvent(lifebarEvent);
        } else if (entityEvent instanceof DynamicObjectDieEvent dieEvent) {
            handleDieEvent(dieEvent);
        }
    }

    @Override
    protected Logger log() {
        return log;
    }

    @Override
    public void update(long delta) {
        getEntities().forEach(guildStone -> guildStone.update((int)delta));
    }
}
