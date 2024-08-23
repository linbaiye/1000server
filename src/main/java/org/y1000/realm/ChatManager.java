package org.y1000.realm;

import org.y1000.message.clientevent.chat.ClientChatEvent;
import org.y1000.realm.event.RealmEvent;

interface ChatManager {

    void handleClientChat(long from, ClientChatEvent clientChatEvent);


    void handleCrossRealmChat(RealmEvent realmEvent);

}
