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
    private final NpcSdb npcSdb;
    private final MagicParamSdb magicParamSdb;
    private final MerchantItemSdbRepository merchantItemSdbRepository;

    private final RealmSpecificSdbRepository realmSpecificSdbRepository;


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


    private NpcAnimation createAnimation(String name, NpcAnimationEnum type) {
        var animate = monsterSdb.getAnimate(name);
        int length = actionSdb.getActionLength(animate, type);
        if (type == NpcAnimationEnum.Idle) {
            length *= 2;
        }
        return new NpcAnimation(length, type);
    }


    private NpcAttackAbility createAttackAbility(String name) {
        return new NpcAttackAbility(monsterSdb.getAttackSpeed(name) * Realm.STEP_MILLIS + 1500,
                monsterSdb.getDamage(name),
                monsterSdb.getAccuracy(name) + 75,
                monsterSdb.getSoundAttack(name), createAnimation(name, NpcAnimationEnum.Attack));
    }

    private NpcTurnAbility createTurnAbility(String name) {
        return new NpcTurnAbility(createAnimation(name, NpcAnimationEnum.Turn));
    }

    private NpcHurtAbility createHurtAbility(String name) {
        return new NpcHurtAbility(monsterSdb.getArmor(name), monsterSdb.getAvoid(name) + 20, monsterSdb.getSoundStructed(name),
                monsterSdb.getLife(name), createAnimation(name, NpcAnimationEnum.Hurt),
                monsterSdb.getRecovery(name) * Realm.STEP_MILLIS + 700);
    }

    private NpcIdleAbility createIdleAbility(String name) {
        return new NpcIdleAbility(createAnimation(name, NpcAnimationEnum.Idle));
    }

    private NpcMoveAbility createMoveAbility(String name) {
        return new NpcMoveAbility(monsterSdb.getWalkSpeed(name) * Realm.STEP_MILLIS,
                createAnimation(name, NpcAnimationEnum.Move));
    }

    private NpcDieAbility createDieAbility(String name) {
        return new NpcDieAbility(createAnimation(name, NpcAnimationEnum.Die), monsterSdb.getSoundDie(name));
    }

    private List<NpcAbility> abilities(String name) {
        List<NpcAbility> abilities = new ArrayList<>();
        if (monsterSdb.attack(name)) {
            abilities.add(createAttackAbility(name));
        }
        abilities.add(createHurtAbility(name));
        abilities.add(createIdleAbility(name));
        if (monsterSdb.getWalkSpeed(name) > 0) {
            abilities.add(createMoveAbility(name));
            abilities.add(createTurnAbility(name));
        }
        abilities.add(createDieAbility(name));
        return abilities;
    }


    private NpcAI createAI(Npc npc) {
        return npc.findAbility(NpcMoveAbility.class).isPresent() ?
                new WanderingAI(npc, monsterSdb.getActionWidth(npc.getIdName())) :
                new FrozenAI(npc);
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
                idName,
                randomDirection());
        npc.changeAI(createAI(npc));
        return npc;
    }
}
