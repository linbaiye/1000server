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
import org.y1000.item.ItemFactory;
import org.y1000.item.ItemSdbImpl;
import org.y1000.kungfu.KungFuSdb;
import org.y1000.realm.*;
import org.y1000.repository.*;
import org.y1000.network.*;
import org.y1000.sdb.ActionSdb;
import org.y1000.sdb.*;

@Slf4j
public final class Server implements ServerContext {

    private final ServerBootstrap gameServer;


    private EventLoopGroup workerGroup;

    private EventLoopGroup bossGroup;

    private PlayerRepositoryImpl playerRepository;

    private RealmManager realmManager;

    private final ItemRepositoryImpl itemRepository;

    private DynamicObjectFactory dynamicObjectFactory;

    private NpcFactory npcFactory;

    private final EntityManagerFactory entityManagerFactory;

    private static final int port = 9999;
    private static final int accountPort = 9901;

    private static final int managementPort = 9902;

    private final ServerBootstrap accountServer;

    private final AccountManager accountManager;

    private final AccountRepository accountRepository;

    private Channel gameSercerChannel;
    private Channel accountChannel;
    private Channel managmentChannel;
    private ServerBootstrap managementServer;

    private boolean shutdown;


    public Server() {
        workerGroup = new NioEventLoopGroup();
        bossGroup = new NioEventLoopGroup();
        gameServer = new ServerBootstrap();
        accountServer = new ServerBootstrap();
        managementServer = new ServerBootstrap();
        entityManagerFactory = Persistence.createEntityManagerFactory("org.y1000");
        KungFuBookRepositoryImpl kungFuRepositoryImpl = new KungFuBookRepositoryImpl(entityManagerFactory);
        ItemRepositoryImpl repository = new ItemRepositoryImpl(ItemSdbImpl.INSTANCE, ItemDrugSdbImpl.INSTANCE, kungFuRepositoryImpl, entityManagerFactory);
        itemRepository = repository;
        npcFactory = new NpcFactoryImpl(ActionSdb.INSTANCE, MonstersSdbImpl.INSTANCE, KungFuSdb.INSTANCE,
                NpcSdbImpl.Instance, MagicParamSdb.INSTANCE, new MerchantItemSdbRepositoryImpl(ItemSdbImpl.INSTANCE), RealmSpecificSdbRepositoryImpl.INSTANCE);
        dynamicObjectFactory = new DynamicObjectFactoryImpl(DynamicObjectSdbImpl.INSTANCE);
        GuildRepository guildRepository = new GuildRepositoryImpl(entityManagerFactory);
        playerRepository = new PlayerRepositoryImpl(repository, kungFuRepositoryImpl, kungFuRepositoryImpl, entityManagerFactory, itemRepository, guildRepository);
        RealmFactory realmFactory = new RealmFactoryImpl(repository, npcFactory, ItemSdbImpl.INSTANCE, MonstersSdbImpl.INSTANCE,
                MapSdbImpl.INSTANCE, RealmSpecificSdbRepositoryImpl.INSTANCE, dynamicObjectFactory, CreateGateSdbImpl.INSTANCE,
                entityManagerFactory, playerRepository, repository, PosByDieImpl.INSTANCE, guildRepository, itemRepository, kungFuRepositoryImpl);
        accountRepository = new AccountRepositoryImpl();
        accountManager = new AccountManager(entityManagerFactory, accountRepository, playerRepository, playerRepository);
        realmManager = RealmManager.create(MapSdbImpl.INSTANCE, realmFactory, accountManager, playerRepository);
        shutdown = false;
    }

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
                                .addLast("packetHandler", new ConnectionImpl(realmManager, Server.this))
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
                        p.addLast(new AccountConnectionHandler(accountManager));
                    }
                });
    }

    private void setupManagementServer() {
        managementServer.group(bossGroup, workerGroup)
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
                        p.addLast(new ManagementHttpConnectionHandler(realmManager, Server.this::shutdown));
                    }
                });
    }



    private void close(Channel channel) {
        if (channel != null) {
            try {
                channel.close().sync();
            } catch (Exception e) {
                // nothing to do.
            }
        }
    }

    private synchronized void shutdown() {
        if (shutdown) {
            return;
        }
        shutdown = true;
        realmManager.shut();
        close(gameSercerChannel);
        close(accountChannel);
        close(managmentChannel);
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
        entityManagerFactory.close();
        System.exit(0);
    }

    private void startNetworking() {
        try {
            setupGameServer();
            gameSercerChannel = gameServer.bind(port).sync().channel();
            setupAccountServer();
            accountChannel = accountServer.bind(accountPort).sync().channel();
            setupManagementServer();
            managmentChannel = managementServer.bind(managementPort).sync().channel();
            log.info("All servers ready.");
        } catch (Exception e) {
            log.error("Caught exception, server exit now.", e);
            System.exit(1);
        }
    }

    private void startRealms() {
        realmManager.startRealms();
    }

    public void loopEvent() {
        new Thread(realmManager).start();
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.startRealms();
        server.loopEvent();
        server.startNetworking();
    }

    @Override
    public ItemFactory getItemFactory() {
        return itemRepository;
    }
}