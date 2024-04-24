package org.y1000.realm;

import org.y1000.entities.Entity;
import org.y1000.entities.players.Player;
import org.y1000.entities.RelevantScope;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class RelevantScopeManager {
    private final Map<Entity, RelevantScope> relevantScopeMap;

    public RelevantScopeManager() {
        relevantScopeMap = new HashMap<>();
    }

    private RelevantScope createScope(Entity source) {
        RelevantScope scope = new RelevantScope(source);
        for (Entity entity : relevantScopeMap.keySet()) {
            if (scope.addIfVisible(entity)) {
                relevantScopeMap.get(entity).addIfVisible(source);
            }
        }
        return scope;
    }

    public void add(Entity entity) {
        if (relevantScopeMap.containsKey(entity)) {
            return;
        }
        RelevantScope scope = createScope(entity);
        relevantScopeMap.put(entity, scope);
        Set<Player> players = scope.filter(Player.class);
        players.forEach(player -> player.connection().write());
    }

    public Set<Player> getVisiblePlayers(Entity entity) {
        RelevantScope relevantScope = relevantScopeMap.get(entity);
        if (relevantScope == null) {
            return Collections.emptySet();
        }
        return relevantScope.filter(Player.class);
    }

    public void update(Entity entity) {

    }
}
