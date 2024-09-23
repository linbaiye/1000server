package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.y1000.entities.objects.DynamicObjectFactory;
import org.y1000.entities.players.Player;
import org.y1000.event.EntityEvent;
import org.y1000.guild.GuildMembership;
import org.y1000.guild.GuildStone;
import org.y1000.message.PlayerTextEvent;
import org.y1000.repository.GuildRepository;
import org.y1000.util.Coordinate;


@Slf4j
public final class GuildManagerImpl extends AbstractActiveEntityManager<GuildStone> implements GuildManager {

    private final DynamicObjectFactory factory;

    private final EntityIdGenerator entityIdGenerator;

    private final EntityEventSender eventSender;

    private final CrossRealmEventSender crossRealmEventSender;

    private final RealmMap realmMap;

    private final GuildRepository guildRepository;

    private final int realmId;

    public GuildManagerImpl(DynamicObjectFactory factory,
                            EntityIdGenerator entityIdGenerator,
                            EntityEventSender eventSender,
                            CrossRealmEventSender crossRealmEventSender,
                            RealmMap realmMap,
                            GuildRepository guildRepository,
                            int realmId) {
        this.factory = factory;
        this.entityIdGenerator = entityIdGenerator;
        this.eventSender = eventSender;
        this.crossRealmEventSender = crossRealmEventSender;
        this.realmMap = realmMap;
        this.guildRepository = guildRepository;
        this.realmId = realmId;
    }

    @Override
    public void create(Player founder, Coordinate coordinate, String name) {
        Validate.notNull(founder);
        Validate.notNull(coordinate);
        if (founder.guildMembership().isPresent()) {
            founder.emitEvent(PlayerTextEvent.systemTip(founder, "你已有门派。"));
            return;
        }
        int i = guildRepository.countByName(name);
        if (i > 0) {
            founder.emitEvent(PlayerTextEvent.systemTip(founder, "此门派名称已存在。"));
            return;
        }
        GuildStone guildstone = factory.createGuildStone(name, realmId, realmMap, coordinate);
        GuildMembership membership = new GuildMembership("门主", guildstone.idName());
        guildRepository.save(guildstone, founder, membership);
    }

    @Override
    public void init() {
        guildRepository.findByRealm(realmId, entityIdGenerator, realmMap).forEach(stone -> {
            eventSender.add(stone);
            add(stone);
        });
    }

    @Override
    public void onEvent(EntityEvent entityEvent) {

    }

    @Override
    protected Logger log() {
        return log;
    }

    @Override
    public void update(long delta) {

    }
}
