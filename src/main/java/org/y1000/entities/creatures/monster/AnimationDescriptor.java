package org.y1000.entities.creatures.monster;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.OldPlayerStateEnum;

public record AnimationDescriptor(OldPlayerStateEnum playerStateEnum, Direction direction, int startFrame, int frameNumber, int tickPerFrame) {
    public int animationLength() {
        return tickPerFrame * 10;
    }
}
