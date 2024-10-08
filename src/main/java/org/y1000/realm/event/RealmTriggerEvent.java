package org.y1000.realm.event;

public record RealmTriggerEvent(int toRealmId, String toName) implements IdentityRealmEvent {


}
