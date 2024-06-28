package org.y1000.entities.creatures.monster;


import org.y1000.entities.Direction;
import org.y1000.entities.attribute.AttributeProvider;
import org.y1000.entities.attribute.Damage;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.*;
import org.y1000.entities.creatures.npc.AbstractViolentNpc;
import org.y1000.entities.creatures.npc.NpcAI;
import org.y1000.entities.creatures.npc.ViolentNpc;
import org.y1000.entities.creatures.npc.ViolentNpcWanderingAI;
import org.y1000.entities.players.Player;
import org.y1000.entities.projectile.Projectile;
import org.y1000.event.EntityEvent;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;
import org.y1000.util.UnaryAction;

import java.util.Map;

public abstract class AbstractMonster extends AbstractViolentNpc implements Monster {
    public AbstractMonster(long id, Coordinate coordinate, Direction direction, String name, Map<State, Integer> stateMillis, AttributeProvider attributeProvider, RealmMap realmMap, NpcAI<ViolentNpc> ai) {
        super(id, coordinate, direction, name, stateMillis, attributeProvider, realmMap, ai);
    }
//    private MonsterAI ai;
//
//    protected AbstractMonster(long id, Coordinate coordinate, Direction direction, String name,
//                              RealmMap realmMap, Map<State, Integer> stateMillis,
//                              AttributeProvider attributeProvider, MonsterAttackSkill spell) {
//        super(id, coordinate, direction, name, stateMillis, attributeProvider, realmMap, new ViolentNpcWanderingAI());
//        changeAI(new MonsterWanderingAI(wanderingArea().random(spawnCoordinate()), coordinate));
//    }
//
//    @Override
//    public void revive(Coordinate coordinate) {
//        doRevive(coordinate);
//        changeAI(new MonsterWanderingAI(wanderingArea().random(spawnCoordinate()), coordinate));
//    }
//
//    public MonsterAI AI() {
//        return ai;
//    }
//
//    public void changeAI(MonsterAI ai) {
//        this.ai = ai;
//        this.ai.start(this);
//    }
//
//    public MonsterAttackSkill attackSkill() {
//        return attackSkill;
//    }
//
//
//    @Override
//    public void update(int delta) {
//        cooldown(delta);
//        ai.update(this, delta);
//        state().update(this, delta);
//    }
//
//
//
//
//    @Override
//    public boolean attackedBy(Player attacker) {
//        return attackedByPlayer(attacker, attacker.damage(), attacker.hit(), attacker::gainAttackExp);
//    }
//
//    @Override
//    public void attackedBy(Projectile projectile) {
//        if (!(projectile.shooter() instanceof Player player)) {
//            return;
//        }
//        attackedByPlayer(player, projectile.damage(), projectile.hit(), player::gainRangedAttackExp);
//    }


//    @Override
//    public void onEvent(EntityEvent entityEvent) {
//        if (getFightingEntity() == null) {
//            log().error("Invalid event received.");
//            return;
//        }
//        if (!canPurchaseOrAttack(getFightingEntity())) {
//            clearFightingEntity();
//        }
//    }

}
