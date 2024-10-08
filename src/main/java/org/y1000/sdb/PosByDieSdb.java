package org.y1000.sdb;

import java.util.Optional;

public interface PosByDieSdb {

    Integer getDestServer(String id);

    Integer getDestX(String id);

    Integer getDestY(String id);

    Optional<String> findIdByRealmId(int realmId);

}
