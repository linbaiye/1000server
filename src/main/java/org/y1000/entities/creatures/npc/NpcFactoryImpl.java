package org.y1000.entities.creatures.npc;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.monster.*;
import org.y1000.entities.creatures.npc.AI.*;
import org.y1000.entities.creatures.npc.interactability.BuyInteractability;
import org.y1000.entities.creatures.npc.interactability.NpcInteractability;
import org.y1000.entities.creatures.npc.interactability.NpcInteractor;
import org.y1000.entities.creatures.npc.interactability.SellInteractability;
import org.y1000.entities.creatures.npc.spell.CloneSpell;
import org.y1000.entities.creatures.npc.spell.NpcSpell;
import org.y1000.entities.creatures.npc.spell.NpcSpellType;
import org.y1000.entities.creatures.npc.spell.ShiftSpell;
import org.y1000.kungfu.KungFuSdb;
import org.y1000.kungfu.KungFuType;
import org.y1000.quest.Quest;
import org.y1000.realm.Realm;
import org.y1000.realm.RealmMap;
import org.y1000.sdb.ActionSdb;
import org.y1000.sdb.*;
import org.y1000.util.Coordinate;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
public final class NpcFactoryImpl implements NpcFactory {

    private final ActionSdb actionSdb;
    private final MonstersSdb monsterSdb;
    private final KungFuSdb kungFuSdb;
    private final NpcSdb npcSdb;
    private final MagicParamSdb magicParamSdb;
    private final MerchantItemSdbRepository merchantItemSdbRepository;

    private final RealmSpecificSdbRepository realmSpecificSdbRepository;

    private final Map<String, List<AnimationDescriptor>> animationDescriptors;

    public NpcFactoryImpl(ActionSdb actionSdb,
                          MonstersSdb monsterSdb,
                          KungFuSdb kungFuSdb,
                          NpcSdb npcSdb,
                          MagicParamSdb magicParamSdb,
                          MerchantItemSdbRepository merchantItemSdbRepository,
                          RealmSpecificSdbRepository realmSpecificSdbRepository
                          ) {
        this.actionSdb = actionSdb;
        this.monsterSdb = monsterSdb;
        this.kungFuSdb = kungFuSdb;
        this.npcSdb = npcSdb;
        this.magicParamSdb = magicParamSdb;
        this.merchantItemSdbRepository = merchantItemSdbRepository;
        this.realmSpecificSdbRepository = realmSpecificSdbRepository;
        animationDescriptors = buildAnimationDescriptors(monsterSdb.getAllAnimateIds(), npcSdb.getAllAnimateIds());
    }


    private Map<String, List<AnimationDescriptor>> buildAnimationDescriptors(Set<String> monsterAnimateIds, Set<String> npcAnimateIds) {
        return Collections.emptyMap();
//        monsterAnimateIds.forEach();
    }


    private Direction randomDirection() {
        var v = ThreadLocalRandom.current().nextInt(Direction.UP.value(), Direction.UP_LEFT.value() + 1);
        log.debug("Random direction {}", Direction.fromValue(v));
        return Direction.fromValue(v);
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


    private Map<NpcAnimationEnum, Integer> createDevirtueActionLengthMap(String animate) {
        Map<NpcAnimationEnum, Integer> result = new HashMap<>();
        int move = actionSdb.getActionLength(animate, NpcAnimationEnum.Move);
        int idle = actionSdb.getActionLength(animate, NpcAnimationEnum.Idle);
        int hurt = actionSdb.getActionLength(animate, NpcAnimationEnum.Hurt);
        int die = actionSdb.getActionLength(animate, NpcAnimationEnum.Die);
        int turn = actionSdb.getActionLength(animate, NpcAnimationEnum.Turn);
        result.put(NpcAnimationEnum.Idle, idle);
        result.put(NpcAnimationEnum.Move, move);
        result.put(NpcAnimationEnum.Hurt, hurt);
        result.put(NpcAnimationEnum.Die, die);
        result.put(NpcAnimationEnum.Turn, turn);
        return result;
    }

    private Map<NpcAnimationEnum, Integer> createSubmissiveNpcActionLengthMap(String idName) {
        return createDevirtueActionLengthMap(npcSdb.getAnimate(idName));
    }

    private Map<NpcAnimationEnum, Integer> createActionLengthMap(String animate) {
        Map<NpcAnimationEnum, Integer> result = createDevirtueActionLengthMap(animate);
        int attack = actionSdb.getActionLength(animate, NpcAnimationEnum.Attack);
        result.put(NpcAnimationEnum.Attack, attack);
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

    private AggressiveMonster createAggressiveCreature(String name, long id, RealmMap map, Coordinate coordinate, List<NpcSpell> spells, INpcAI ai) {
        return AggressiveMonster.builder()
                .id(id)
                .coordinate(coordinate)
                .direction(randomDirection())
                .name(monsterSdb.getViewName(name))
                .realmMap(map)
                .stateMillis(createActionLengthMap(monsterSdb.getAnimate(name)))
                .attributeProvider(new MonsterAttributeProvider(name, monsterSdb))
                .skill(createSkill(name))
                .ai(ai)
                .spells(spells)
                .build();
    }

    private INpc createSubmissiveMonster(String name, long id, RealmMap map, Coordinate coordinate, List<NpcSpell> spells) {

        if (name.equals("稻草人")) {
            return Scarecrow.builder()
                    .id(id)
                    .coordinate(coordinate)
                    .name(monsterSdb.getViewName(name))
                    .realmMap(map)
                    .stateMillis(createActionLengthMap(monsterSdb.getAnimate(name)))
                    .attributeProvider(new MonsterAttributeProvider(name, monsterSdb))
                    .build();
        }
        int actionWidth = monsterSdb.getActionWidth(name);
        INpcAI npcAI = actionWidth == 0 ? NpcFrozenAI.INSTANCE : new SubmissiveWanderingAI();
        return SubmissiveNpc.builder()
                .id(id)
                .coordinate(coordinate)
                .name(monsterSdb.getViewName(name))
                .realmMap(map)
                .stateMillis(createActionLengthMap(monsterSdb.getAnimate(name)))
                .attributeProvider(new MonsterAttributeProvider(name, monsterSdb))
                .ai(npcAI)
                .spells(spells)
                .build();
    }

    private INpc createSubmissiveNpc(String name, long id, RealmMap map, Coordinate coordinate, List<NpcSpell> spells) {
        int actionWidth = npcSdb.getActionWidth(name);
        if (name.equals("九尾狐酒母")) {
            return NineTailFoxHuman.builder()
                    .id(id)
                    .coordinate(coordinate)
                    .direction(randomDirection())
                    .name(npcSdb.getViewName(name))
                    .realmMap(map)
                    .stateMillis(createActionLengthMap(npcSdb.getAnimate(name)))
                    .attributeProvider(new NonMonsterNpcAttributeProvider(name, npcSdb))
                    .build();
        }
        var npcAI = actionWidth == 0 ? NpcFrozenAI.INSTANCE : new SubmissiveWanderingAI();
        return SubmissiveNpc.builder()
                .id(id)
                .coordinate(coordinate)
                .name(npcSdb.getViewName(name))
                .realmMap(map)
                .stateMillis(createActionLengthMap(npcSdb.getAnimate(name)))
                .attributeProvider(new NonMonsterNpcAttributeProvider(name, npcSdb))
                .ai(npcAI)
                .spells(spells)
                .build();
    }


    private INpc createPassiveCreature(String name, long id, RealmMap map, Coordinate coordinate, List<NpcSpell> spells, INpcAI ai) {
        boolean attack = monsterSdb.attack(name);
        if (attack) {
            return null;
//            return PassiveMonster.builder()
//                    .id(id)
//                    .coordinate(coordinate)
//                    .direction(randomDirection())
//                    .name(monsterSdb.getViewName(name))
//                    .realmMap(map)
//                    .stateMillis(createActionLengthMap(monsterSdb.getAnimate(name)))
//                    .attributeProvider(new MonsterAttributeProvider(name, monsterSdb))
//                    .ai(ai)
//                    .skill(createSkill(name))
//                    .spells(spells)
//                    .build();
        } else {
            return createSubmissiveMonster(name, id, map, coordinate, spells);
        }
    }



    private INpc createMonster(String name, long id, RealmMap realmMap, Coordinate coordinate, List<NpcSpell> spells, INpcAI ai) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(realmMap);
        Objects.requireNonNull(coordinate);
        String animate = monsterSdb.getAnimate(name);
        if (animate == null) {
            throw new NotImplementedException(name + " has no action sdb.");
        }
        boolean passive = monsterSdb.isPassive(name);
        return passive ? createPassiveCreature(name, id, realmMap, coordinate, spells, ai) : createAggressiveCreature(name, id, realmMap, coordinate, spells, ai);
    }


    private Quest getQuest(String idName) {
        QuestSdb questSdb = QuestSdb.forNpc(idName);
        List<String> names = questSdb.getNames();
        return Quest.parse(names.get(0), questSdb);
    }

    private NpcInteractor createNpcInteractor(String name, String merchantSdbFile) {
        List<NpcInteractability> abilities = new ArrayList<>();
        if (!StringUtils.isEmpty(merchantSdbFile)) {
            MerchantItemSdb merchantItemSdb = merchantItemSdbRepository.load(merchantSdbFile);
            if (!merchantItemSdb.buy().isEmpty())
                abilities.add(new BuyInteractability(merchantItemSdb.buy()));
            if (!merchantItemSdb.sell().isEmpty())
                abilities.add(new SellInteractability(merchantItemSdb.sell()));
        }
        if (npcSdb.isQuester(name)) {
            abilities.add(getQuest(name));
        }
        return abilities.isEmpty() ? null : new NpcInteractor("有什么可以帮助你的吗？", abilities);
    }


    private Optional<Chatter> loadChatter(String dialogSdb) {
        if (dialogSdb == null)
            return Optional.empty();
        var dialogs = realmSpecificSdbRepository.loadDialog(dialogSdb)
                .map(NpcDialogSdb::idleDialogs)
                .orElse(Collections.emptyList());
        if (dialogs.isEmpty())
            return Optional.empty();
        return Optional.of(new Chatter(dialogs));
    }

    private INpc createSubmissiveNpc(String name, long id, RealmMap realmMap,
                                     Coordinate coordinate,
                                     String merchantSdbFile,
                                     String dialogSdb) {
        if ("仓库管理员".equals(name)) {
            return null;
//            return Banker.builder()
//                    .id(id)
//                    .coordinate(coordinate)
//                    .direction(randomDirection())
//                    .name(npcSdb.getViewName(name))
//                    .realmMap(realmMap)
//                    .stateMillis(createActionLengthMap(npcSdb.getAnimate(name)))
//                    .attributeProvider(new NonMonsterNpcAttributeProvider(name, npcSdb))
//                    .ai(new SubmissiveWanderingAI())
//                    .build();
        }
        NpcInteractor npcInteractor = createNpcInteractor(name, merchantSdbFile);
        if (npcInteractor == null) {
            return createSubmissiveNpc(name, id, realmMap, coordinate, null);
        }
        return null;
//        return SubmissiveInteractableNpc.builder()
//                .id(id)
//                .realmMap(realmMap)
//                .interactor(npcInteractor)
//                .name(npcSdb.getViewName(name))
//                .coordinate(coordinate)
//                .stateMillis(createSubmissiveNpcActionLengthMap(name))
//                .attributeProvider(new NonMonsterNpcAttributeProvider(name, npcSdb))
//                .ai(new SubmissiveWanderingAI(loadChatter(dialogSdb).orElse(null)))
//                .build();
    }

    private INpc createViolentNpc(String name, long id, RealmMap realmMap,
                                  Coordinate coordinate,
                                  String merchantSdbFile) {
        NpcInteractor npcInteractor = createNpcInteractor(name, merchantSdbFile);
        String animate = npcSdb.getAnimate(name);
        if (npcInteractor == null) {
            return Guardian.builder()
                    .id(id)
                    .coordinate(coordinate)
                    .direction(randomDirection())
                    .name(npcSdb.getViewName(name))
                    .width(npcSdb.getActionWidth(name))
                    .realmMap(realmMap)
                    .stateMillis(createActionLengthMap(animate))
                    .ai(new ViolentNpcWanderingAI(coordinate))
                    .attributeProvider(new NonMonsterNpcAttributeProvider(name, npcSdb))
                    .build();
        }
        return null;
//        return ViolentInteractableNpc.builder()
//                .id(id)
//                .realmMap(realmMap)
//                .interactor(npcInteractor)
//                .name(npcSdb.getViewName(name))
//                .coordinate(coordinate)
//                .stateMillis(createActionLengthMap(animate))
//                .attributeProvider(new NonMonsterNpcAttributeProvider(name, npcSdb))
//                .ai(new ViolentNpcWanderingAI(coordinate))
//                .build();
    }


    @Override
    public INpc createNpc(String name, long id, RealmMap realmMap, Coordinate coordinate) {
        Validate.notNull(name);
        Validate.notNull(realmMap);
        Validate.notNull(coordinate);
        if (monsterSdb.contains(name)) {
            return createMonster(name, id, realmMap, coordinate, loadSpells(name), new MonsterWanderingAI(coordinate));
        }
        log.error("Name {} does not exist.", name);
        throw new NoSuchElementException(name);
    }

    @Override
    public INpc createClonedNpc(INpc npc, long id, Coordinate coordinate) {
        Validate.notNull(npc);
        Validate.notNull(coordinate);
        if (monsterSdb.contains(npc.idName())) {
            return createMonster(npc.idName(), id, npc.realmMap(), coordinate, null, new MonsterWanderingAI(coordinate));
        }
        log.error("Name {} does not exist.", npc.idName());
        throw new NoSuchElementException(npc.idName());
    }



    @Override
    public INpc createNonMonsterNpc(String name, long id, RealmMap realmMap, Coordinate coordinate, CreateNonMonsterSdb createNpcSdb) {
        Validate.notNull(name);
        Validate.notNull(realmMap);
        Validate.notNull(coordinate);
        Validate.notNull(createNpcSdb);
        var merchantFile = createNpcSdb.getMerchant(name).orElse(npcSdb.getNpcText(name));
        if (!npcSdb.isProtector(name))
            return createSubmissiveNpc(name, id, realmMap, coordinate, merchantFile, createNpcSdb.getDialog(name).orElse(null));
        return createViolentNpc(name, id, realmMap, coordinate, merchantFile);
    }


    private NpcAnimation createAnimationTimer(String name, NpcAnimationEnum type) {
        var animate = monsterSdb.getAnimate(name);
        int length = actionSdb.getActionLength(animate, type);
        if (type == NpcAnimationEnum.Idle) {
            length *= 2;
        }
        return new NpcAnimation(length, type);
    }


    private NpcAttackAbility createAttackAbility(String name) {
        return new NpcAttackAbility(monsterSdb.getAttackSpeed(name) * Realm.STEP_MILLIS,
                monsterSdb.getRecovery(name) * Realm.STEP_MILLIS, monsterSdb.getDamage(name),
                monsterSdb.getAccuracy(name),
                monsterSdb.getSoundAttack(name), createAnimationTimer(name, NpcAnimationEnum.Attack));
    }

    private NpcTurnAbility createTurnAbility(String name) {
        return new NpcTurnAbility(createAnimationTimer(name, NpcAnimationEnum.Turn));
    }

    private NpcHurtAbility createHurtAbility(String name) {
        return new NpcHurtAbility(monsterSdb.getArmor(name), monsterSdb.getAvoid(name), monsterSdb.getSoundStructed(name),
                monsterSdb.getLife(name), createAnimationTimer(name, NpcAnimationEnum.Hurt));
    }

    private NpcIdleAbility createIdleAbility(String name) {
        return new NpcIdleAbility(createAnimationTimer(name, NpcAnimationEnum.Idle));
    }

    private NpcMoveAbility createMoveAbility(String name) {
        return new NpcMoveAbility(monsterSdb.getWalkSpeed(name) * Realm.STEP_MILLIS,
                createAnimationTimer(name, NpcAnimationEnum.Move));
    }

    private List<NpcAbility> abilities(String name) {
        List<NpcAbility> abilities = new ArrayList<>();
        if (monsterSdb.attack(name)) {
            abilities.add(createAttackAbility(name));
        }
        abilities.add(createHurtAbility(name));
        abilities.add(createTurnAbility(name));
        abilities.add(createIdleAbility(name));
        abilities.add(createMoveAbility(name));
        return abilities;
    }


    @Override
    public Npc create(long id,
                      String idName,
                      RealmMap realmMap,
                      Coordinate coordinate,
                      NpcEventListener listener) {
        Npc npc = new Npc(id,
                abilities(idName),
                monsterSdb.getViewName(idName),
                coordinate,
                listener, realmMap,
                monsterSdb.getAnimate(idName),
                monsterSdb.getShape(idName),
                idName);
        WanderingAI wanderingAI = new WanderingAI(npc, monsterSdb.getActionWidth(idName));
        npc.findAbility(NpcHurtAbility.class).ifPresent(a -> a.setHurtTrigger(wanderingAI::onAttacked));
        npc.changeAI(wanderingAI);
        return npc;
    }
}
