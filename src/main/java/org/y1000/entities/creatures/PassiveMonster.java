package org.y1000.entities.creatures;


import org.y1000.entities.Direction;
import org.y1000.entities.attribute.ArmorAttribute;
import org.y1000.entities.attribute.DamageAttribute;
import org.y1000.entities.attribute.HarhAttribute;
import org.y1000.entities.creatures.event.CreatureHurtEvent;
import org.y1000.message.AbstractInterpolation;
import org.y1000.message.CreatureInterpolation;
import org.y1000.realm.RealmImpl;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;
import org.y1000.util.Rectangle;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public final class PassiveMonster extends AbstractCreature<PassiveMonster> {

    private final RealmMap realmMap;

    private final ArmorAttribute armorAttribute;

    private final DamageAttribute damageAttribute;

    private final HarhAttribute harhAttribute;

    private int recoveryCooldown;
    private int attackCooldown;
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
        damageAttribute = DamageAttribute.DEFAULT;
        harhAttribute = new HarhAttribute(0, 25, 100, 0, 200);
        recoveryCooldown = 0;
        attackCooldown = 0;
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


    public void cooldownRecovery() {
        recoveryCooldown = harhAttribute.recovery()  * RealmImpl.STEP_MILLIS;
    }

    public void cooldownAttack() {
        attackCooldown = harhAttribute.attackSpeed() * RealmImpl.STEP_MILLIS;
    }

    public int recoveryCooldown() {
        return recoveryCooldown;
    }

    public int attackCooldown() {
        return attackCooldown;
    }


    @Override
    public void update(int delta) {
        recoveryCooldown = recoveryCooldown > delta ? recoveryCooldown - delta : 0;
        attackCooldown = attackCooldown > delta ? attackCooldown - delta : 0;
        state().update(this, delta);
    }


    @Override
    public void attackedBy(Creature attacker) {
        if (state().attackable()) {
            state().attackedBy(this, attacker);
        }
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
}
