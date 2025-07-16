package org.y1000.network;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.y1000.ServerContext;
import org.y1000.message.I2ClientMessage;
import org.y1000.network.gen.ClientPacket;
import org.y1000.realm.RealmManager;

@Slf4j
public final class ConnectionImpl extends AbstractConnection {

    public ConnectionImpl(RealmManager realmManager, ServerContext serverContext) {
        super(realmManager, serverContext);
    }

    @Override
    public void writeAndFlush(I2ClientMessage message) {
        var context = getContext();
        if (context == null) {
            return;
        }
        context.channel().writeAndFlush(message);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof ClientPacket packet) {
            try {
                var message = createMessage(packet);
                if (message != null) {
                    getRealmManager().queueEvent(ConnectionEvent.Data(this, message));
                }
            } catch (Exception e) {
                log.error("Exception ", e);
            }
        }
    }
}
