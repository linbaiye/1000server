package org.y1000.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.y1000.account.AccountManager;
import org.y1000.item.EquipmentType;
import org.y1000.message.clientevent.*;
import org.y1000.message.clientevent.chat.ClientChatEvent;
import org.y1000.network.event.ConnectionDataEvent;
import org.y1000.network.event.ConnectionEstablishedEvent;
import org.y1000.network.gen.ClientPacket;
import org.y1000.util.Coordinate;

public class AccountConnectionHandler extends ChannelInboundHandlerAdapter {

    private final AccountManager accountManager;

    public AccountConnectionHandler(AccountManager accountManager) {
        this.accountManager = accountManager;
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ClientPacket packet) {
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (!ctx.channel().isActive()) {
            ctx.close();
        }
    }
}
