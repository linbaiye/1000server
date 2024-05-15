package org.y1000.entities.players;

import org.y1000.entities.creatures.AbstractCreateState;
import org.y1000.entities.creatures.Creature;
import org.y1000.entities.creatures.State;
import org.y1000.message.clientevent.ClientEventVisitor;

public class AbstractPlayerCooldownState extends AbstractCreateState<PlayerImpl> implements
        ClientEventVisitor {

    public AbstractPlayerCooldownState(int totalMillis) {
        super(totalMillis);
    }

    @Override
    public State stateEnum() {
        return State.COOLDOWN;
    }


    @Override
    public void update(PlayerImpl player, int delta) {

    }

    @Override
    public void afterAttacked(PlayerImpl player, Creature attacker) {
        player.changeState(new PlayerCooldownState(player.cooldown(), attacker));
    }
}
