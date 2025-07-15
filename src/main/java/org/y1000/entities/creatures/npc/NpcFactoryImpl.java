package org.y1000.entities.creatures.npc;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.monster.*;
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

@Slf4j
public final class NpcFactoryImpl implements NpcFactory {

    private final ActionSdb actionSdb;
    private final MonstersSdb monsterSdb;
    private final KungFuSdb kungFuSdb;
    private final NonMonsterNpcSdb nonMonsterNpcSdb;
    private final MagicParamSdb magicParamSdb;


    public NpcFactoryImpl(ActionSdb actionSdb,
                          MonstersSdb monsterSdb,
                          KungFuSdb kungFuSdb,
                          NonMonsterNpcSdb nonMonsterNpcSdb,
                          MagicParamSdb magicParamSdb
                          ) {
        this.actionSdb = actionSdb;
        this.monsterSdb = monsterSdb;
        this.kungFuSdb = kungFuSdb;
        this.nonMonsterNpcSdb = nonMonsterNpcSdb;
        this.magicParamSdb = magicParamSdb;
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



    private Quest getQuest(String idName) {
        QuestSdb questSdb = QuestSdb.forNpc(idName);
        List<String> names = questSdb.getNames();
        return Quest.parse(names.get(0), questSdb);
    }


    private NpcAnimation createAnimation(String animate, NpcAnimationEnum type) {
        int length = actionSdb.getActionLength(animate, type);
        if (type == NpcAnimationEnum.Idle) {
            length *= 2;
        }
        return new NpcAnimation(length, type);
    }


    private NpcMeleeAbility createMeleeAbility(String name, NpcSdb npcSdb) {
        return new NpcMeleeAbility(npcSdb.getDamage(name),
                npcSdb.getAccuracy(name) + 75,
                npcSdb.getSoundAttack(name),
                createAnimation(npcSdb.getAnimate(name), NpcAnimationEnum.Attack),
                npcSdb.getAttackSpeed(name) * Realm.STEP_MILLIS + 1500);
    }

    private NpcTurnAbility createTurnAbility(String name, NpcSdb npcSdb) {
        return new NpcTurnAbility(createAnimation(npcSdb.getAnimate(name), NpcAnimationEnum.Turn));
    }

    private NpcHurtAbility createHurtAbility(String name, NpcSdb npcSdb) {
        return new NpcHurtAbility(npcSdb.getArmor(name), npcSdb.getAvoid(name) + 20, npcSdb.getSoundStructed(name),
                npcSdb.getLife(name), createAnimation(npcSdb.getAnimate(name), NpcAnimationEnum.Hurt),
                npcSdb.getRecovery(name) * Realm.STEP_MILLIS + 700);
    }

    private NpcIdleAbility createIdleAbility(String name, NpcSdb npcSdb) {
        return new NpcIdleAbility(createAnimation(npcSdb.getAnimate(name), NpcAnimationEnum.Idle));
    }

    private NpcMoveAbility createMoveAbility(String name, NpcSdb npcSdb, int walkSpeed) {
        return new NpcMoveAbility(walkSpeed, createAnimation(npcSdb.getAnimate(name), NpcAnimationEnum.Move));
    }

    private NpcDieAbility createDieAbility(String name, NpcSdb npcSdb) {
        return new NpcDieAbility(createAnimation(npcSdb.getAnimate(name), NpcAnimationEnum.Die), npcSdb.getSoundDie(name));
    }

    private int getWalkSpeed(String name) {
        if (monsterSdb.containsName(name))
            return monsterSdb.getWalkSpeed(name) * Realm.STEP_MILLIS;
        String animate = nonMonsterNpcSdb.getAnimate(name);
        return actionSdb.getActionLength(animate, NpcAnimationEnum.Move);
    }

    private static final int INIT_SKILL_DIV_DAMAGE = 5000;

    private NpcShootAbility createShootAbility(String attackMagic, String animate) {
        String[] split = attackMagic.split(":");
        String name = split[0];
        int level = Integer.parseInt(split[1]);
        kungFuSdb.getAttackSpeed(name);
        int damageBody = kungFuSdb.getDamageBody(name);
        damageBody = damageBody + (damageBody * level) / INIT_SKILL_DIV_DAMAGE;
        return new NpcShootAbility(kungFuSdb.getBowImage(name), kungFuSdb.getSoundSwing(name),
                kungFuSdb.getAttackSpeed(name), createAnimation(animate, NpcAnimationEnum.Attack),
                damageBody, kungFuSdb.getAccuracy(name));
    }

    private List<Object> abilities(String idName, NpcSdb npcSdb) {
        List<Object> abilities = new ArrayList<>();
        if (npcSdb.attack(idName)) {
            abilities.add(createMeleeAbility(idName, npcSdb));
        }
        NpcHurtAbility hurtAbility = createHurtAbility(idName, npcSdb);
        abilities.add(hurtAbility);
        abilities.add(createIdleAbility(idName, npcSdb));
        int walkSpeed = getWalkSpeed(idName);
        if (walkSpeed > 0) {
            abilities.add(createMoveAbility(idName, npcSdb, walkSpeed));
            abilities.add(createTurnAbility(idName, npcSdb));
        }
        abilities.add(createDieAbility(idName, npcSdb));
        int escapeLife = npcSdb.getEscapeLife(idName);
        if (escapeLife > 0)
            abilities.add(new LifeLowEscapeAbility(escapeLife));

        if (npcSdb.getAttackMagic(idName) != null)
            abilities.add(createShootAbility(npcSdb.getAttackMagic(idName), npcSdb.getAnimate(idName)));
        return abilities;
    }

    private NpcAI createAI(NpcImpl npc) {
        return npc.findAbility(NpcMoveAbility.class).isPresent() ?
                new WanderingAI(npc) : new FrozenAI(npc);
    }

    @Override
    public NpcImpl create(long id,
                          String idName,
                          RealmMap realmMap,
                          Coordinate coordinate,
                          NpcEventListener listener) {
        var npcSdb = monsterSdb.containsName(idName) ? monsterSdb : nonMonsterNpcSdb;
        var sound = monsterSdb.containsName(idName) ? monsterSdb.getSoundNormal(idName) : null;
        var viewRange = monsterSdb.containsName(idName) ? monsterSdb.getViewWidth(idName) : 0;
        NpcImpl npc = new NpcImpl(id,
                abilities(idName, npcSdb),
                npcSdb.getViewName(idName),
                coordinate,
                listener, realmMap,
                npcSdb.getAnimate(idName),
                npcSdb.getShape(idName),
                idName,
                randomDirection(),
                npcSdb.getActionWidth(idName),
                sound, viewRange);
        npc.changeAI(createAI(npc));
        return npc;
    }
}
