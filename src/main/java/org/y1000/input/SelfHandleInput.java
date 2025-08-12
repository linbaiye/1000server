package org.y1000.input;

import org.y1000.entities.players.PlayerInputHandler;

/**
 * Inputs that can be handled by player alone.
 */
public interface SelfHandleInput {
    void accept(PlayerInputHandler handler);
}
