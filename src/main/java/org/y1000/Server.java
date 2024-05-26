package org.y1000;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldPrepender;
import org.y1000.item.ItemConfig;
import org.y1000.entities.repository.ItemRepository;
import org.y1000.entities.repository.ItemRepositoryImpl;
import org.y1000.network.*;
import org.y1000.entities.repository.PlayerRepository;
import org.y1000.entities.repository.PlayerRepositoryImpl;
import org.y1000.realm.RealmManager;

public final class Server {

    private final ServerBootstrap bootstrap;

    private final int port;

    private EventLoopGroup workerGroup;

    private EventLoopGroup serverGroup;

    private PlayerRepository playerRepository;

    private RealmManager realmManager;

    private ItemRepository itemRepository;

    public Server(int port) {
        this.port = port;
        workerGroup = new NioEventLoopGroup();
        serverGroup = new NioEventLoopGroup();
        bootstrap = new ServerBootstrap();

        ItemRepositoryImpl repository = new ItemRepositoryImpl(ItemConfig.read());
        playerRepository = new PlayerRepositoryImpl(repository);
        itemRepository = repository;
        realmManager = RealmManager.create(repository, itemRepository);
    }


    private ItemRepository createItemRepository() {
        return null;
    }

    private void create() {
        ItemRepository itemRepository = new ItemRepositoryImpl(ItemConfig.read());
    }

//    private void startRealms() {
//        Optional<Realm> realmOptional = Realm.create("start", createPlayerRepository());
//        if (realmOptional.isPresent()) {
//            realm = realmOptional.get();
//            new Thread(realm).start();
//        } else {
//            throw new IllegalArgumentException();
//        }
//    }

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
                                    .addLast("packetHandler", new DevelopingConnection(playerRepository, realmManager))
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

    private void startRealms() {
        realmManager.startRealms();
    }

    public void loopEvent() {
        new Thread(realmManager).start();
    }

    public static void main(String[] args) {
        Server server = new Server(9999);
        server.startRealms();
        server.loopEvent();
        server.startNetworking();
    }
}