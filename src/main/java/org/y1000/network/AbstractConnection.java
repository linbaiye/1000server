package org.y1000.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.repository.PlayerRepository;
import org.y1000.item.EquipmentType;
import org.y1000.message.PlayerDropItemEvent;
import org.y1000.message.clientevent.*;
import org.y1000.network.event.ConnectionClosedEvent;
import org.y1000.network.event.ConnectionDataEvent;
import org.y1000.network.event.ConnectionEstablishedEvent;
import org.y1000.network.gen.ClientPacket;
import org.y1000.realm.RealmManager;
import org.y1000.util.Coordinate;

import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public abstract class AbstractConnection extends ChannelInboundHandlerAdapter implements Connection {

    private final AtomicReference<ChannelHandlerContext> context;

    private final PlayerRepository playerRepository;

    private final RealmManager realmManager;

    public AbstractConnection(RealmManager realmManager, PlayerRepository playerRepository) {
        this.realmManager = realmManager;
        context = new AtomicReference<>();
        this.playerRepository = playerRepository;
    }

    private ClientEvent createMessage(ClientPacket clientPacket) {
        return switch (clientPacket.getTypeCase()) {
            case MOVEEVENTPACKET -> ClientMovementEvent.fromPacket(clientPacket);
            case LOGINPACKET -> LoginEvent.fromPacket(clientPacket.getLoginPacket());
            case ATTACKEVENTPACKET -> ClientAttackEvent.fromPacket(clientPacket.getAttackEventPacket());
            case SWAPINVENTORYSLOTPACKET -> ClientSwapInventoryEvent.fromPacket(clientPacket.getSwapInventorySlotPacket());
            case DOUBLECLICKINVENTORYSLOTPACKET -> new ClientDoubleClickSlotEvent(clientPacket.getDoubleClickInventorySlotPacket().getSlot());
            case DROPITEM -> new ClientDropItemEvent(clientPacket.getDropItem().getNumber(), clientPacket.getDropItem().getSlot(),
                    clientPacket.getDropItem().getX(), clientPacket.getDropItem().getY(),
                    new Coordinate(clientPacket.getDropItem().getCoordinateX(), clientPacket.getDropItem().getCoordinateY()));
            case PICKITEM -> new ClientPickItemEvent(clientPacket.getPickItem().getId());
            case UNEQUIP -> new ClientUnequipEvent(EquipmentType.fromValue(clientPacket.getUnequip().getType()));
            default -> throw new IllegalArgumentException();
        };
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ClientPacket packet) {
            try {
                var message = createMessage(packet);
                if (message instanceof LoginEvent loginEvent) {
                    var player = playerRepository.load(loginEvent.getToken());
                    realmManager.queueEvent(new ConnectionEstablishedEvent(player, this));
                } else {
                    realmManager.queueEvent(new ConnectionDataEvent(this, message));
                }
                log.debug("Received message {}.", message);
            } catch (Exception e) {
                log.error("Exception ", e);

            }
            //clientEventListeners.forEach(clientEventListener -> clientEventListener.OnEvent(message));
        }
    }

    ChannelHandlerContext getContext() {
        return context.get();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("Channel closed.");
        realmManager.queueEvent(new ConnectionClosedEvent(this));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (!ctx.channel().isActive()) {
            context.get().close();
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        context.set(ctx);
    }

    @Override
    public void close() {
        try {
            context.get().close();
        } catch (Exception e) {
            //ignored.
        }
    }
}
