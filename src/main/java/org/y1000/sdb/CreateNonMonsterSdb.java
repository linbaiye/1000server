package org.y1000.sdb;


import java.util.Optional;

public interface CreateNonMonsterSdb extends CreateNpcSdb {

    Optional<String> getConfig(String idName);

    boolean containsNpc(String viewName);

    Optional<String> getMerchant(String viewName);

    Optional<String> getDialog(String idName);
}
