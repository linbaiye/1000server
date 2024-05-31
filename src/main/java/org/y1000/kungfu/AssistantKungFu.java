package org.y1000.kungfu;

import lombok.experimental.SuperBuilder;
import org.y1000.entities.players.PlayerImpl;

@SuperBuilder
public final class AssistantKungFu extends AbstractKungFu {

    private boolean eightDirection;

    public void apply(PlayerImpl player) {

    }

    @Override
    public KungFuType kungFuType() {
        return KungFuType.ASSISTANT;
    }
}
