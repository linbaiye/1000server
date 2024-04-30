package org.y1000.entities.players;

import org.y1000.entities.Entity;
import org.y1000.entities.creatures.AbstractCreateState;
import org.y1000.message.clientevent.ClientAttackEvent;
import org.y1000.message.clientevent.ClientEventVisitor;

public abstract class AbstractPlayerAttackState extends AbstractCreateState<PlayerImpl> implements
        ClientEventVisitor {
    private final int length;

    private Entity target;

    public AbstractPlayerAttackState(int length, Entity target) {
        this.length = length;
        this.target = target;
    }

    protected abstract void onElapsed();

    Entity getTarget() {
        return target;
    }

    @Override
    public void update(PlayerImpl player, int delta) {
        if (elapsedMillis() >= length) {
            return;
        }
        elapse(delta);
        if (elapsedMillis() >= length) {
            onElapsed();
        }
    }

    @Override
    public void visit(PlayerImpl player, ClientAttackEvent event) {
         player.realm().findInsight(player, event.entityId())
                         .ifPresent(entity -> target = entity);
    }

}
