package org.y1000.entities.creatures.npc;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.y1000.entities.Direction;
import org.y1000.entities.RemoveEntityEvent;
import org.y1000.entities.AttributeProvider;
import org.y1000.entities.creatures.monster.NpcAnimationEnum;
import org.y1000.entities.creatures.npc.AI.INpcAI;
import org.y1000.entities.creatures.event.INpcMoveEvent;
import org.y1000.entities.creatures.npc.spell.NpcSpell;
import org.y1000.entities.creatures.npc.spell.ShiftSpell;
import org.y1000.entities.players.Damage;
import org.y1000.entities.creatures.*;
import org.y1000.entities.creatures.event.*;
import org.y1000.entities.players.Player;
import org.y1000.entities.projectile.Projectile;
import org.y1000.message.I2ClientMessage;
import org.y1000.message.NpcSnapshot;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;
import org.y1000.util.Rectangle;

import java.util.*;
import java.util.function.Consumer;

public abstract class AbstractNpc extends AbstractCreature implements INpc {

    private final AttributeProvider attributeProvider;

    private final RealmMap realmMap;

    private INpcAI ai;

    private int currentLife;

    private Coordinate spwanCoordinate;

    private Rectangle wanderingArea;

    private final List<NpcSpell> spells;

    private final Map<NpcAnimationEnum, Integer> stateMillis;

    private NpcState npcState;



    public AbstractNpc(long id,
                       Coordinate coordinate,
                       Direction direction,
                       String name,
                       Map<NpcAnimationEnum, Integer> stateMillis,
                       AttributeProvider attributeProvider,
                       RealmMap realmMap,
                       List<NpcSpell> spells,
                       INpcAI ai) {
        super(id, coordinate, direction, name);
        Validate.notNull(ai);
        this.attributeProvider = attributeProvider;
        this.realmMap = realmMap;
        this.spells = spells == null ? Collections.emptyList() : spells;
        this.ai = ai;
        int range = attributeProvider.wanderingRange();
        this.spwanCoordinate = coordinate;
        this.wanderingArea = new Rectangle(coordinate.move(-range, -range), coordinate.move(range, range));
        this.currentLife = attributeProvider.life();
        changeCoordinate(coordinate);
        this.stateMillis = stateMillis;
        this.changeState(NpcCommonState.idle(this));
    }

    @Override
    public void changeState(NpcState state) {
        npcState = state;
    }

    @Override
    public NpcAnimationEnum npcStateEnum() {
        return npcState().stateEnum();
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

    private Optional<ShiftSpell> findShiftSpell() {
        return findSpell(ShiftSpell.class);
    }

    @Override
    public<S extends NpcSpell> Optional<S> findSpell(Class<S> type) {
        Validate.notNull(type);
        return spells.stream()
                .filter(npcSpell -> type.isAssignableFrom(npcSpell.getClass()))
                .findFirst().map(type::cast);
    }

    @Override
    public void startAction(NpcAnimationEnum stateEnum) {
        switch (stateEnum) {
            case Idle -> idle();
            case Die -> die();
            case Move -> move(getStateMillis(NpcAnimationEnum.Move));
        }
    }

    @Override
    public void onMoveFailed() {
        this.getAI().onMoveFailed(this);
    }

    /**
     * Invoked when the npc gets hurt.
     * @param attacker the attacker.
     */
    abstract void hurt(ViolentCreature attacker) ;


    void doHurtAction(ViolentCreature attacker, int millis) {
//        creatureState().moveToHurtCoordinate(this);
//        OldPlayerStateEnum afterHurt = creatureState().decideAfterHurtState();
//        changeState(new NpcHurtState(millis, creatureState(), attacker));
//        emitEvent(new CreatureHurtEvent(this, afterHurt));
//        hurtSound().ifPresent(s -> emitEvent(new EntitySoundEvent(this, s)));
    }


    private void idle() {
        changeState(NpcCommonState.idle(this));
        emitEvent(NpcChangeStateEvent.of(this));
    }


    @Override
    public void stay(int millis) {
        changeState(NpcCommonState.idle(this, millis));
        emitEvent(NpcChangeStateEvent.of(this));
    }

    @Override
    public void turn() {
        changeState(NpcCommonState.turn(this));
        emitEvent(NpcChangeStateEvent.of(this));
    }

    @Override
    public void move(int millis) {
        Validate.isTrue(millis >= 0);
        changeState(NpcMoveState.move(this, millis));
        emitEvent(INpcMoveEvent.move(this, this.direction(), millis));
    }

    public void die() {
        if (isDead())
            return;
        changeState(NpcCommonState.die(this, getStateMillis(NpcAnimationEnum.Die) + (findShiftSpell().isPresent() ? 2000 : 8000)));
        emitEvent(new CreatureDieEvent(this));
        dieSound().ifPresent(s -> emitEvent(new EntitySoundEvent(this, s)));
        ai.onDead(this);
    }


    protected abstract Logger log();

    public void changeAndStartAI(INpcAI newAI) {
        changeAI(newAI);
        start();
    }

    public void changeAI(INpcAI newAI) {
        Validate.notNull(newAI);
        this.ai = newAI;
    }


    @Override
    public INpcAI getAI() {
        return ai;
    }

    @Override
    public void start() {
        this.ai.start(this);
    }

    @Override
    public void onActionDone() {
        if (npcStateEnum() == NpcAnimationEnum.Die) {
            realmMap.free(this);
            emitEvent(new RemoveEntityEvent(this));
        }
        ai.onActionDone(this);
    }

    @Override
    public String idName() {
        return attributeProvider().idName();
    }


    protected boolean doAttacked(Damage damage, int attackerHit,
                                 Consumer<Integer> gainAttackExp,
                                 ViolentCreature attacker) {
        if (isDead() || isDodged(attackerHit)) {
            return false;
        }
        if (getHurtAndGiveExp(damage, this::takeDamage, gainAttackExp) == 0) {
            return false;
        }
        if (currentLife() > 0) {
            hurt(attacker);
        } else {
            die();
        }
        return true;
    }

    @Override
    public boolean attackedBy(Player attacker) {
        Validate.notNull(attacker);
        return false;
//        return doAttacked(attacker.damage(), attacker.hit(), attacker::gainAttackExp, attacker);
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

    @Override
    public int viewWidth() {
        return attributeProvider.viewWidth();
    }


    @Override
    public String animation() {
        return attributeProvider.animate();
    }

    @Override
    public String shape() {
        return attributeProvider.shape();
    }


    @Override
    public I2ClientMessage captureSnapshot() {
        return NpcSnapshot.ofNpc(this, npcState().elapsedMillis());
    }

    @Override
    public int getStateMillis(NpcAnimationEnum stateEnum) {
        return stateMillis.get(stateEnum);
    }

    @Override
    public NpcState npcState() {
        return npcState;
    }

    @Override
    public boolean canBeAttackedNow() {
        return !isDead();
    }
}
