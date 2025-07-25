package org.y1000.entities.objects;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.y1000.guild.GuildStone;
import org.y1000.persistence.GuildStonePo;
import org.y1000.realm.RealmMap;
import org.y1000.sdb.DynamicObjectSdb;
import org.y1000.util.Coordinate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public final class DynamicObjectFactoryImpl implements DynamicObjectFactory {
    private final DynamicObjectSdb dynamicObjectSdb;

    public DynamicObjectFactoryImpl(DynamicObjectSdb dynamicObjectSdb) {
        this.dynamicObjectSdb = dynamicObjectSdb;
    }

    @Override
    public IDynamicObject createDynamicObject(String name,
                                              long id,
                                              RealmMap realmMap,
                                              Coordinate coordinate) {
        Validate.notNull(name);
        Validate.notNull(realmMap);
        Validate.notNull(coordinate);
        DynamicObjectType kind = dynamicObjectSdb.getKind(name);
        if (kind == DynamicObjectType.TRIGGER) {
            return TriggerDynamicObject.builder()
                    .id(id)
                    .idName(name)
                    .realmMap(realmMap)
                    .coordinate(coordinate)
                    .dynamicObjectSdb(dynamicObjectSdb)
                    .build();
        } else if (kind == DynamicObjectType.KILLABLE) {
            return dynamicObjectSdb.getRegenInterval(name) > 0 ?
            RespawnKillableDynamicObject.builder()
                    .id(id)
                    .coordinate(coordinate)
                    .idName(name)
                    .realmMap(realmMap)
                    .dynamicObjectSdb(dynamicObjectSdb)
                    .build()
                    :
                    KillableDynamicObject.builder()
                            .id(id)
                            .coordinate(coordinate)
                            .idName(name)
                            .realmMap(realmMap)
                            .dynamicObjectSdb(dynamicObjectSdb)
                            .build();
        } else if (kind == DynamicObjectType.YAOHUA) {
            return Yaohua.builder()
                    .id(id)
                    .coordinate(coordinate)
                    .idName(name)
                    .realmMap(realmMap)
                    .dynamicObjectSdb(dynamicObjectSdb)
                    .build();
        } else if (kind == DynamicObjectType.IMMUNE) {

        }
        log.error("Unable to create dynamic object : " + name);
        return null;
    }

    @Override
    public GuildStone createGuildStone(long id, String name, int realmId, RealmMap realmMap, Coordinate coordinate) {
        Validate.notNull(name);
        Validate.notNull(realmMap);
        Validate.notNull(coordinate);
        if (checkCreateGuildStone(name) != null)
            throw new IllegalArgumentException();
        GuildStonePo stonePo = GuildStonePo.builder()
                .createdTime(LocalDateTime.now())
                .x(coordinate.x())
                .y(coordinate.y())
                .currentHealth(2000000)
                .maxHealth(2000000)
                .realmId(realmId)
                .name(name)
                .build();
        return GuildStone.builder()
                .id(id)
                .realmMap(realmMap)
                .coordinate(coordinate)
                .realmId(stonePo.getRealmId())
                .dynamicObjectSdb(stonePo)
                .currentHealth(stonePo.getCurrentHealth())
                .idName(stonePo.getName())
                .build();
    }

    @Override
    public String checkCreateGuildStone(String name) {
        if (StringUtils.isEmpty(name))
            return "请输入正确门派名字";
        if (name.length() >= 8)
            return "门派名字最长8个字";
        return null;
    }


    private List<Animation> buildAnimations(String name, DynamicObjectSdb sdb) {
        List<Animation> animations = new ArrayList<>();
        String sStep0 = sdb.getSStep0(name);
        String eStep0 = sdb.getEStep0(name);
        int life = sdb.getLife(name);
        String trigger = sdb.getEventItem(name);
        boolean active = StringUtils.isNotEmpty(trigger) || life != 0;
        List<int[]> startEndList = new ArrayList<>();
        if (StringUtils.isNotEmpty(sStep0) && StringUtils.isNotEmpty(eStep0)) {
            startEndList.add(new int[]{Integer.parseInt(sStep0), Integer.parseInt(eStep0)});
        }
        String sStep1 = sdb.getSStep1(name);
        String eStep1 = sdb.getEStep1(name);
        if (StringUtils.isNotEmpty(sStep1) && StringUtils.isNotEmpty(eStep1)) {
            startEndList.add(new int[]{Integer.parseInt(sStep1), Integer.parseInt(eStep1)});
        }
        String sStep2 = sdb.getSStep2(name);
        String eStep2 = sdb.getEStep2(name);
        if (StringUtils.isNotEmpty(sStep2) && StringUtils.isNotEmpty(eStep2)) {
            startEndList.add(new int[]{Integer.parseInt(sStep2), Integer.parseInt(eStep2)});
        }
        for (int i = 0; i < startEndList.size(); i++) {
            int start = startEndList.get(i)[0];
            int end = startEndList.get(i)[1];
            boolean loop = start == end;
            animations.add(new Animation(start, end))
        }
    }

    @Override
    public DynamicObject create(long id, String name, RealmMap realmMap, Coordinate coordinate) {
        return null;
    }
}
