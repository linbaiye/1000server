package org.y1000.sdb;

public interface CreateEntitySdbRepository {

    CreateNpcSdb loadMonster(int realmId);

    boolean monsterSdbExists(int realmId);

    CreateNonMonsterSdb loadNpc(int realmId);

    boolean npcSdbExists(int realmId);

    CreateDynamicObjectSdb loadObject(int realmId);

    boolean objectSdbExists(int realmId);

    HaveItemSdb loadHaveItem(int realmId);

}
