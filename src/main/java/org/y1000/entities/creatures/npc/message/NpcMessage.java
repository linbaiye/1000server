package org.y1000.entities.creatures.npc.message;

import org.y1000.entities.creatures.npc.Npc;
import org.y1000.message.I2ClientMessage;

public interface NpcMessage extends I2ClientMessage {
    Npc source();

    void accept(NpcMessageHandler handler);
}
