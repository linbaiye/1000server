package org.y1000.sdb;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class CreateEntitySdbRepositoryImpl implements CreateEntitySdbRepository {
    public static final CreateEntitySdbRepositoryImpl INSTANCE = new CreateEntitySdbRepositoryImpl();
    private CreateEntitySdbRepositoryImpl() {}

    @Override
    public CreateNpcSdb loadMonster(int realmId) {
        return new CreateMonsterSdbImpl(realmId);
    }

    @Override
    public boolean monsterSdbExists(int realmId) {
        return exists(CreateMonsterSdbImpl.makeFileName(realmId));
    }

    private boolean exists(String fileName) {
        var path = AbstractSdbReader.SDB_PATH + "/" + AbstractCreateEntitySdb.SETTING_PATH + "/" + fileName;
        return getClass().getResource(path) != null;
    }

    @Override
    public CreateNpcSdb loadNpc(int realmId) {
        return new CreateNpcSdbImpl(realmId);
    }

    @Override
    public boolean npcSdbExists(int realmId) {
        String fileName = CreateNpcSdbImpl.makeFileName(realmId);
        return exists(fileName);
    }

    @Override
    public CreateNpcSdb loadObject(int realmId) {
        return null;
    }

    @Override
    public boolean objectSdbExists(int realmId) {
        return false;
    }
}
