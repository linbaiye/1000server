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
    public CreateNonMonsterSdb loadNpc(int realmId) {
        return new CreateNpcSdbImpl(realmId);
    }

    @Override
    public boolean npcSdbExists(int realmId) {
        String fileName = CreateNpcSdbImpl.makeFileName(realmId);
        return exists(fileName);
    }

    @Override
    public CreateDynamicObjectSdb loadObject(int realmId) {
        return new CreateDynamicObjectSdbImpl(realmId);
    }

    @Override
    public boolean objectSdbExists(int realmId) {
        String fileName = CreateDynamicObjectSdbImpl.makeFileName(realmId);
        return getClass().getResource(AbstractSdbReader.SDB_PATH + "/" + fileName) != null;
    }


    @Override
    public HaveItemSdb loadHaveItem(int realmId) {
        return exists(HaveItemSdbImpl.makeFileName(realmId)) ? new HaveItemSdbImpl(realmId) : EmptyHaveItemSdb.INSTANCE;
    }
}
