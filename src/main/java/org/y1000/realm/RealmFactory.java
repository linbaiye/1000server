package org.y1000.realm;

public interface RealmFactory {

    Realm createRealm(int id, RealmEventHandler crossRealmEventHandler);
}
