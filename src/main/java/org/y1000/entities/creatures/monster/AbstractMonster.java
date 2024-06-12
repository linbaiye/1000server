package org.y1000.entities.creatures.monster;

import lombok.Getter;
import org.y1000.entities.Direction;
import org.y1000.entities.Projectile;
import org.y1000.entities.attribute.Damage;
import org.y1000.entities.creatures.AbstractViolentCreature;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.ViolentCreature;
import org.y1000.entities.creatures.event.CreatureAttackEvent;
import org.y1000.entities.creatures.event.CreatureChangeStateEvent;
import org.y1000.entities.creatures.monster.fight.MonsterFightAttackState;
import org.y1000.entities.creatures.monster.fight.MonsterFightCooldownState;
import org.y1000.entities.creatures.monster.fight.MonsterFightIdleState;
import org.y1000.entities.creatures.monster.fight.MonsterHurtState;
import org.y1000.entities.creatures.monster.wander.MonsterWanderingIdleState;
import org.y1000.message.AbstractCreatureInterpolation;
import org.y1000.message.CreatureInterpolation;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;
import org.y1000.util.Rectangle;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractMonster extends AbstractViolentCreature<AbstractMonster, MonsterState<AbstractMonster>> {
    private final RealmMap realmMap;

    private final Damage damage;

    @Getter
    private final Rectangle wanderingArea;

    @Getter
    private final Coordinate spwanCoordinate;

    private final int recovery;

    private final int attackSpeed;

    private final int avoidance;

    private int currentLife;

    private final int maxLife;

    private final int armor;



    protected AbstractMonster(long id, Coordinate coordinate, Direction direction, String name,
                              RealmMap realmMap, int avoidance, int recovery, int attackSpeed, int life,
                              int wanderingRange, int armor, Map<State, Integer> stateMillis) {
        super(id, coordinate, direction, name, stateMillis);
        this.realmMap = realmMap;
        this.recovery = recovery;
        this.attackSpeed = attackSpeed;
        this.avoidance = avoidance;
        damage = Damage.DEFAULT;
        spwanCoordinate = coordinate;
        wanderingArea = new Rectangle(coordinate.move(-wanderingRange, -wanderingRange), coordinate.move(wanderingRange, wanderingRange));
        maxLife = life;
        currentLife = life;
        this.armor = armor;
        this.realmMap.occupy(this);
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
        if (!handleAttacked(this, hit, (s) -> new MonsterHurtState(getStateMillis(State.HURT), state()))) {
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

    public void fight() {
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
        Direction towards = coordinate().computeDirection(entity.coordinate());
        if (towards != direction()) {
            changeDirection(towards);
        }
        int cooldown = cooldown();
        if (cooldown > 0) {
            changeState(MonsterFightCooldownState.cooldown(cooldown));
            emitEvent(CreatureChangeStateEvent.of(this));
            return;
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
    public int attackSpeed() {
        return attackSpeed;
    }

    @Override
    public int recovery() {
        return recovery;
    }

    @Override
    public int hit() {
        return 0;
    }

    @Override
    public int avoidance() {
        return avoidance;
    }

    @Override
    public Damage damage() {
        return damage;
    }
}
