package org.y1000.entities.creatures.npc;

import org.apache.commons.lang3.StringUtils;
import org.y1000.message.serverevent.EntityChatEvent;

public interface HumanNpc extends Npc {
    default void say(String text) {
        if (StringUtils.isNotEmpty(text))
            emitEvent(new EntityChatEvent(this, viewName() + ": " + text));
    }
}
