package org.y1000.entities.objects;

import lombok.Builder;
import org.y1000.entities.RemoveEntityEvent;
import org.y1000.entities.creatures.ViolentCreature;
import org.y1000.entities.creatures.event.EntitySoundEvent;
import org.y1000.entities.players.Player;
import org.y1000.entities.projectile.Projectile;
import org.y1000.item.Item;
import org.y1000.message.AbstractEntityInterpolation;
import org.y1000.realm.RealmMap;
import org.y1000.sdb.DynamicObjectSdb;
import org.y1000.util.Coordinate;

import java.util.Objects;


public final class TriggerDynamicObject extends AbstractMutableDynamicObject implements RespawnDynamicObject {

    private final String requiredItem;

    private DynamicObjectState state;

    @Builder
    public TriggerDynamicObject(long id,
                                RealmMap realmMap,
                                Coordinate coordinate,
                                DynamicObjectSdb dynamicObjectSdb,
                                String idName) {
        super(id, coordinate, realmMap, dynamicObjectSdb, idName, parseAnimationFrames(idName, dynamicObjectSdb));
        this.requiredItem = dynamicObjectSdb.getEventItem(idName).split(":")[0].trim();
        this.state = DynamicObjectState.INITIAL;
        changeAnimation(0);
    }


    @Override
    public void update(int delta) {
        if (state == DynamicObjectState.INITIAL) {
            return;
        }
        updateAnimation(delta);
    }

    private int getIndex() {
        if (state == DynamicObjectState.INITIAL) {
            return 0;
        } else if (state == DynamicObjectState.CHANGING) {
            return 1;
        } else {
            return 2;
        }
    }

    private void changeState(DynamicObjectState state) {
        this.state = state;
        if (state == DynamicObjectState.CHANGED) {
            changeAnimation(getIndex(), dynamicObjectSdb().getOpenedMillis(idName()));
        } else {
            changeAnimation(getIndex());
        }
    }

    public void trigger(Player player, int slot) {
        if (state != DynamicObjectState.INITIAL) {
            return;
        }
        if (occupyingCoordinates().stream().noneMatch(coordinate -> coordinate.directDistance(player.coordinate()) <= 2)) {
            return;
        }
        Item item = player.inventory().getItem(slot);
        if (item != null && requiredItem.equals(item.name()) && player.consumeItem(slot)) {
            dynamicObjectSdb().getSoundEvent(idName()).ifPresent(s -> emitEvent(new EntitySoundEvent(this, s)));
            changeState(DynamicObjectState.CHANGING);
        }
    }

    @Override
    public AbstractEntityInterpolation captureInterpolation() {
        return new DynamicObjectInterpolation(this, animationElapsedDuration(), requiredItem);
    }


    @Override
    public boolean canBeAttackedNow() {
        return state == DynamicObjectState.INITIAL;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TriggerDynamicObject object)) return false;
        return Objects.equals(id(), object.id());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id());
    }

    @Override
    protected void onAnimationDone() {
        if (state == DynamicObjectState.CHANGING) {
            changeState(DynamicObjectState.CHANGED);
            emitEvent(new DynamicObjectDieEvent(this));
            return;
        }
        if (dynamicObjectSdb().isRemove(idName())) {
            realmMap().free(this);
            emitEvent(new RemoveEntityEvent(this));
        } else {
            changeState(DynamicObjectState.INITIAL);
        }
    }


    @Override
    public void respawn() {
        realmMap().occupy(this);
        changeState(DynamicObjectState.INITIAL);
    }

    @Override
    public int respawnTime() {
        return dynamicObjectSdb().getRegenInterval(idName()) * 10;
    }
}
