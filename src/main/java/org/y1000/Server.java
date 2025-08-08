package org.y1000;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.metamodel.Metamodel;
import lombok.extern.slf4j.Slf4j;
import org.y1000.account.AccountManager;
import org.y1000.entities.creatures.npc.NpcFactory;
import org.y1000.entities.creatures.npc.NpcFactoryImpl;
import org.y1000.entities.objects.DynamicObjectFactory;
import org.y1000.entities.objects.DynamicObjectFactoryImpl;
import org.y1000.item.BankInventory;
import org.y1000.item.ItemFactory;
import org.y1000.item.ItemSdbImpl;
import org.y1000.kungfu.KungFuSdb;
import org.y1000.realm.*;
import org.y1000.repository.*;
import org.y1000.network.*;
import org.y1000.sdb.ActionSdb;
import org.y1000.sdb.*;

import java.util.Map;

@Slf4j
public final class Server implements ServerContext {

    private final ServerBootstrap gameServer;


    private EventLoopGroup workerGroup;

    private EventLoopGroup bossGroup;

    private PlayerRepository playerRepository;

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

    private static class TestEntityManager implements EntityManagerFactory {

        @Override
        public EntityManager createEntityManager() {
            return null;
        }

        @Override
        public EntityManager createEntityManager(Map map) {
            return null;
        }

        @Override
        public EntityManager createEntityManager(SynchronizationType synchronizationType) {
            return null;
        }

        @Override
        public EntityManager createEntityManager(SynchronizationType synchronizationType, Map map) {
            return null;
        }

        @Override
        public CriteriaBuilder getCriteriaBuilder() {
            return null;
        }

        @Override
        public Metamodel getMetamodel() {
            return null;
        }

        @Override
        public boolean isOpen() {
            return false;
        }

        @Override
        public void close() {

        }

        @Override
        public Map<String, Object> getProperties() {
            return null;
        }

        @Override
        public Cache getCache() {
            return null;
        }

        @Override
        public PersistenceUnitUtil getPersistenceUnitUtil() {
            return null;
        }

        @Override
        public void addNamedQuery(String name, Query query) {

        }

        @Override
        public <T> T unwrap(Class<T> cls) {
            return null;
        }

        @Override
        public <T> void addNamedEntityGraph(String graphName, EntityGraph<T> entityGraph) {

        }
    }

    private static boolean Dev = true;


    public Server() {
        workerGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());
        bossGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());
        gameServer = new ServerBootstrap();
        accountServer = new ServerBootstrap();
        managementServer = new ServerBootstrap();
        entityManagerFactory = Dev ? new TestEntityManager() : Persistence.createEntityManagerFactory("org.y1000");
        KungFuBookRepositoryImpl kungFuRepositoryImpl = new KungFuBookRepositoryImpl(entityManagerFactory);
        ItemRepositoryImpl repository = new ItemRepositoryImpl(ItemSdbImpl.INSTANCE, ItemDrugSdbImpl.INSTANCE, kungFuRepositoryImpl, entityManagerFactory);
        itemRepository = repository;
        npcFactory = new NpcFactoryImpl(ActionSdb.INSTANCE, MonstersSdbImpl.INSTANCE, KungFuSdb.INSTANCE,
                NonMonsterNpcSdbImpl.Instance, MagicParamSdb.INSTANCE, ItemSdbImpl.INSTANCE, itemRepository, QuestSdbImpl.Instance, Dev ? new BankDevRepository() : itemRepository);
        dynamicObjectFactory = new DynamicObjectFactoryImpl(DynamicObjectSdbImpl.INSTANCE);
        GuildRepository guildRepository = new GuildRepositoryImpl(entityManagerFactory);
        PlayerRepositoryImpl factory = new PlayerRepositoryImpl(repository, kungFuRepositoryImpl, kungFuRepositoryImpl, entityManagerFactory, itemRepository, guildRepository);
        playerRepository = Dev ? new PlayerDevRepository(factory, itemRepository) :
        new PlayerRepositoryImpl(repository, kungFuRepositoryImpl, kungFuRepositoryImpl, entityManagerFactory, itemRepository, guildRepository);
        RealmFactory realmFactory = new RealmFactoryImpl(repository, npcFactory, ItemSdbImpl.INSTANCE, MonstersSdbImpl.INSTANCE,
                MapSdbImpl.INSTANCE, RealmSpecificSdbRepositoryImpl.INSTANCE, dynamicObjectFactory, CreateGateSdbImpl.INSTANCE,
                entityManagerFactory, playerRepository, repository, PosByDieImpl.INSTANCE, guildRepository, itemRepository, kungFuRepositoryImpl);
        accountRepository = new AccountRepositoryImpl();
        accountManager = new AccountManager(entityManagerFactory, accountRepository, playerRepository, factory);
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
                                .addLast("packetHandler", new DevelopingConnection(realmManager, Server.this))
                                //.addLast("packetHandler", new ConnectionImpl(realmManager, Server.this))
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