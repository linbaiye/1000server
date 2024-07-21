package org.y1000.sdb;

public final class CreateNpcSdbImpl extends AbstractCreateNpcSdb {

    public CreateNpcSdbImpl(int realmId) {
        super(makeFileName(realmId));
    }

    public static String makeFileName(int id) {
        return "CreateNpc" + id + ".sdb";
    }

    @Override
    protected String parseName(String id) {
        return get(id, "NpcName");
    }
}

