package org.y1000.sdb;

import java.util.Optional;

public interface RealmSpecificSdbRepository {

    CreateNpcSdb loadCreateMonster(int realmId);

    boolean monsterSdbExists(int realmId);

    CreateNonMonsterSdb loadCreateNpc(int realmId);

    boolean npcSdbExists(int realmId);

    CreateDynamicObjectSdb loadCreateObject(int realmId);

    boolean objectSdbExists(int realmId);

    HaveItemSdb loadHaveItem(int realmId);

    Optional<NpcDialogSdb> loadDialog(String name);

}
