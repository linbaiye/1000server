package org.y1000.entities.creatures.npc;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.npc.spell.*;
import org.y1000.item.ItemFactory;
import org.y1000.item.ItemSdb;
import org.y1000.kungfu.KungFuSdb;
import org.y1000.kungfu.KungFuType;
import org.y1000.realm.Realm;
import org.y1000.realm.RealmMap;
import org.y1000.sdb.ActionSdb;
import org.y1000.sdb.*;
import org.y1000.util.Coordinate;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public final class NpcFactoryImpl implements NpcFactory {

    private final ActionSdb actionSdb;
    private final MonstersSdb monsterSdb;
    private final KungFuSdb kungFuSdb;
    private final NonMonsterNpcSdb nonMonsterNpcSdb;
    private final MagicParamSdb magicParamSdb;

    private final ItemSdb itemSdb;

    private static final int DEFAULT_WALK_MILLIS = 2000;

    private final ItemFactory itemFactory;

    private static final int DEFAULT_NPC_VIEW_WIDTH = 8;

    private static final NpcRespawnAbility RESPAWN_ABILITY = new NpcRespawnAbility(8000);

    public NpcFactoryImpl(ActionSdb actionSdb,
                          MonstersSdb monsterSdb,
                          KungFuSdb kungFuSdb,
                          NonMonsterNpcSdb nonMonsterNpcSdb,
                          MagicParamSdb magicParamSdb,
                          ItemSdb itemSdb,
                          ItemFactory itemFactory) {
        this.actionSdb = actionSdb;
        this.monsterSdb = monsterSdb;
        this.kungFuSdb = kungFuSdb;
        this.nonMonsterNpcSdb = nonMonsterNpcSdb;
        this.magicParamSdb = magicParamSdb;
        this.itemSdb = itemSdb;
        this.itemFactory = itemFactory;
    }

    private Direction randomDirection() {
        var v = ThreadLocalRandom.current().nextInt(Direction.UP.value(), Direction.UP_LEFT.value() + 1);
        return Direction.fromValue(v);
    }

    private Object createMagic(String npcName, String magicName) {
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
            case Copy -> new NpcCopyAbility(magicParamSdb.getNumberParam1(npcName, magicName), magicParamSdb.getNumberParam2(npcName, magicName));
            case SHIFT -> new ShiftSpell(magicParamSdb.getNameParam1(npcName, magicName));
            case HEAL -> null;
            case HIDE -> null;
        };
    }


    private NpcAnimation createAnimation(String animate, NpcAction type) {
        int length = actionSdb.getActionLength(animate, type);
        if (type == NpcAction.Idle) {
            length *= 2;
        }
        return new NpcAnimation(length, type);
    }


    private NpcMeleeAbility createMeleeAbility(String name, NpcSdb npcSdb) {
        return new NpcMeleeAbility(npcSdb.getDamage(name),
                npcSdb.getAccuracy(name) + 75,
                npcSdb.getSoundAttack(name),
                createAnimation(npcSdb.getAnimate(name), NpcAction.Attack),
                npcSdb.getAttackSpeed(name) * Realm.STEP_MILLIS + 1500);
    }

    private NpcTurnAbility createTurnAbility(String name, NpcSdb npcSdb) {
        return new NpcTurnAbility(createAnimation(npcSdb.getAnimate(name), NpcAction.Turn));
    }

    private NpcHurtAbility createHurtAbility(String name, NpcSdb npcSdb) {
        return new NpcHurtAbility(npcSdb.getArmor(name), npcSdb.getAvoid(name) + 20, npcSdb.getSoundStructed(name),
                npcSdb.getLife(name), createAnimation(npcSdb.getAnimate(name), NpcAction.Hurt),
                npcSdb.getRecovery(name) * Realm.STEP_MILLIS + 700);
    }

    private NpcIdleAbility createIdleAbility(String name, NpcSdb npcSdb) {
        return new NpcIdleAbility(createAnimation(npcSdb.getAnimate(name), NpcAction.Idle));
    }

    private NpcMoveAbility createMoveAbility(String name, NpcSdb npcSdb, int walkSpeed) {
        return new NpcMoveAbility(walkSpeed, createAnimation(npcSdb.getAnimate(name), NpcAction.Move));
    }

    private NpcDieAbility createDieAbility(String name, NpcSdb npcSdb) {
        return new NpcDieAbility(createAnimation(npcSdb.getAnimate(name), NpcAction.Die), npcSdb.getSoundDie(name));
    }

    private static final int INIT_SKILL_DIV_DAMAGE = 5000;

    private NpcShootAbility createShootAbility(String attackMagic, String animate, int hit) {
        String[] split = attackMagic.split(":");
        String name = split[0];
        int level = Integer.parseInt(split[1]);
        kungFuSdb.getAttackSpeed(name);
        int damageBody = kungFuSdb.getDamageBody(name);
        damageBody = damageBody + (damageBody * level) / INIT_SKILL_DIV_DAMAGE;
        return new NpcShootAbility(kungFuSdb.getBowImage(name), kungFuSdb.getSoundSwing(name),
                kungFuSdb.getAttackSpeed(name) * Realm.STEP_MILLIS + 1500, createAnimation(animate, NpcAction.Attack),
                damageBody, hit);
    }

    private List<Object> buildCommonAbilities(String idName, NpcSdb npcSdb, boolean hasRespawn) {
        List<Object> abilities = new ArrayList<>();
        NpcHurtAbility hurtAbility = createHurtAbility(idName, npcSdb);
        abilities.add(hurtAbility);
        abilities.add(createIdleAbility(idName, npcSdb));
        abilities.add(createDieAbility(idName, npcSdb));
        String attackMagic = npcSdb.getAttackMagic(idName);
        if (StringUtils.isNotEmpty(attackMagic))
            abilities.add(createShootAbility(attackMagic, npcSdb.getAnimate(idName), npcSdb.getAccuracy(idName) + 70));
        NpcDropItemAbility.parse(npcSdb.getHaveItem(idName)).ifPresent(abilities::add);
        if (hasRespawn) {
            int regenInterval = npcSdb.getRegenInterval(idName) * Realm.STEP_MILLIS;
            abilities.add(regenInterval > 0 ? new NpcRespawnAbility(regenInterval) : RESPAWN_ABILITY);
        }
        return abilities;
    }

    private List<Object> buildNpcInteractAbilities(NpcSettingSdb npcSettingSdb, String sprite, long id) {
        List<Object> abilities = new ArrayList<>();
        abilities.add(NpcInteractAbility.build(npcSettingSdb, sprite, id));
        if (!npcSettingSdb.getSellItems().isEmpty())
            abilities.add(NpcSellAbility.build(id, npcSettingSdb, itemSdb, sprite, itemFactory));
        if (!npcSettingSdb.getBuyItems().isEmpty())
            abilities.add(NpcBuyAbility.build(id, npcSettingSdb, itemSdb, sprite, itemFactory));
        return abilities;
    }


    private List<Object> buildNpcAbilities(long id, String idName) {
        List<Object> abilities = buildCommonAbilities(idName, nonMonsterNpcSdb, true);
        if (nonMonsterNpcSdb.isProtector(idName)) {
            abilities.add(new NpcProtectAbility(DEFAULT_NPC_VIEW_WIDTH));
            abilities.add(createMeleeAbility(idName, nonMonsterNpcSdb));
        }
        abilities.add(createMoveAbility(idName, nonMonsterNpcSdb, DEFAULT_WALK_MILLIS));
        abilities.add(createTurnAbility(idName, nonMonsterNpcSdb));
        NpcSettingSdb.tryLoad(idName).ifPresent(sdb -> abilities.addAll(buildNpcInteractAbilities(sdb, nonMonsterNpcSdb.getShape(idName), id)));
        return abilities;
    }

    private List<Object> buildCalledNpcAbilities(String idName) {
        List<Object> abilities = buildCommonAbilities(idName, nonMonsterNpcSdb, false);
        if (nonMonsterNpcSdb.isProtector(idName)) {
            abilities.add(createMeleeAbility(idName, nonMonsterNpcSdb));
        }
        abilities.add(createMoveAbility(idName, nonMonsterNpcSdb, DEFAULT_WALK_MILLIS / 4));
        abilities.add(createTurnAbility(idName, nonMonsterNpcSdb));
        abilities.add(new EngageAlivePlayerAbility(DEFAULT_NPC_VIEW_WIDTH));
        return abilities;
    }

    private List<Object> buildMonsterAbilities(String idName) {
        List<Object> abilities = buildCommonAbilities(idName, monsterSdb, true);
        if (monsterSdb.attack(idName)) {
            abilities.add(createMeleeAbility(idName, monsterSdb));
        }
        int walkSpeed = monsterSdb.getWalkSpeed(idName);
        if (walkSpeed > 0) {
            abilities.add(createMoveAbility(idName, monsterSdb, walkSpeed * Realm.STEP_MILLIS));
            abilities.add(createTurnAbility(idName, monsterSdb));
        }
        int escapeLife = monsterSdb.getEscapeLife(idName);
        if (escapeLife > 0)
            abilities.add(new LifeLowEscapeAbility(escapeLife, monsterSdb.getViewWidth(idName)));
        String soundNormal = monsterSdb.getSoundNormal(idName);
        if (StringUtils.isNotEmpty(soundNormal))
            abilities.add(new NpcSoundAbility(soundNormal));
        if (!monsterSdb.isPassive(idName))
            abilities.add(new EngageAlivePlayerAbility(monsterSdb.getViewWidth(idName)));
        var magic = monsterSdb.getHaveMagic(idName);
        if (StringUtils.isNotEmpty(magic)) {
            abilities.add(createMagic(idName, magic));
        }
        return abilities;
    }

    private NpcAI createAI(NpcImpl npc) {
        return npc.findAbility(NpcMoveAbility.class).isPresent() ?
                new WanderingAI(npc) : new FrozenAI(npc);
    }

    private NpcImpl create(long id,
                          String idName,
                          RealmMap realmMap,
                          Coordinate coordinate,
                          NpcEventListener listener,
                          List<Object> abilities) {
        var npcSdb = monsterSdb.containsName(idName) ? monsterSdb : nonMonsterNpcSdb;
        NpcImpl npc = new NpcImpl(id,
                abilities,
                npcSdb.getViewName(idName),
                coordinate,
                listener, realmMap,
                npcSdb.getAnimate(idName),
                npcSdb.getShape(idName),
                idName,
                randomDirection(),
                npcSdb.getActionWidth(idName));
        npc.changeAI(createAI(npc));
        return npc;
    }

    @Override
    public NpcImpl create(long id,
                          String idName,
                          RealmMap realmMap,
                          Coordinate coordinate,
                          NpcEventListener listener) {
        List<Object> abilities = monsterSdb.containsName(idName) ?
                buildMonsterAbilities(idName) : buildNpcAbilities(id, idName);
        return create(id, idName, realmMap, coordinate, listener, abilities);
    }

    @Override
    public NpcImpl createCalledNpc(long id, String idName, RealmMap realmMap, Coordinate coordinate, NpcEventListener listener) {
        List<Object> abilities = buildCalledNpcAbilities(idName);
        return create(id, idName, realmMap, coordinate, listener, abilities);
    }

    @Override
    public NpcImpl createCopied(long id, String idName, RealmMap realmMap, Coordinate coordinate, NpcEventListener listener) {
        List<Object> abilities = buildMonsterAbilities(idName);
        abilities.removeIf(a -> a instanceof NpcCopyAbility);
        abilities.removeIf(a -> a instanceof NpcRespawnAbility);
        abilities.removeIf(a -> a instanceof NpcDropItemAbility);
        return create(id, idName, realmMap, coordinate, listener, abilities);
    }
}
