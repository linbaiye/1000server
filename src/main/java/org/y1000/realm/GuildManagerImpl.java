package org.y1000.realm;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import lombok.extern.slf4j.Slf4j;
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
import org.y1000.message.PlayerTextEvent;
import org.y1000.message.RemoveEntityMessage;
import org.y1000.message.serverevent.TextMessage;
import org.y1000.message.serverevent.UpdateInventorySlotEvent;
import org.y1000.realm.event.BroadcastTextEvent;
import org.y1000.realm.event.DismissGuildEvent;
import org.y1000.repository.GuildRepository;
import org.y1000.repository.ItemRepository;
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

    public GuildManagerImpl(DynamicObjectFactory factory,
                            EntityIdGenerator entityIdGenerator,
                            EntityEventSender eventSender,
                            CrossRealmEventSender crossRealmEventSender,
                            RealmMap realmMap,
                            GuildRepository guildRepository,
                            ItemRepository itemRepository,
                            EntityManagerFactory entityManagerFactory,
                            int realmId) {
        Validate.notNull(factory);
        Validate.notNull(entityIdGenerator);
        Validate.notNull(eventSender);
        Validate.notNull(crossRealmEventSender);
        Validate.notNull(realmMap);
        Validate.notNull(guildRepository);
        Validate.notNull(itemRepository);
        Validate.notNull(entityManagerFactory);
        this.factory = factory;
        this.entityIdGenerator = entityIdGenerator;
        this.eventSender = eventSender;
        this.crossRealmEventSender = crossRealmEventSender;
        this.realmMap = realmMap;
        this.guildRepository = guildRepository;
        this.itemRepository = itemRepository;
        this.entityManagerFactory = entityManagerFactory;
        this.realmId = realmId;
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
