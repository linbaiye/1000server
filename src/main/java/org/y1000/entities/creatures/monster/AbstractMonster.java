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
import org.y1000.message.serverevent.EntityEvent;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;
import org.y1000.util.Rectangle;

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

    private final int hit;

    private final String attackSound;


    protected AbstractMonster(long id, Coordinate coordinate, Direction direction, String name,
                              RealmMap realmMap, int avoidance, int recovery, int attackSpeed, int life,
                              int wanderingRange, int armor, Map<State, Integer> stateMillis,
                              String attackSound) {
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
        this.attackSound = attackSound;
        changeState(MonsterWanderingIdleState.start(getStateMillis(State.IDLE), wanderingArea.random(coordinate)));
        this.realmMap.occupy(this);
        this.hit = 0;
    }

    protected AbstractMonster(long id, Coordinate coordinate, Direction direction, String name,
                              RealmMap realmMap, Map<State, Integer> stateMillis,
                              AttributeProvider attributeProvider) {
        super(id, coordinate, direction, name, stateMillis);
        this.realmMap = realmMap;
        this.recovery = attributeProvider.recovery();
        this.attackSpeed = attributeProvider.attackSpeed();
        this.avoidance = attributeProvider.avoidance();
        spwanCoordinate = coordinate;
        int range = attributeProvider.wanderingRange();
        wanderingArea = new Rectangle(coordinate.move(-range, -range), coordinate.move(range, range));
        maxLife = attributeProvider.life();
        currentLife = attributeProvider.life();
        this.armor = attributeProvider.armor();
        this.attackSound = attributeProvider.attackSound();
        changeState(MonsterWanderingIdleState.start(getStateMillis(State.IDLE), wanderingArea.random(coordinate)));
        this.hit = attributeProvider.hit();
        this.damage = new Damage(attributeProvider.damage(), 0, 0, 0);
        this.realmMap.occupy(this);
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

    private void applyDamage(Damage damage) {
        int bodyDamage = damage.bodyDamage() - armor();
        bodyDamage = bodyDamage > 0 ? bodyDamage : 1;
        currentLife -= bodyDamage;
        if (currentLife < 0) {
            currentLife = 0;
        }
    }

    @Override
    public void attackedBy(ViolentCreature attacker) {
        if (!handleAttacked(attacker,
                (s) -> new MonsterHurtState(getStateMillis(State.HURT), state()),
                this::applyDamage)) {
            return;
        }
        if (getFightingEntity() == null ||
                getFightingEntity().coordinate().directDistance(coordinate()) > 1) {
            setFightingEntity(attacker);
        }
    }

    @Override
    public void attackedBy(Projectile projectile) {
        attackedBy(projectile.getShooter());
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
        return hit;
    }

    @Override
    public int avoidance() {
        return avoidance;
    }

    @Override
    public Damage damage() {
        return damage;
    }

    @Override
    public int maxLife() {
        return maxLife;
    }

    @Override
    public int currentLife() {
        return currentLife;
    }

    @Override
    public int armor() {
        return armor;
    }

    @Override
    public void onEvent(EntityEvent entityEvent) {
        if (getFightingEntity() == null) {
            log().error("Invalid event received.");
            return;
        }
        if (!canAttack(getFightingEntity())) {
            clearFightingEntity();
        }
    }
}
