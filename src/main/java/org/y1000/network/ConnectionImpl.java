package org.y1000.network;

import lombok.extern.slf4j.Slf4j;
import org.y1000.ServerContext;
import org.y1000.repository.PlayerRepository;
import org.y1000.message.ServerMessage;
import org.y1000.realm.RealmManager;

@Slf4j
public final class ConnectionImpl extends AbstractConnection {

    public ConnectionImpl(RealmManager realmManager, ServerContext serverContext) {
        super(realmManager, serverContext);
    }

    @Override
    public void write(ServerMessage message) {
        var context = getContext();
        if (context == null) {
            return;
        }
        context.channel().writeAndFlush(message);
    }

    @Override
    public void flush() {
        var context = getContext();
        if (context == null) {
            return;
        }
        context.channel().flush();
    }
}
