package org.y1000.entities.players.kungfu;

import lombok.experimental.SuperBuilder;
import org.y1000.entities.players.PlayerImpl;

@SuperBuilder
public final class AssistantKungFu extends AbstractLevelKungFu {

    private boolean eightDirection;

    @Override
    public String name() {
        return null;
    }

    public void apply(PlayerImpl player) {

    }

}
