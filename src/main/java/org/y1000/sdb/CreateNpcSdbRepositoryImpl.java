package org.y1000.sdb;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class CreateNpcSdbRepositoryImpl implements CreateNpcSdbRepository {
    public static final CreateNpcSdbRepositoryImpl INSTANCE = new CreateNpcSdbRepositoryImpl();
    private CreateNpcSdbRepositoryImpl() {}

    @Override
    public CreateNpcSdb loadMonster(int realmId) {
        return new CreateMonsterSdbImpl(realmId);
    }

    @Override
    public boolean monsterSdbExists(int realmId) {
        return exists(CreateMonsterSdbImpl.makeFileName(realmId));
    }

    private boolean exists(String fileName) {
        var path = CreateNpcSdbImpl.SDB_PATH + "/" + CreateNpcSdbImpl.SETTING_PATH + "/" + fileName;
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
}
