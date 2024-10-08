package org.y1000.sdb;

import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public final class RealmSpecificSdbRepositoryImpl implements RealmSpecificSdbRepository {

    public static final RealmSpecificSdbRepositoryImpl INSTANCE = new RealmSpecificSdbRepositoryImpl();
    private RealmSpecificSdbRepositoryImpl() {}

    @Override
    public CreateNpcSdb loadCreateMonster(int realmId) {
        return new CreateMonsterSdbImpl(realmId);
    }

    @Override
    public boolean monsterSdbExists(int realmId) {
        return exists(CreateMonsterSdbImpl.makeFileName(realmId));
    }

    private boolean exists(String fileName) {
        var path = AbstractCSVSdbReader.SDB_PATH + "/" + AbstractCreateEntitySdb.SETTING_PATH + "/" + fileName;
        return getClass().getResource(path) != null;
    }


    @Override
    public CreateNonMonsterSdb loadCreateNpc(int realmId) {
        return new CreateNpcSdbImpl(realmId);
    }

    @Override
    public boolean npcSdbExists(int realmId) {
        String fileName = CreateNpcSdbImpl.makeFileName(realmId);
        return exists(fileName);
    }

    @Override
    public CreateDynamicObjectSdb loadCreateObject(int realmId) {
        return new CreateDynamicObjectSdbImpl(realmId);
    }

    @Override
    public boolean objectSdbExists(int realmId) {
        String fileName = CreateDynamicObjectSdbImpl.makeFileName(realmId);
        return getClass().getResource(AbstractCSVSdbReader.SDB_PATH + "/" + fileName) != null;
    }


    @Override
    public HaveItemSdb loadHaveItem(int realmId) {
        return exists(HaveItemSdbImpl.makeFileName(realmId)) ? new HaveItemSdbImpl(realmId) : EmptyHaveItemSdb.INSTANCE;
    }

    @Override
    public Optional<NpcDialogSdb> loadDialog(String name) {
        if (name == null) {
            return Optional.empty();
        }
        var path = NpcDialogSdbImpl.path(name);
        return getClass().getResource(path) != null ?
                Optional.of(new NpcDialogSdbImpl(name)) : Optional.empty();
    }
}
