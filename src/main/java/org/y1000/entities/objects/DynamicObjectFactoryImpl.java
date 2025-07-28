package org.y1000.entities.objects;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.DynamicObjectDropItemAbility;
import org.y1000.entities.creatures.npc.NpcDropItemAbility;
import org.y1000.guild.GuildStone;
import org.y1000.persistence.GuildStonePo;
import org.y1000.realm.DynamicObjectEventListener;
import org.y1000.realm.Realm;
import org.y1000.realm.RealmMap;
import org.y1000.sdb.CreateDynamicObjectSdb;
import org.y1000.sdb.DynamicObjectSdb;
import org.y1000.util.Coordinate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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

    private List<String> parseNpcNames(String callNpc) {
        String[] split = callNpc.split(":");
        List<String> result = new ArrayList<>();
        for (int i = 0; i * 2 < split.length; i++) {
            result.add(split[i * 2]);
        }
        return result;
    }

    private List<Object> buildAbilities(String name, String number, CreateDynamicObjectSdb createDynamicObjectSdb) {
        String sStep0 = dynamicObjectSdb.getSStep0(name);
        String eStep0 = dynamicObjectSdb.getEStep0(name);
        int aniId = 1;
        StaticAbility staticAbility = new StaticAbility(new Animation(Integer.parseInt(sStep0), Integer.parseInt(eStep0), true, aniId++));
        List<Object> abilities = new ArrayList<>();
        abilities.add(staticAbility);
        int life = dynamicObjectSdb.getLife(name);
        if (life > 0) {
            abilities.add(new DynamicObjectHurtAbility(life, dynamicObjectSdb.getSoundSpecial(name).orElse(null),
                    dynamicObjectSdb.getSoundDie(name).orElse(null),
                    createDynamicObjectSdb.getCallNpc(number).map(this::parseNpcNames).orElse(Collections.emptyList())));
        }
        String eventItem = dynamicObjectSdb.getEventItem(name);
        if (StringUtils.isNotEmpty(eventItem)) {
            String[] split = eventItem.split(":");
            abilities.add(new DynamicObjectTriggerAbility(split[0], Integer.parseInt(split[1])));
        }
        int openedMillis = dynamicObjectSdb.getOpenedInterval(name) * Realm.STEP_MILLIS;
        if (openedMillis > 0) {
            List<Animation> animations = new ArrayList<>();
            String sStep1 = dynamicObjectSdb.getSStep1(name);
            String eStep1 = dynamicObjectSdb.getEStep1(name);
            animations.add(new Animation(Integer.parseInt(sStep1), Integer.parseInt(eStep1), false, aniId++));
            String sStep2 = dynamicObjectSdb.getSStep2(name);
            String eStep2 = dynamicObjectSdb.getEStep2(name);
            if (StringUtils.isNotEmpty(sStep2) && StringUtils.isNotEmpty(eStep2)) {
                animations.add(new Animation(Integer.parseInt(sStep2), Integer.parseInt(eStep2), true, aniId));
            }
            abilities.add(new OpenAbility(openedMillis, animations, dynamicObjectSdb.getSoundEvent(name).orElse(null),
                    dynamicObjectSdb.isRemove(name), dynamicObjectSdb.getRegenInterval(name) * Realm.STEP_MILLIS));
        }
        createDynamicObjectSdb.getDropItem(number).flatMap(DynamicObjectDropItemAbility::parse)
                .ifPresent(abilities::add);
        return abilities;
    }

    private Set<Coordinate> parseCoordinates(String idName, Coordinate coordinate) {
        String guardPos = dynamicObjectSdb.getGuardPos(idName);
        if (StringUtils.isEmpty(guardPos)) {
            return Set.of(coordinate);
        }
        String[] tokens = guardPos.split(":");
        if (tokens.length % 2 != 0) {
            throw new IllegalArgumentException("Invalid guardPos: " + guardPos + ", name:" + idName);
        }
        Coordinate[] guardCoordinates = new Coordinate[tokens.length / 2 + 1];
        guardCoordinates[0] = coordinate;
        for (int i = 0, j = 1; i < tokens.length / 2; i++, j++) {
            int x = Integer.parseInt(tokens[i * 2]);
            int y = Integer.parseInt(tokens[i * 2 + 1]);
            guardCoordinates[j] = coordinate.move(x, y);
        }
        return Set.of(guardCoordinates);
    }

    @Override
    public DynamicObject create(long id, String number, DynamicObjectEventListener eventListener, CreateDynamicObjectSdb createDynamicObjectSdb) {
        int x = createDynamicObjectSdb.getX(number);
        int y = createDynamicObjectSdb.getY(number);
        var coordinate = Coordinate.xy(x, y);
        var name = createDynamicObjectSdb.getName(number);
        return new DynamicObject(id, dynamicObjectSdb.getViewName(name).orElse(null),
                buildAbilities(name, number, createDynamicObjectSdb), parseCoordinates(name, coordinate),
                coordinate, eventListener, "x" + dynamicObjectSdb.getShape(name));
    }
}
