package org.y1000.entities.creatures.npc;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.monster.*;
import org.y1000.entities.creatures.npc.spell.CloneSpell;
import org.y1000.entities.creatures.npc.spell.NpcSpell;
import org.y1000.entities.creatures.npc.spell.NpcSpellType;
import org.y1000.entities.creatures.npc.spell.ShiftSpell;
import org.y1000.kungfu.KungFuSdb;
import org.y1000.kungfu.KungFuType;
import org.y1000.realm.RealmMap;
import org.y1000.sdb.ActionSdb;
import org.y1000.sdb.*;
import org.y1000.util.Coordinate;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public final class NpcFactoryImpl implements NpcFactory {

    private final ActionSdb actionSdb;
    private final MonstersSdb monsterSdb;
    private final KungFuSdb kungFuSdb;
    private final NpcSdb npcSdb;
    private final MagicParamSdb magicParamSdb;
    private final MerchantItemSdbRepository merchantItemSdbRepository;

    public NpcFactoryImpl(ActionSdb actionSdb,
                          MonstersSdb monsterSdb,
                          KungFuSdb kungFuSdb,
                          NpcSdb npcSdb,
                          MagicParamSdb magicParamSdb,
                          MerchantItemSdbRepository merchantItemSdbRepository) {
        this.actionSdb = actionSdb;
        this.monsterSdb = monsterSdb;
        this.kungFuSdb = kungFuSdb;
        this.npcSdb = npcSdb;
        this.magicParamSdb = magicParamSdb;
        this.merchantItemSdbRepository = merchantItemSdbRepository;
    }


    private NpcSpell createSpell(String npcName, String magicName) {
        KungFuType magicType = kungFuSdb.getMagicType(magicName);
        if (magicType != KungFuType.NPC_SPELL) {
            log.error("{} is not a npc spell.", magicName);
            return null;
        }
        int function = kungFuSdb.getFunction(magicName);
        if (!NpcSpellType.contains(function)) {
            return null;
        }
        return switch (NpcSpellType.fromValue(function)) {
            case HIDE -> null;
            case CLONE -> new CloneSpell(magicParamSdb.getNumberParam1(npcName, magicName), magicParamSdb.getNumberParam2(npcName, magicName));
            case HEAL -> null;
            case SHIFT -> new ShiftSpell(magicParamSdb.getNameParam1(npcName, magicName));
        };
    }

    private List<NpcSpell> loadSpells(String name) {
        String haveMagic = monsterSdb.getHaveMagic(name);
        if (StringUtils.isEmpty(haveMagic)) {
            return Collections.emptyList();
        }
        String[] magics = haveMagic.split(":");
        return Arrays.stream(magics).map(m -> createSpell(name, m))
                .filter(Objects::nonNull).collect(Collectors.toList());
    }


    private Map<State, Integer> createDevirtueActionLengthMap(String animate) {
        Map<State, Integer> result = new HashMap<>();
        int move = actionSdb.getActionLength(animate, State.WALK);
        int idle = actionSdb.getActionLength(animate, State.IDLE);
        int hurt = actionSdb.getActionLength(animate, State.HURT);
        int die = actionSdb.getActionLength(animate, State.DIE);
        result.put(State.IDLE, idle);
        result.put(State.WALK, move);
        result.put(State.HURT, hurt);
        result.put(State.DIE, die);
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

    private AggressiveMonster createAggressiveCreature(String name, long id, RealmMap map, Coordinate coordinate, List<NpcSpell> spells, NpcAI ai) {
        return AggressiveMonster.builder()
                .id(id)
                .coordinate(coordinate)
                .direction(Direction.DOWN)
                .name(monsterSdb.getViewName(name))
                .realmMap(map)
                .stateMillis(createActionLengthMap(monsterSdb.getAnimate(name)))
                .attributeProvider(new MonsterAttributeProvider(name, monsterSdb))
                .skill(createSkill(name))
                .ai(ai)
                .spells(spells)
                .build();
    }

    private Npc createSubmissiveMonster(String name, long id, RealmMap map, Coordinate coordinate, List<NpcSpell> spells) {

        int actionWidth = monsterSdb.getActionWidth(name);
        var npcAI = actionWidth == 0 ? NpcFrozenAI.INSTANCE : new SubmissiveWanderingAI();
        return SubmissiveNpc.builder()
                .id(id)
                .coordinate(coordinate)
                .direction(Direction.DOWN)
                .name(monsterSdb.getViewName(name))
                .realmMap(map)
                .stateMillis(createActionLengthMap(monsterSdb.getAnimate(name)))
                .attributeProvider(new MonsterAttributeProvider(name, monsterSdb))
                .ai(npcAI)
                .spells(spells)
                .build();
    }

    private Npc createSubmissiveNpc(String name, long id, RealmMap map, Coordinate coordinate, List<NpcSpell> spells) {
        int actionWidth = npcSdb.getActionWidth(name);
        if (name.equals("九尾狐酒母")) {
            return NineTailFoxHuman.builder()
                    .id(id)
                    .coordinate(coordinate)
                    .direction(Direction.DOWN)
                    .name(npcSdb.getViewName(name))
                    .realmMap(map)
                    .stateMillis(createActionLengthMap(npcSdb.getAnimate(name)))
                    .attributeProvider(new NonMonsterNpcAttributeProvider(name, npcSdb))
                    .ai(new SubmissiveWanderingAI())
                    .build();
        }
        var npcAI = actionWidth == 0 ? NpcFrozenAI.INSTANCE : new SubmissiveWanderingAI();
        return SubmissiveNpc.builder()
                .id(id)
                .coordinate(coordinate)
                .direction(Direction.DOWN)
                .name(npcSdb.getViewName(name))
                .realmMap(map)
                .stateMillis(createActionLengthMap(npcSdb.getAnimate(name)))
                .attributeProvider(new NonMonsterNpcAttributeProvider(name, npcSdb))
                .ai(npcAI)
                .spells(spells)
                .build();
    }


    private Npc createPassiveCreature(String name, long id, RealmMap map, Coordinate coordinate, List<NpcSpell> spells, NpcAI ai) {
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
                    .ai(ai)
                    .skill(createSkill(name))
                    .spells(spells)
                    .build();
        } else {
            return createSubmissiveMonster(name, id, map, coordinate, spells);
        }
    }



    private Npc createMonster(String name, long id, RealmMap realmMap, Coordinate coordinate, List<NpcSpell> spells, NpcAI ai) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(realmMap);
        Objects.requireNonNull(coordinate);
        String animate = monsterSdb.getAnimate(name);
        if (animate == null) {
            throw new NotImplementedException(name + " has no action sdb.");
        }
        //boolean passive = true;
        boolean passive = monsterSdb.isPassive(name);
        return passive ? createPassiveCreature(name, id, realmMap, coordinate, spells, ai) : createAggressiveCreature(name, id, realmMap, coordinate, spells, ai);
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
        if (npcSdb.isSeller(name) && !StringUtils.isEmpty(npcSdb.getNpcText(name))) {
            return createMerchant(name, id, realmMap, coordinate);
        } else if (npcSdb.isProtector(name)){
            return createGuardian(name, id, realmMap, coordinate);
        } else {
            return createSubmissiveNpc(name, id, realmMap, coordinate, Collections.emptyList());
        }
    }

    @Override
    public Npc createNpc(String name, long id, RealmMap realmMap, Coordinate coordinate) {
        Validate.notNull(name);
        Validate.notNull(realmMap);
        Validate.notNull(coordinate);

        if (monsterSdb.contains(name)) {
            return createMonster(name, id, realmMap, coordinate, loadSpells(name), new MonsterWanderingAI());
        } else if (npcSdb.contains(name)) {
            return createNonMonsterNpc(name, id, realmMap, coordinate);
        }
        log.error("Name {} does not exist.", name);
        throw new NoSuchElementException(name);
    }

    @Override
    public Npc createClonedNpc(Npc npc, long id, Coordinate coordinate) {
        Validate.notNull(npc);
        if (monsterSdb.contains(npc.idName())) {
            return createMonster(npc.idName(), id, npc.realmMap(), coordinate, null, new MonsterWanderingAI());
        }
        log.error("Name {} does not exist.", npc.idName());
        throw new NoSuchElementException(npc.idName());
    }
}
