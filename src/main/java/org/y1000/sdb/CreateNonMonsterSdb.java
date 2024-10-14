package org.y1000.sdb;

import org.y1000.entities.creatures.NpcType;
import org.y1000.message.clientevent.ClientOperateBankEvent;

import java.util.Optional;

public interface CreateNonMonsterSdb extends CreateNpcSdb {

    Optional<NpcType> getType(String idName);

    Optional<String> getConfig(String idName);

    boolean containsNpc(String idName);

    Optional<String> getMerchant(String idName);

    Optional<String> getDialog(String idName);
}
