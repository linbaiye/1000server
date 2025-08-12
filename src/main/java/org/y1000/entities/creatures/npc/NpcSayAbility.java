package org.y1000.entities.creatures.npc;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.entities.creatures.npc.event.NpcSayEvent;

import java.util.List;

public final class NpcSayAbility {

    private int counter;

    private final String[] dialogs;

    private int index;

    public NpcSayAbility(List<String> dialogs) {
        Validate.isTrue(dialogs != null && !dialogs.isEmpty());
        this.dialogs = dialogs.toArray(new String[0]);
        index = 0;
        counter = 0;
    }

    public void trySay(Npc npc) {
        if (++counter < 10)
            return;
        counter = 0;
        npc.sendEvent(NpcSayEvent.say(npc, dialogs[index]));
        if (++index >= dialogs.length)
            index = 0;
    }
}
