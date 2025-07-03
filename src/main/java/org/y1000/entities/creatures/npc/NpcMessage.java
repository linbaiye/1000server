package org.y1000.entities.creatures.npc;

import org.y1000.message.I2ClientMessage;

public interface NpcMessage extends I2ClientMessage  {
    Npc source();
}
