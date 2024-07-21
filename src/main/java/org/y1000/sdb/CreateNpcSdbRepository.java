package org.y1000.sdb;

public interface CreateNpcSdbRepository {

    CreateNpcSdb loadMonster(int realmId);
    boolean monsterSdbExists(int realmId);

    CreateNpcSdb loadNpc(int realmId);

    boolean npcSdbExists(int realmId);

}
