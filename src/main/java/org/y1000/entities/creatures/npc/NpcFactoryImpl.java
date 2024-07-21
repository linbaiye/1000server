package org.y1000.entities.creatures.npc;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.monster.*;
import org.y1000.kungfu.KungFuSdb;
import org.y1000.realm.RealmMap;
import org.y1000.sdb.ActionSdb;
import org.y1000.sdb.*;
import org.y1000.util.Coordinate;

import java.util.*;

@Slf4j
public final class NpcFactoryImpl implements NpcFactory {

    private final ActionSdb actionSdb;
    private final MonstersSdb monsterSdb;
    private final KungFuSdb kungFuSdb;
    private final NpcSdb npcSdb;
    private final MerchantItemSdbRepository merchantItemSdbRepository;

    public NpcFactoryImpl(ActionSdb actionSdb,
                          MonstersSdb monsterSdb,
                          KungFuSdb kungFuSdb, NpcSdb npcSdb,
                          MerchantItemSdbRepository merchantItemSdbRepository) {
        this.actionSdb = actionSdb;
        this.monsterSdb = monsterSdb;
        this.kungFuSdb = kungFuSdb;
        this.npcSdb = npcSdb;
        this.merchantItemSdbRepository = merchantItemSdbRepository;
    }


    private Map<State, Integer> createDevirtueActionLengthMap(String animate) {
        Map<State, Integer> result = new HashMap<>();
        int idle = actionSdb.getActionLength(animate, State.IDLE);
        int move = actionSdb.getActionLength(animate, State.WALK);
        int hurt = actionSdb.getActionLength(animate, State.HURT);
        int die = actionSdb.getActionLength(animate, State.DIE);
        int frozen = actionSdb.getActionLength(animate, State.FROZEN);
        result.put(State.IDLE, idle);
        result.put(State.WALK, move);
        result.put(State.HURT, hurt);
        result.put(State.DIE, die);
        result.put(State.FROZEN, frozen < 100 ? frozen * 10 : frozen);
        return result;
    }

    private Map<State, Integer> createActionLengthMap(String animate) {
        Map<State, Integer> result = createDevirtueActionLengthMap(animate);
        int attack = actionSdb.getActionLength(animate, State.ATTACK);
        result.put(State.ATTACK, attack);
        return result;
    }

    private NpcRangedSkill createSkill(String name) {
        var magicNameAndLevel = monsterSdb.getAttackMagic(name);
        if (StringUtils.isEmpty(magicNameAndLevel)) {
            return null;
        }
        String magicName = magicNameAndLevel.split(":")[0];
        String bowImage = kungFuSdb.getBowImage(magicName);
        if (StringUtils.isEmpty(bowImage)) {
            return null;
        }
        return new NpcRangedSkill(Integer.parseInt(bowImage), kungFuSdb.getSoundSwing(magicName));
    }

    private AggressiveMonster createAggressiveCreature(String name, long id, RealmMap map, Coordinate coordinate) {
        return AggressiveMonster.builder()
                .id(id)
                .coordinate(coordinate)
                .direction(Direction.DOWN)
                .name(monsterSdb.getViewName(name))
                .realmMap(map)
                .stateMillis(createActionLengthMap(monsterSdb.getAnimate(name)))
                .attributeProvider(new MonsterAttributeProvider(name, monsterSdb))
                .skill(createSkill(name))
                .ai(new MonsterWanderingAI())
                .build();
    }

    private Npc createPassiveCreature(String name, long id, RealmMap map, Coordinate coordinate) {
        boolean attack = monsterSdb.attack(name);
        if (attack) {
            return PassiveMonster.builder()
                    .id(id)
                    .coordinate(coordinate)
                    .direction(Direction.DOWN)
                    .name(monsterSdb.getViewName(name))
                    .realmMap(map)
                    .stateMillis(createActionLengthMap(monsterSdb.getAnimate(name)))
                    .attributeProvider(new MonsterAttributeProvider(name, monsterSdb))
                    .ai(new MonsterWanderingAI(new ViolentNpcWanderingAI()))
                    .skill(createSkill(name))
                    .build();
        } else {
            int actionWidth = monsterSdb.getActionWidth(name);
            var ai = actionWidth == 0 ? NpcFrozenAI.INSTANCE : new SubmissiveWanderingAI();
            return SubmissiveNpc.builder()
                    .id(id)
                    .coordinate(coordinate)
                    .direction(Direction.DOWN)
                    .name(monsterSdb.getViewName(name))
                    .realmMap(map)
                    .stateMillis(createActionLengthMap(monsterSdb.getAnimate(name)))
                    .attributeProvider(new MonsterAttributeProvider(name, monsterSdb))
                    .ai(ai)
                    .build();
        }
    }


    private Npc createMonster(String name, long id, RealmMap realmMap, Coordinate coordinate) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(realmMap);
        Objects.requireNonNull(coordinate);
        String animate = monsterSdb.getAnimate(name);
        if (animate == null) {
            throw new NotImplementedException(name + " has no action sdb.");
        }
        boolean passive = monsterSdb.isPassive(name);
        return passive ? createPassiveCreature(name, id, realmMap, coordinate) : createAggressiveCreature(name, id, realmMap, coordinate);
    }



    @Override
    public Npc createMerchant(String name, long id, RealmMap realmMap, Coordinate coordinate) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(realmMap);
        Objects.requireNonNull(coordinate);
        String animate = npcSdb.getAnimate(name);
        if (animate == null) {
            throw new NotImplementedException(name + " has no action sdb.");
        }
        String npcText = npcSdb.getNpcText(name);
        MerchantItemSdb merchantItemSdb = merchantItemSdbRepository.load(npcText);
        return DevirtueMerchant.builder()
                .id(id)
                .coordinate(coordinate)
                .direction(Direction.DOWN)
                .name(npcSdb.getViewName(name))
                .realmMap(realmMap)
                .stateMillis(createDevirtueActionLengthMap(animate))
                .attributeProvider(new NonMonsterNpcAttributeProvider(name, npcSdb))
                .ai(new SubmissiveWanderingAI())
                .textFileName(npcText)
                .sell(merchantItemSdb.sell())
                .buy(merchantItemSdb.buy())
                .build();
    }


    private Guardian createGuardian(String name, long id, RealmMap realmMap, Coordinate coordinate) {
        String animate = npcSdb.getAnimate(name);
        return Guardian.builder()
                .id(id)
                .coordinate(coordinate)
                .direction(Direction.DOWN)
                .name(npcSdb.getViewName(name))
                .realmMap(realmMap)
                .stateMillis(createActionLengthMap(animate))
                .attributeProvider(new NonMonsterNpcAttributeProvider(name, npcSdb))
                .ai(new ViolentNpcWanderingAI())
                .build();
    }

    private Npc createNonMonsterNpc(String name, long id, RealmMap realmMap, Coordinate coordinate) {
        if (npcSdb.isSeller(name)) {
            return createMerchant(name, id, realmMap, coordinate);
        } else {
            return createGuardian(name, id, realmMap, coordinate);
        }
    }

    @Override
    public Npc createNpc(String name, long id, RealmMap realmMap, Coordinate coordinate) {
        Validate.notNull(name);
        Validate.notNull(realmMap);
        Validate.notNull(coordinate);
        if (monsterSdb.contains(name)) {
            return createMonster(name, id, realmMap, coordinate);
        } else if (npcSdb.contains(name)) {
            return createNonMonsterNpc(name, id, realmMap, coordinate);
        }
        log.error("Name {} does not exist.", name);
        throw new NoSuchElementException(name);
    }
}
