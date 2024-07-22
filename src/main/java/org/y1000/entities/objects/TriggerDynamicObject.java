package org.y1000.entities.objects;

import lombok.Builder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.AbstractEntity;
import org.y1000.entities.AttackableEntity;
import org.y1000.entities.RemoveEntityEvent;
import org.y1000.entities.creatures.ViolentCreature;
import org.y1000.entities.players.Player;
import org.y1000.entities.projectile.Projectile;
import org.y1000.item.Item;
import org.y1000.message.AbstractEntityInterpolation;
import org.y1000.realm.RealmMap;
import org.y1000.sdb.DynamicObjectSdb;
import org.y1000.util.Coordinate;

import java.util.Optional;


public final class TriggerDynamicObject extends AbstractEntity implements DynamicObject {

    private final Coordinate coordinate;

    private DynamicObjectState state;

    private final RealmMap realmMap;

    private final DynamicObjectSdb dynamicObjectSdb;

    private final String idName;

    private final Animation[] animations;

    private int timer;

    private final String requiredItem;


    @Builder
    public TriggerDynamicObject(long id,
                                RealmMap realmMap,
                                Coordinate coordinate,
                                DynamicObjectSdb dynamicObjectSdb,
                                String idName) {
        super(id);
        Validate.notNull(realmMap);
        Validate.notNull(coordinate);
        Validate.notNull(dynamicObjectSdb);
        Validate.notNull(idName);
        this.coordinate = coordinate;
        this.realmMap = realmMap;
        this.dynamicObjectSdb = dynamicObjectSdb;
        this.idName = idName;
        this.state = DynamicObjectState.INITIAL;
        this.animations = parseAnimationFrames(idName, dynamicObjectSdb);
        this.requiredItem = dynamicObjectSdb.getEventItem(idName).split(":")[0].trim();
        realmMap.occupy(this);
    }

    @Override
    public Coordinate coordinate() {
        return coordinate;
    }

    public void rebuild()  {
        this.state = DynamicObjectState.INITIAL;
        realmMap.occupy(this);
    }

    @Override
    public void update(int delta) {
        if (state == DynamicObjectState.INITIAL) {
            return;
        }
        timer -= delta;
        if (timer > 0) {
            return;
        }
        if (state == DynamicObjectState.CHANGING) {
            state = DynamicObjectState.CHANGED;
            timer = dynamicObjectSdb.getOpenedInterval(idName);
            emitEvent(new UpdateDynamicObjectEvent(this));
        } else {
            if (dynamicObjectSdb.isRemove(idName)) {
                realmMap.free(this);
                emitEvent(new RemoveEntityEvent(this));
            } else {
                state = DynamicObjectState.INITIAL;
                emitEvent(new UpdateDynamicObjectEvent(this));
            }
        }
    }

    public void trigger(Player player, int slot) {
        if (state != DynamicObjectState.INITIAL) {
            return;
        }
        Item item = player.inventory().getItem(slot);
        if (item != null && requiredItem.equals(item.name()) && player.consumeItem(slot)) {
            state = DynamicObjectState.CHANGING;
            timer = changingDuration();
            emitEvent(new UpdateDynamicObjectEvent(this));
        }
    }

    private int changingDuration() {
        return 500 * currentAnimation().total();
    }

    @Override
    public AbstractEntityInterpolation captureInterpolation() {
        var elapsed = state == DynamicObjectState.CHANGING ? changingDuration() - timer : 0;
        return new DynamicObjectInterpolation(this, elapsed);
    }

    @Override
    public boolean canBeAttackedNow() {
        return true;
    }

    public String shape() {
        return dynamicObjectSdb.getShape(idName);
    }

    public Optional<String> name() {
        return dynamicObjectSdb.getViewName(idName);
    }

    public Animation currentAnimation() {
        if (state == DynamicObjectState.INITIAL) {
            return animations[0];
        } else if (state == DynamicObjectState.CHANGING) {
            return animations[1];
        } else {
            return animations[2];
        }
    }

    @Override
    public boolean attackedBy(Player attacker) {
        return true;
    }

    @Override
    public void attackedBy(ViolentCreature attacker) {
        // bad,.
    }

    @Override
    public void attackedBy(Projectile projectile) {
        // bad.
    }

    public DynamicObjectType type() {
        return DynamicObjectType.TRIGGER;
    }

    @Override
    public RealmMap realmMap() {
        return realmMap;
    }

    private static Animation[] parseAnimationFrames(String idName, DynamicObjectSdb sdb) {
        try {
            int s0 = Integer.parseInt(sdb.getSStep0(idName));
            int e0 = Integer.parseInt(sdb.getEStep0(idName));
            int s1 = Integer.parseInt(sdb.getSStep1(idName));
            int e1 = Integer.parseInt(sdb.getEStep1(idName));
            String sStep2 = sdb.getSStep2(idName);
            int s2 = StringUtils.isNotEmpty(sStep2) ? Integer.parseInt(sStep2) : e1;
            String eStep2 = sdb.getEStep2(idName);
            int e2 = StringUtils.isNotEmpty(eStep2) ? Integer.parseInt(eStep2) : s2;
            return new Animation[]{new Animation(s0, e0), new Animation(s1, e1), new Animation(s2, e2)};
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid animation for "+ idName, e);
        }
    }

}
