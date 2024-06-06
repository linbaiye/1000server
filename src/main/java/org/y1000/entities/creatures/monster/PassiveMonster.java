package org.y1000.entities.creatures.monster;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.Direction;
import org.y1000.entities.Projectile;
import org.y1000.entities.attribute.ArmorAttribute;
import org.y1000.entities.attribute.Damage;
import org.y1000.entities.creatures.*;
import org.y1000.entities.creatures.event.CreatureChangeStateEvent;
import org.y1000.entities.creatures.event.CreatureAttackEvent;
import org.y1000.entities.creatures.monster.fight.*;
import org.y1000.entities.creatures.monster.wander.MonsterWanderingIdleState;
import org.y1000.message.AbstractCreatureInterpolation;
import org.y1000.message.CreatureInterpolation;
import org.y1000.message.serverevent.EntityEvent;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;
import org.y1000.util.Rectangle;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@Slf4j
public final class PassiveMonster extends AbstractViolentCreature<PassiveMonster, CreatureState<PassiveMonster>> {

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
        if (!handleAttacked(this, hit, (s) -> new MonsterHurtState(getStateMillis(State.HURT)))) {
            return;
        }
        if (getFightingEntity() == null ||
                getFightingEntity().coordinate().directDistance(coordinate()) > 1) {
            setFightingEntity(attacker);
        }
    }

    @Override
    public void attackedBy(ViolentCreature attacker) {
        attackedBy(attacker, attacker.hit());
    }

    @Override
    public void attackedBy(Projectile projectile) {
        attackedBy(projectile.getShooter(), projectile.getHit());
    }

    public void nextHuntMove() {
        if (getFightingEntity() == null) {
            changeState(MonsterWanderingIdleState.reroll(this));
            emitEvent(CreatureChangeStateEvent.of(this));
            return;
        }
        var entity = getFightingEntity();
        if (entity.coordinate().directDistance(coordinate()) > 1) {
            changeState(MonsterFightIdleState.start(this));
            emitEvent(CreatureChangeStateEvent.of(this));
            return;
        }
        int cooldown = cooldown();
        if (cooldown > 0) {
            changeState(MonsterFightCooldownState.cooldown(cooldown));
            emitEvent(CreatureChangeStateEvent.of(this));
            return;
        }
        Direction towards = coordinate().computeDirection(entity.coordinate());
        if (towards != direction()) {
            changeDirection(towards);
        }
        cooldownAttack();
        entity.attackedBy(this);
        changeState(MonsterFightAttackState.of(this));
        emitEvent(CreatureAttackEvent.ofMonster(this));
    }


    @Override
    public AbstractCreatureInterpolation captureInterpolation() {
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


    @Override
    public void onEvent(EntityEvent entityEvent) {
        if (getFightingEntity() == null) {
            log.error("Invalid event received.");
            return;
        }
        if (!canAttack(getFightingEntity())) {
            clearFightingEntity();
        }
    }
}
