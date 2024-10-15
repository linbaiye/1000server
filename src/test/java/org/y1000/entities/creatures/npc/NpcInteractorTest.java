package org.y1000.entities.creatures.npc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.TestingEventListener;
import org.y1000.entities.creatures.npc.interactability.NpcInteractability;
import org.y1000.entities.creatures.npc.interactability.NpcInteractor;
import org.y1000.entities.players.Player;
import org.y1000.event.EntityEvent;
import org.y1000.message.serverevent.InteractionMenuEvent;
import org.y1000.network.gen.NpcInteractionMenuPacket;
import org.y1000.util.Coordinate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NpcInteractorTest {

    private NpcInteractor npcInteractor;
    private List<NpcInteractability> interactabilityList;

    private Player player;

    private InteractableNpc npc;

    private TestingEventListener eventListener;

    private final NpcInteractability defaultAbility = new NpcInteractability() {
        @Override
        public String playerSeeingName() {
            return "default";
        }
        @Override
        public void interact(Player clicker, InteractableNpc npc) {
        }
    };

    @BeforeEach
    void setUp() {
        interactabilityList = new ArrayList<>();
        interactabilityList.add(defaultAbility);
        npcInteractor = new NpcInteractor("Main text", interactabilityList);
        player = Mockito.mock(Player.class);
        when(player.coordinate()).thenReturn(Coordinate.xy(1, 1));
        npc = Mockito.mock(InteractableNpc.class);
        eventListener = new TestingEventListener();
        doAnswer(invocationOnMock -> {
            eventListener.onEvent(invocationOnMock.getArgument(0));
            return null;
        }).when(player).emitEvent(any(EntityEvent.class));
        when(npc.id()).thenReturn(1L);
        when(npc.viewName()).thenReturn("name");
        when(npc.shape()).thenReturn("shape");
        when(npc.avatarImageId()).thenReturn(2);
        when(npc.mainMenuDialog()).thenReturn(npcInteractor.getMainText());
    }

    @Test
    void onNpcClickedNoVisible() {
        when(npc.canBeSeenAt(any(Coordinate.class))).thenReturn(false);
        npcInteractor.onNpcClicked(player, npc);
        verify(player, times(0)).emitEvent(any(EntityEvent.class));
    }

    @Test
    void onNpcClicked_whenVisible() {
        when(npc.canBeSeenAt(any(Coordinate.class))).thenReturn(true);
        npcInteractor.onNpcClicked(player, npc);
        NpcInteractionMenuPacket interactionMenu = eventListener.removeFirst(InteractionMenuEvent.class).toPacket().getInteractionMenu();
        assertEquals(1L, interactionMenu.getId());
        assertEquals("name", interactionMenu.getViewName());
        assertEquals("shape", interactionMenu.getShape());
        assertEquals("default", interactionMenu.getInteractions(0));
        assertEquals("Main text", interactionMenu.getText());
    }

    @Test
    void onInteractabilityClicked() {
    }

    @Test
    void findAbility() {
    }
}