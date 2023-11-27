package org.y1000;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import org.y1000.connection.ConnectionImpl;
import org.y1000.connection.LengthBasedMessageDecoder;
import org.y1000.connection.gen.Message;
import org.y1000.connection.gen.ShowCreaturePacket;

public class Server {


    public static void main(String[] args) {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        EventLoopGroup serverGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap(); // (2)
            bootstrap.group(serverGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // (3)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel channel) throws Exception {
                            channel.pipeline()
                                    .addLast("packetDecoder", new LengthBasedMessageDecoder())
                                    .addLast("packetHandler", new ConnectionImpl());
                        }
                    });
            bootstrap.bind(9999).sync().channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}