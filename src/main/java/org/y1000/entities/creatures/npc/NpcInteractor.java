package org.y1000.entities.creatures.npc;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.Player;
import org.y1000.message.serverevent.InteractionMenuEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class NpcInteractor {

    private final List<NpcInteractability> abilities;

    private final String mainText;

    NpcInteractor(String mainText,
                  List<NpcInteractability> abilities) {
        Validate.isTrue(abilities != null && !abilities.isEmpty());
        this.abilities = abilities;
        this.mainText = mainText;
    }

    public void onNpcClicked(Player player,
                             InteractableNpc npc,
                             String shape,
                             int avatarIdx) {
        if (player == null || npc == null)
            return;
        List<String> interactions = new ArrayList<>();
        abilities.forEach(npcInteractability -> interactions.add(npcInteractability.abilityName()));
        InteractionMenuEvent interactionMenuEvent = new InteractionMenuEvent(player, npc.viewName(), npc.id(), shape, avatarIdx, mainText, interactions);
        player.emitEvent(interactionMenuEvent);
    }

    public void onInteractabilityClicked(Player player,
                                         InteractableNpc npc,
                                         String name) {
        if (player == null || npc == null || StringUtils.isEmpty(name))
            return;
        findAbility(name).ifPresent(a -> a.interact(player, npc));
    }

    public Optional<NpcInteractability> findAbility(String name) {
        return abilities.stream().filter(a -> a.abilityName().equals(name))
                .findFirst();
    }
}
