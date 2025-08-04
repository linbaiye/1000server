package org.y1000.entities.creatures.npc.AI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.creatures.npc.HumanNpc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class ChatterTest {

    private NpcSayAbility chatter;

    private List<String> dialogs;

    private HumanNpc humanNpc;
    @BeforeEach
    void setUp() {
        humanNpc = Mockito.mock(HumanNpc.class);
        dialogs = new ArrayList<>();
        dialogs.add("test");
        chatter = new NpcSayAbility(dialogs);
    }


}