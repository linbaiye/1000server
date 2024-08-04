package org.y1000.entities.creatures.npc;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.Direction;
import org.y1000.entities.RemoveEntityEvent;
import org.y1000.entities.AttributeProvider;
import org.y1000.entities.players.Damage;
import org.y1000.entities.creatures.*;
import org.y1000.entities.creatures.event.*;
import org.y1000.entities.players.Player;
import org.y1000.entities.projectile.Projectile;
import org.y1000.realm.RealmMap;
import org.y1000.util.Action;
import org.y1000.util.Coordinate;
import org.y1000.util.Rectangle;
import org.y1000.util.UnaryAction;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public abstract class AbstractNpc extends AbstractCreature<Npc, NpcState> implements Npc {

    private final AttributeProvider attributeProvider;

    private final RealmMap realmMap;

    private int currentLife;

    private Coordinate spwanCoordinate;

    private Rectangle wanderingArea;

    private static final Set<State> ACCEPTABLE_STATES = Set.of(State.IDLE, State.WALK, State.FROZEN, State.DIE);

    public AbstractNpc(long id,
                       Coordinate coordinate,
                       Direction direction,
                       String name,
                       Map<State, Integer> stateMillis,
                       AttributeProvider attributeProvider,
                       RealmMap realmMap) {
        super(id, coordinate, direction, name, stateMillis);
        this.attributeProvider = attributeProvider;
        this.realmMap = realmMap;
        doRevive(coordinate);
    }


    protected void doRevive(Coordinate coordinate) {
        int range = attributeProvider.wanderingRange();
        spwanCoordinate = coordinate;
        wanderingArea = new Rectangle(coordinate.move(-range, -range), coordinate.move(range, range));
        currentLife = attributeProvider.life();
        changeCoordinate(coordinate);
        this.changeState(NpcCommonState.idle(getStateMillis(State.IDLE)));
    }

    protected AttributeProvider attributeProvider() {
        return attributeProvider;
    }

    @Override
    public Rectangle wanderingArea() {
        return wanderingArea;
    }

    @Override
    public Coordinate spawnCoordinate() {
        return spwanCoordinate;
    }

    @Override
    public int avoidance() {
        return attributeProvider.avoidance();
    }

    @Override
    public int maxLife() {
        return attributeProvider.life();
    }

    @Override
    public int currentLife() {
        return currentLife;
    }

    @Override
    public int bodyArmor() {
        return attributeProvider.armor();
    }


    @Override
    public void startAction(State state) {
        Validate.isTrue(ACCEPTABLE_STATES.contains(state), "Invalid state : " + state);
        switch (state) {
            case IDLE -> idle();
            case DIE -> die();
            //case FROZEN -> freeze();
            case WALK -> move(getStateMillis(State.WALK));
        }
    }


    private void hurtAction(ViolentCreature attacker) {
        state().moveToHurtCoordinate(this);
        State afterHurt = state().decideAfterHurtState();
        changeState(new NpcHurtState(getStateMillis(State.HURT), state(), attacker));
        emitEvent(new CreatureHurtEvent(this, afterHurt));
        hurtSound().ifPresent(s -> emitEvent(new EntitySoundEvent(this, s)));
    }


    private void idle() {
        changeState(NpcCommonState.idle(getStateMillis(State.IDLE) * 2));
        emitEvent(new NpcChangeStateEvent(this, stateEnum()));
    }

    private void freeze() {
        changeState(NpcCommonState.freeze(getStateMillis(State.FROZEN)));
        emitEvent(new NpcChangeStateEvent(this, stateEnum()));
    }


    @Override
    public void stay(int millis) {
        changeState(NpcCommonState.idle(millis));
        emitEvent(new NpcChangeStateEvent(this, stateEnum()));
    }

    @Override
    public void move(int millis) {
        Validate.isTrue(millis > 0);
        changeState(NpcMoveState.move(this, millis));
        emitEvent(NpcMoveEvent.move(this, this.direction(), millis));
    }

    private void die() {
        changeState(NpcCommonState.die(getStateMillis(State.DIE) + 8000));
        emitEvent(new CreatureDieEvent(this));
        dieSound().ifPresent(s -> emitEvent(new EntitySoundEvent(this, s)));
    }

    protected void handleActionDone(Action action) {
        if (stateEnum() == State.DIE) {
            emitEvent(new RemoveEntityEvent(this));
            realmMap.free(this);
        } else {
            action.invoke();
        }
    }

    @Override
    public String idName() {
        return attributeProvider().idName();
    }

    protected boolean doAttacked(Damage damage, int attackerHit,
                                 UnaryAction<Integer> gainAttackExp,
                                 ViolentCreature attacker) {
        if (doAttackedAndGiveExp(damage, attackerHit, this::takeDamage, gainAttackExp) == 0) {
            return false;
        }
        if (currentLife() > 0) {
            hurtAction(attacker);
        } else {
            startAction(State.DIE);
        }
        return true;
    }

    @Override
    public boolean attackedBy(Player attacker) {
        Validate.notNull(attacker);
        return doAttacked(attacker.damage(), attacker.hit(), attacker::gainAttackExp, attacker);
    }

    @Override
    public void attackedBy(Projectile projectile) {
        Validate.notNull(projectile);
        if (projectile.shooter() instanceof Player player) {
            doAttacked(projectile.damage(), projectile.hit(), player::gainRangedAttackExp, player);
        } else {
            attackedBy(projectile.shooter());
        }
    }

    @Override
    public void attackedBy(ViolentCreature attacker) {
        Validate.notNull(attacker);
        doAttacked(attacker.damage(), attacker.hit(), e -> {}, attacker);
    }

    protected void takeDamage(Damage damage) {
        int bodyDamage = damage.bodyDamage() - bodyArmor();
        bodyDamage = bodyDamage > 0 ? bodyDamage : 1;
        currentLife = currentLife > bodyDamage ? currentLife - bodyDamage : 0;
    }

    @Override
    public RealmMap realmMap() {
        return realmMap;
    }

    @Override
    public Optional<String> hurtSound() {
        return StringUtils.isEmpty(attributeProvider.hurtSound()) ? Optional.empty() :
                Optional.of(attributeProvider.hurtSound());
    }


    @Override
    public Optional<String> dieSound() {
        return attributeProvider.dieSound();
    }

    @Override
    public int attackedByAoe(ViolentCreature caster, int hit, Damage damage) {
        Validate.notNull(caster);
        Validate.notNull(damage);
        Validate.isTrue(hit >= 0);
        int[] exp = new int[1];
        doAttacked(damage, hit, e -> exp[0] = e, caster);
        return exp[0];
    }

    @Override
    public int walkSpeed() {
        return attributeProvider.walkSpeed();
    }
}
