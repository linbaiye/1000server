package org.y1000.realm;

import org.y1000.message.input.chat.ClientInputTextEvent;
import org.y1000.realm.event.RealmEvent;

interface ChatManager {

    void handleClientChat(long from, ClientInputTextEvent clientInputTextEvent);


    void handleCrossRealmChat(RealmEvent realmEvent);

}
