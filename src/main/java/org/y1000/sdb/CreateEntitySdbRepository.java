package org.y1000.sdb;

public interface CreateEntitySdbRepository {

    CreateNpcSdb loadMonster(int realmId);

    boolean monsterSdbExists(int realmId);

    CreateNpcSdb loadNpc(int realmId);

    boolean npcSdbExists(int realmId);


    CreateNpcSdb loadObject(int realmId);

    boolean objectSdbExists(int realmId);

}
