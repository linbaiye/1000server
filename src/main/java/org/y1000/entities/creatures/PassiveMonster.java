package org.y1000.entities.creatures;


import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.Direction;
import org.y1000.entities.attribute.ArmorAttribute;
import org.y1000.entities.attribute.Damage;
import org.y1000.entities.creatures.event.ChangeStateEvent;
import org.y1000.entities.creatures.event.CreatureAttackEvent;
import org.y1000.message.AbstractInterpolation;
import org.y1000.message.CreatureInterpolation;
import org.y1000.message.MoveEvent;
import org.y1000.message.SetPositionEvent;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;
import org.y1000.util.Rectangle;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@Slf4j
public final class PassiveMonster extends AbstractViolentCreature<PassiveMonster> {

    private final RealmMap realmMap;

    private final ArmorAttribute armorAttribute;

    private final Damage damage;

    private final Rectangle wanderingArea;

    private static final Map<State, Integer> BAFFULO_STATE_MILLIS = new HashMap<>() {
        {
            put(State.IDLE, 1000);
            put(State.WALK, 770);
            put(State.HURT, 540);
            put(State.ATTACK, 700);
            put(State.DIE, 700);
        }
    };

    public PassiveMonster(long id, Coordinate coordinate, Direction direction, String name,
                          RealmMap realmMap) {
        super(id, coordinate, direction, name, BAFFULO_STATE_MILLIS);
        this.realmMap = realmMap;
        armorAttribute = ArmorAttribute.DEFAULT;
        damage = Damage.DEFAULT;
        this.wanderingArea = new Rectangle(coordinate.move(-10, -10),
                coordinate.move(10, 10));
        changeState(new PassiveMonsterIdleState(getStateMillis(State.IDLE)));
    }


    public RealmMap realmMap() {
        return realmMap;
    }

    public Rectangle wanderingArea() {
        return wanderingArea;
    }


    @Override
    public void update(int delta) {
        cooldown(delta);
        state().update(this, delta);
    }


    private void moveTowardsAttacker(Creature attacker) {
        Direction towards = coordinate().computeDirection(attacker.coordinate());
        changeState(PassiveMonsterMoveState.towardsAttacker(getStateMillis(State.WALK), coordinate(), towards, attacker));
        emitEvent(MoveEvent.movingTo(this, towards));
    }

    public void retaliate(Creature attacker) {
        log.debug("Retaliate start.");
        if (!attacker.attackable()) {
            log.debug("Attacker not attackable, back to idle.");
            changeState(PassiveMonsterIdleState.of(this));
            emitEvent(ChangeStateEvent.of(this));
            return;
        }
        if (attacker.coordinate().distance(coordinate()) > 1) {
            moveTowardsAttacker(attacker);
            return;
        }
        Direction towards = coordinate().computeDirection(attacker.coordinate());
        if (towards != direction()) {
            changeDirection(towards);
        }
        if (cooldown() > 0) {
            log.debug("Need to cooldown for {}.", cooldown());
            changeState(new MonsterCooldownState(cooldown(), attacker));
            emitEvent(SetPositionEvent.of(this));
            return;
        }
        log.debug("Monster attacking.");
        cooldownAttack();
        changeState(MonsterAttackState.attack(this, attacker));
        attacker.attackedBy(this);
        emitEvent(CreatureAttackEvent.ofMonster(this));
    }


    @Override
    public AbstractInterpolation captureInterpolation() {
        return new CreatureInterpolation(id(), coordinate(), state().stateEnum(), direction(), state().elapsedMillis(), name());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        return obj == this || ((PassiveMonster) obj).id() == id();
    }

    @Override
    public int attackSpeed() {
        return 200;
    }

    @Override
    public int recovery() {
        return 100;
    }

    @Override
    public int hit() {
        return 0;
    }

    @Override
    public Damage damage() {
        return damage;
    }

    @Override
    protected CreatureState<PassiveMonster> createHurtState(ViolentCreature attacker) {
        return new PassiveMonsterHurtState(attacker, getStateMillis(State.HURT), state()::afterAttacked);
    }
}
