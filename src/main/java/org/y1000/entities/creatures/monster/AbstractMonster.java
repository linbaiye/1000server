package org.y1000.entities.creatures.monster;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.y1000.entities.Direction;
import org.y1000.entities.projectile.MonsterProjectile;
import org.y1000.entities.projectile.PlayerProjectile;
import org.y1000.entities.attribute.AttributeProvider;
import org.y1000.entities.attribute.Damage;
import org.y1000.entities.creatures.AbstractViolentCreature;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.ViolentCreature;
import org.y1000.entities.creatures.event.*;
import org.y1000.entities.creatures.monster.fight.MonsterFightAttackState;
import org.y1000.entities.creatures.monster.fight.MonsterFightCooldownState;
import org.y1000.entities.creatures.monster.fight.MonsterFightIdleState;
import org.y1000.entities.creatures.monster.fight.MonsterHurtState;
import org.y1000.entities.creatures.monster.wander.MonsterWanderingIdleState;
import org.y1000.entities.creatures.monster.wander.WanderingState;
import org.y1000.entities.players.Player;
import org.y1000.message.AbstractCreatureInterpolation;
import org.y1000.message.CreatureInterpolation;
import org.y1000.message.serverevent.EntityEvent;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;
import org.y1000.util.Rectangle;
import org.y1000.util.UnaryAction;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

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

    private final AttributeProvider attributeProvider;

    private int timeLeftToSound;

    private final MonsterRangedSpell rangedSpell;


    protected AbstractMonster(long id, Coordinate coordinate, Direction direction, String name,
                              RealmMap realmMap, Map<State, Integer> stateMillis,
                              AttributeProvider attributeProvider, MonsterRangedSpell spell) {
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
        changeState(MonsterWanderingIdleState.start(getStateMillis(State.IDLE), wanderingArea.random(coordinate)));
        this.hit = attributeProvider.hit();
        this.damage = new Damage(attributeProvider.damage(), 0, 0, 0);
        this.realmMap.occupy(this);
        this.attributeProvider = attributeProvider;
        this.rangedSpell = spell;
        setSoundTimer();
    }


    public void revive() {
        changeCoordinate(wanderingArea.random(spwanCoordinate));
        currentLife = maxLife;
    }


    public RealmMap realmMap() {
        return realmMap;
    }

    public Rectangle wanderingArea() {
        return wanderingArea;
    }


    public Optional<MonsterRangedSpell> rangedSpell() {
        return Optional.ofNullable(rangedSpell);
    }

    private void setSoundTimer() {
        timeLeftToSound = ThreadLocalRandom.current().nextInt(15, 25) * 1000;
    }

    private void countDownSoundTimer(int delta) {
        if (normalSound().isEmpty()) {
            return;
        }
        if (state() instanceof WanderingState)
            timeLeftToSound = Math.max(0, timeLeftToSound - delta);
        else
            setSoundTimer();
        if (timeLeftToSound == 0) {
            setSoundTimer();
            emitEvent(new CreatureSoundEvent(this, normalSound().orElse("")));
        }
    }

    @Override
    public void update(int delta) {
        cooldown(delta);
        countDownSoundTimer(delta);
        state().update(this, delta);
    }

    private void takeDamage(Damage damage) {
        int bodyDamage = damage.bodyDamage() - bodyArmor();
        bodyDamage = bodyDamage > 0 ? bodyDamage : 1;
        currentLife -= bodyDamage;
        if (currentLife < 0) {
            currentLife = 0;
        }
        if (currentLife == 0) {
            emitEvent(new CreatureDieEvent(this));
        }
    }

    private void onKilled() {
        changeState(MonsterDieState.die(this));
        emitEvent(new CreatureDieEvent(this));
    }

    @Override
    public boolean attackedBy(ViolentCreature attacker) {
        if (!handleAttacked(attacker,
                (s) -> new MonsterHurtState(getStateMillis(State.HURT), state()),
                this::takeDamage, this::onKilled)) {
            return false;
        }
        if (getFightingEntity() == null ||
                getFightingEntity().coordinate().directDistance(coordinate()) > 1) {
            setFightingEntity(attacker);
        }
        return true;
    }

    private boolean attackedByPlayer(Player attacker, int bodyDamage, int hit, UnaryAction<Integer> gainExpAction) {
        if (!state().attackable() || randomAvoidance(hit)) {
            return false;
        }
        cooldownRecovery();
        var damagedLife = Math.max(bodyDamage - bodyArmor(), 1);
        currentLife = Math.max(currentLife - damagedLife, 0);
        var exp = damagedLifeToExp(damagedLife);
        gainExpAction.invoke(exp);
        if (currentLife() > 0) {
            state().moveToHurtCoordinate(this);
            State afterHurtState = state().decideAfterHurtState();
            changeState(new MonsterHurtState(getStateMillis(State.HURT), state()));
            emitEvent(new CreatureHurtEvent(this, afterHurtState));
            if (getFightingEntity() == null ||
                    getFightingEntity().coordinate().directDistance(coordinate()) > 1) {
                setFightingEntity(attacker);
            }
        } else {
            changeState(MonsterDieState.die(this));
            emitEvent(new CreatureDieEvent(this));
            clearFightingEntity();
        }
        return true;
    }


    @Override
    public boolean attackedBy(Player attacker) {
        return attackedByPlayer(attacker, attacker.damage().bodyDamage(), attacker.hit(), attacker::gainAttackExp);
    }

    @Override
    public void attackedBy(PlayerProjectile projectile) {
        attackedByPlayer(projectile.getShooter(), projectile.damage().bodyDamage(), projectile.getHit(), projectile.getShooter()::gainRangedAttackExp);
    }


    private void doAttack() {
        if (getFightingEntity() == null || !isInRangeAndAttackable(getFightingEntity())) {
            changeState(MonsterWanderingIdleState.reroll(this));
            emitEvent(MonsterChangeStateEvent.of(this));
            return;
        }

    }


    private void rangedFight() {
        var entity = getFightingEntity();
        Direction towards = coordinate().computeDirection(entity.coordinate());
        if (towards != direction()) {
            changeDirection(towards);
        }
        int cooldown = cooldown();
        if (cooldown > 0) {
            changeState(MonsterFightCooldownState.cooldown(cooldown));
            emitEvent(MonsterChangeStateEvent.of(this));
            return;
        }
        cooldownAttack();
        changeState(MonsterFightAttackState.of(this));
        emitEvent(CreatureAttackEvent.ofMonster(this));
        emitEvent(new MonsterShootEvent(new MonsterProjectile(this, entity, rangedSpell.spriteId())));
    }



    public void fight() {
        if (getFightingEntity() == null || !isInRangeAndAttackable(getFightingEntity())) {
            changeState(MonsterWanderingIdleState.reroll(this));
            emitEvent(MonsterChangeStateEvent.of(this));
            return;
        }
        var entity = getFightingEntity();
        if (entity.coordinate().directDistance(coordinate()) > 1) {
            changeState(MonsterFightIdleState.start(this));
            emitEvent(MonsterChangeStateEvent.of(this));
            return;
        }
        Direction towards = coordinate().computeDirection(entity.coordinate());
        if (towards != direction()) {
            changeDirection(towards);
        }
        int cooldown = cooldown();
        if (cooldown > 0) {
            changeState(MonsterFightCooldownState.cooldown(cooldown));
            emitEvent(MonsterChangeStateEvent.of(this));
            return;
        }
        attributeProvider.attackSound().ifPresent(s -> emitEvent(new CreatureSoundEvent(this, s)));
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
    public int bodyArmor() {
        return armor;
    }

    @Override
    public Optional<String> hurtSound() {
        return StringUtils.isEmpty(attributeProvider.hurtSound()) ? Optional.empty() :
                Optional.of(attributeProvider.hurtSound());
    }

    public Optional<String> normalSound() {
        return attributeProvider.normalSound();
    }

    @Override
    public Optional<String> dieSound() {
        return attributeProvider.dieSound();
    }

    @Override
    public void onEvent(EntityEvent entityEvent) {
        if (getFightingEntity() == null) {
            log().error("Invalid event received.");
            return;
        }
        if (!isInRangeAndAttackable(getFightingEntity())) {
            clearFightingEntity();
        }
    }
}
