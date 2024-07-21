package org.y1000.sdb;


public final class CreateMonsterSdbImpl extends AbstractCreateNpcSdb {

    public CreateMonsterSdbImpl(int realmId) {
        super(makeFileName(realmId));
    }

    public static String makeFileName(int realmId) {
        return "CreateMonster" + realmId + ".sdb";
    }

    @Override
    protected String parseName(String id) {
        return get(id, "MonsterName");
    }

}
