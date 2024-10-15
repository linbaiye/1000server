package org.y1000.sdb;

import org.y1000.entities.creatures.NpcType;

import java.util.Optional;

public interface CreateNonMonsterSdb extends CreateNpcSdb {

    Optional<NpcType> getType(String idName);

    Optional<String> getConfig(String idName);

    boolean containsNpc(String viewName);

    Optional<String> getMerchant(String viewName);

    Optional<String> getDialog(String idName);
}
