package org.y1000;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.extern.slf4j.Slf4j;
import org.y1000.account.AccountManager;
import org.y1000.entities.creatures.npc.NpcFactory;
import org.y1000.entities.creatures.npc.NpcFactoryImpl;
import org.y1000.entities.objects.DynamicObjectFactory;
import org.y1000.entities.objects.DynamicObjectFactoryImpl;
import org.y1000.item.ItemSdbImpl;
import org.y1000.kungfu.KungFuSdb;
import org.y1000.realm.RealmFactory;
import org.y1000.realm.RealmFactoryImpl;
import org.y1000.repository.*;
import org.y1000.network.*;
import org.y1000.realm.RealmManager;
import org.y1000.sdb.ActionSdb;
import org.y1000.sdb.*;

@Slf4j
public final class Server {

    private final ServerBootstrap gameServer;

    private final int port;

    private EventLoopGroup workerGroup;

    private EventLoopGroup bossGroup;

    private PlayerRepositoryImpl playerRepository;

    private RealmManager realmManager;

    private ItemRepository itemRepository;

    private DynamicObjectFactory dynamicObjectFactory;

    private NpcFactory npcFactory;

    private final EntityManagerFactory entityManagerFactory;

    private static final int accountPort = 9901;

    private final ServerBootstrap accountServer;

    private final AccountManager accountManager;

    private final AccountRepository accountRepository;


    public Server(int port) {
        this.port = port;
        workerGroup = new NioEventLoopGroup();
        bossGroup = new NioEventLoopGroup();
        gameServer = new ServerBootstrap();
        accountServer = new ServerBootstrap();
        entityManagerFactory = Persistence.createEntityManagerFactory("org.y1000");
        KungFuBookRepositoryImpl kungFuRepositoryImpl = new KungFuBookRepositoryImpl();
        ItemRepositoryImpl repository = new ItemRepositoryImpl(ItemSdbImpl.INSTANCE, ItemDrugSdbImpl.INSTANCE, kungFuRepositoryImpl, entityManagerFactory);
        itemRepository = repository;
        npcFactory = new NpcFactoryImpl(ActionSdb.INSTANCE, MonstersSdbImpl.INSTANCE, KungFuSdb.INSTANCE, NpcSdbImpl.Instance, MagicParamSdb.INSTANCE, new MerchantItemSdbRepositoryImpl(ItemSdbImpl.INSTANCE));
        dynamicObjectFactory = new DynamicObjectFactoryImpl(DynamicObjectSdbImpl.INSTANCE);
        playerRepository = new PlayerRepositoryImpl(repository, kungFuRepositoryImpl, kungFuRepositoryImpl, entityManagerFactory, itemRepository);
        RealmFactory realmFactory = new RealmFactoryImpl(repository, npcFactory, ItemSdbImpl.INSTANCE, MonstersSdbImpl.INSTANCE,
                MapSdbImpl.INSTANCE, CreateEntitySdbRepositoryImpl.INSTANCE, dynamicObjectFactory, CreateGateSdbImpl.INSTANCE,
                entityManagerFactory, playerRepository, repository, PosByDieImpl.INSTANCE);
        accountRepository = new AccountRepositoryImpl();
        accountManager = new AccountManager(entityManagerFactory, accountRepository, playerRepository, playerRepository);
        realmManager = RealmManager.create(MapSdbImpl.INSTANCE, realmFactory, accountManager, playerRepository);
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


    private void setupGameServer() {
        gameServer.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 4096)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel channel) throws Exception {
                        channel.pipeline()
                                .addLast("packetDecoder", new LengthBasedMessageDecoder())
                                .addLast("packetHandler", new DevelopingConnection(realmManager))
                                .addLast("packetLengthAppender", new LengthFieldPrepender(4))
                                .addLast("packetEncoder", MessageEncoder.ENCODER);
                    }
                });
    }

    private void setupAccountServer() {
        accountServer.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 4096)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel channel) throws Exception {
                        ChannelPipeline p = channel.pipeline();
                        p.addLast(new HttpRequestDecoder());
                        p.addLast(new HttpResponseEncoder());
                        p.addLast(new HttpConnectionHandler(accountManager));
                    }
                });
    }



    private void close(Channel channel) {
        if (channel != null) {
            try {
                channel.closeFuture().sync();
            } catch (Exception e) {
                // nothing to do.
            }
        }
    }

    private void startNetworking() {
        Channel gameSercerChannel = null;
        Channel accountChannel = null;
        try {
            setupGameServer();
            gameSercerChannel = gameServer.bind(port).sync().channel();
            setupAccountServer();
            accountChannel = accountServer.bind(accountPort).sync().channel();
        } catch (Exception e) {
            log.error("Caught exception, server exit now.", e);
        } finally {
            close(gameSercerChannel);
            close(accountChannel);
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
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