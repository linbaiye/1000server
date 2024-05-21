package org.y1000.entities.creatures.monster;


import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.Direction;
import org.y1000.entities.Projectile;
import org.y1000.entities.attribute.ArmorAttribute;
import org.y1000.entities.attribute.Damage;
import org.y1000.entities.creatures.AbstractViolentCreature;
import org.y1000.entities.creatures.Creature;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.ViolentCreature;
import org.y1000.entities.creatures.event.ChangeStateEvent;
import org.y1000.entities.creatures.event.CreatureAttackEvent;
import org.y1000.entities.creatures.monster.fight.MonsterCooldownState;
import org.y1000.entities.creatures.monster.fight.MonsterFightingIdleState;
import org.y1000.entities.creatures.monster.wander.MonsterWanderingIdleState;
import org.y1000.message.AbstractInterpolation;
import org.y1000.message.CreatureInterpolation;
import org.y1000.message.SetPositionEvent;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;
import org.y1000.util.Rectangle;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@Slf4j
public final class PassiveMonster extends AbstractViolentCreature<PassiveMonster, MonsterState<PassiveMonster>> {

    private final RealmMap realmMap;

    private final ArmorAttribute armorAttribute;

    private final Damage damage;

    @Getter
    private final Rectangle wanderingArea;

    @Getter
    private final Coordinate spwanCoordinate;

    private static final Map<State, Integer> BAFFULO_STATE_MILLIS = new HashMap<>() {
        {
            put(State.IDLE, 1000);
            put(State.WALK, 770);
            put(State.HURT, 540);
            put(State.ATTACK, 700);
            put(State.DIE, 700);
            put(State.FROZEN, 900);
        }
    };


    public PassiveMonster(long id, Coordinate coordinate, Direction direction, String name,
                          RealmMap realmMap) {
        super(id, coordinate, direction, name, BAFFULO_STATE_MILLIS);
        this.realmMap = realmMap;
        armorAttribute = ArmorAttribute.DEFAULT;
        damage = Damage.DEFAULT;
        spwanCoordinate = coordinate;
        this.wanderingArea = new Rectangle(coordinate.move(-10, -10),
                coordinate.move(10, 10));
        changeState(MonsterWanderingIdleState.start(getStateMillis(State.IDLE), wanderingArea.random(coordinate)));
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

    private void attackedBy(ViolentCreature attacker, int hit) {
        handleAttacked(this, hit, () ->  new PassiveMonsterHurtState(attacker, getStateMillis(State.HURT), state()::afterHurt));
    }

    @Override
    public void attackedBy(ViolentCreature attacker) {
        attackedBy(attacker, attacker.hit());
    }

    @Override
    public void attackedBy(Projectile projectile) {
        attackedBy(projectile.getShooter(), projectile.getHit());
    }

    private void moveTowardsAttacker(Creature attacker) {
        changeState(MonsterFightingIdleState.hunt(this, attacker));
        emitEvent(ChangeStateEvent.of(this));
    }

    public void attack(Creature attacker) {
        if (!attacker.attackable()) {
            log.trace("Not attackable, back to idle.");
            changeState(MonsterWanderingIdleState.reroll(this));
            emitEvent(ChangeStateEvent.of(this));
            return;
        }
        if (attacker.coordinate().directDistance(coordinate()) > 1) {
            log.trace("Moving to attacker.");
            moveTowardsAttacker(attacker);
            return;
        }
        Direction towards = coordinate().computeDirection(attacker.coordinate());
        if (towards != direction()) {
            changeDirection(towards);
        }
        int cooldown = cooldown();
        if (cooldown > 0) {
            log.trace("Need to cooldown for {} millis.", cooldown);
            changeState(new MonsterCooldownState(cooldown, attacker));
            emitEvent(SetPositionEvent.of(this));
            return;
        }
        log.trace("Attack.");
        cooldownAttack();
        attacker.attackedBy(this);
        changeState(MonsterAttackState.attack(this, attacker));
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
    protected Logger log() {
        return log;
    }
}
