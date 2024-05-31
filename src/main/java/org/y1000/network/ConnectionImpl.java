package org.y1000.network;

import lombok.extern.slf4j.Slf4j;
import org.y1000.repository.PlayerRepository;
import org.y1000.message.ServerMessage;
import org.y1000.realm.RealmManager;

@Slf4j
public final class ConnectionImpl extends AbstractConnection {

    public ConnectionImpl(PlayerRepository playerRepository, RealmManager realmManager) {
        super(realmManager, playerRepository);
    }


    @Override
    public void write(ServerMessage message) {
        var context = getContext();
        if (context == null) {
            return;
        }
        context.channel().write(message);
    }



    @Override
    public void writeAndFlush(ServerMessage message) {
        write(message);
        flush();
    }

    @Override
    public void flush() {
        var context = getContext();
        if (context == null) {
            return;
        }
        context.channel().flush();
    }

    @Override
    public long id() {
        return 0;
    }
}
