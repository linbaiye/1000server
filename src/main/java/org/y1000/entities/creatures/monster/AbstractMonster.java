package org.y1000.entities.creatures.monster;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.y1000.entities.Direction;
import org.y1000.entities.PlayerProjectile;
import org.y1000.entities.attribute.AttributeProvider;
import org.y1000.entities.attribute.Damage;
import org.y1000.entities.attribute.EmptyAttributeProvider;
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
        changeState(MonsterWanderingIdleState.start(getStateMillis(State.IDLE), wanderingArea.random(coordinate)));
        this.realmMap.occupy(this);
        this.hit = 0;
        attributeProvider = EmptyAttributeProvider.INSTANCE;
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
        changeState(MonsterWanderingIdleState.start(getStateMillis(State.IDLE), wanderingArea.random(coordinate)));
        this.hit = attributeProvider.hit();
        this.damage = new Damage(attributeProvider.damage(), 0, 0, 0);
        this.realmMap.occupy(this);
        this.attributeProvider = attributeProvider;
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
        /*
           if n < LifeData.avoid then exit;    // 피했음.

   if apercent = 100 then begin
      declife := aHitData.damageBody - LifeData.armorBody;
   end else begin
      declife := (aHitData.damageBody * apercent div 100) * aHitData.HitFunctionSkill div 10000 -LifeData.armorBody;
   end;

   // Monster 나 NPC 의 자체 방어력에 의한 비율적 체력감소
   if LifeData.HitArmor > 0 then begin
      declife := declife - ((declife * LifeData.HitArmor) div 100);
   end;

   if declife <= 0 then declife := 1;

   CurLife := CurLife - declife;
   if CurLife <= 0 then CurLife := 0;

   FreezeTick := mmAnsTick + LifeData.recovery;

   if MaxLife <= 0 then begin
      FboAllowDelete := true;
      exit;
   end;

   if MaxLife <= 0 then BasicData.LifePercent := 0
   else BasicData.LifePercent := CurLife * 100 div MaxLife;

   SubData.Percent := BasicData.LifePercent;
   SubData.attacker := aAttacker;
   SubData.HitData.HitType := aHitData.HitType;

   SendLocalMessage (NOTARGETPHONE, FM_STRUCTED, BasicData, SubData);

   //  경치 더하기  //
   n := MaxLife div declife;
   if n > 15 then exp := DEFAULTEXP         // 10대이상 맞을만 하다면 1000
   else  exp := DEFAULTEXP * n * n div (15*15);      // 20대 맞으면 죽구도 남으면 10 => 500   n 15 => 750   5=>250
         */

    }

    @Override
    public void attackedBy(PlayerProjectile projectile) {
        attackedByPlayer(projectile.getShooter(), projectile.damage().bodyDamage(), projectile.getHit(), projectile.getShooter()::gainRangedAttackExp);
    }

    public void fight() {
        if (getFightingEntity() == null || !canAttack(getFightingEntity())) {
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
        if (!canAttack(getFightingEntity())) {
            clearFightingEntity();
        }
    }
}
