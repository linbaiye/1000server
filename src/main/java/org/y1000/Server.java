package org.y1000;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldPrepender;
import org.y1000.connection.DevelopingConnection;
import org.y1000.connection.LengthBasedMessageDecoder;
import org.y1000.connection.MessageEncoder;
import org.y1000.realm.Realm;

import java.util.Optional;

public class Server {

    private final ServerBootstrap bootstrap;

    private final int port;

    private final EventLoopGroup workerGroup;

    private final EventLoopGroup serverGroup;

    private Realm realm;


    public Server(int port) {
        this.port = port;
        workerGroup = new NioEventLoopGroup();
        serverGroup = new NioEventLoopGroup();
        bootstrap = new ServerBootstrap();
    }


    private void startRealms() {
        Optional<Realm> realmOptional = Realm.create("start");
        if (realmOptional.isPresent()) {
            realm = realmOptional.get();
            new Thread(realm).start();
        } else {
            throw new IllegalArgumentException();
        }
    }

    private void startNetworking() {
        try {
            bootstrap.group(serverGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 4096)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel channel) throws Exception {
                            channel.pipeline()
                                    .addLast("packetDecoder", new LengthBasedMessageDecoder())
                                    .addLast("packetHandler", new DevelopingConnection(realm))
                                    .addLast("packetLengthAppender", new LengthFieldPrepender(4))
                                    .addLast("packetEncoder", MessageEncoder.ENCODER);
                        }
                    });
            bootstrap.bind(port).sync().channel().closeFuture().sync();
        } catch (Exception e) {
            workerGroup.shutdownGracefully();
            serverGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        Server server = new Server(9999);
        server.startRealms();
        server.startNetworking();;
    }
}