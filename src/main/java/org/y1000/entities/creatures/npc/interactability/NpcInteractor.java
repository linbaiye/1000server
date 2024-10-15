package org.y1000.entities.creatures.npc.interactability;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.hibernate.cfg.ValidationSettings;
import org.y1000.entities.creatures.npc.InteractableNpc;
import org.y1000.entities.players.Player;
import org.y1000.message.serverevent.InteractionMenuEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public final class NpcInteractor {

    private final List<NpcInteractability> abilities;

    @Getter
    private final String mainText;

    public NpcInteractor(String mainText,
                  List<NpcInteractability> abilities) {
        Validate.isTrue(abilities != null && !abilities.isEmpty());
        this.abilities = abilities;
        this.mainText = mainText != null? mainText : "";
    }

    private boolean canInteract(Player player,
                                InteractableNpc npc) {
        return player != null && !player.isDead() &&
                npc != null && !npc.isDead() &&
                npc.canBeSeenAt(player.coordinate());
    }

    public void onNpcClicked(Player player,
                             InteractableNpc npc) {
        if (!canInteract(player, npc))
            return;
        List<String> interactions = new ArrayList<>();
        abilities.forEach(npcInteractability -> interactions.add(npcInteractability.playerSeeingName()));
        InteractionMenuEvent interactionMenuEvent = InteractionMenuEvent.mainMenu(player, npc, interactions);
        player.emitEvent(interactionMenuEvent);
    }

    public void onInteractabilityClicked(Player player,
                                         InteractableNpc npc,
                                         String name) {
        if (canInteract(player, npc) && !StringUtils.isEmpty(name))
            findInteractbility(name).ifPresent(a -> a.interact(player, npc));
    }

    public Optional<NpcInteractability> findFirstInteractbility(Predicate<? super NpcInteractability> predicate) {
        return abilities.stream().filter(predicate).findFirst();
    }

    private Optional<NpcInteractability> findInteractbility(String name) {
        return findFirstInteractbility(a -> a.playerSeeingName().equals(name));
    }
}
