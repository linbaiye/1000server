package org.y1000.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.y1000.message.clientevent.chat.ClientChatEvent;
import org.y1000.item.EquipmentType;
import org.y1000.message.clientevent.*;
import org.y1000.network.event.ConnectionClosedEvent;
import org.y1000.network.event.ConnectionDataEvent;
import org.y1000.network.gen.ClientPacket;
import org.y1000.realm.RealmManager;
import org.y1000.util.Coordinate;

import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public abstract class AbstractConnection extends ChannelInboundHandlerAdapter implements Connection {

    private final AtomicReference<ChannelHandlerContext> context;

    private final RealmManager realmManager;

    public AbstractConnection(RealmManager realmManager) {
        this.realmManager = realmManager;
        context = new AtomicReference<>();
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
            case TOGGLEKUNGFU -> new ClientToggleKungFuEvent(clientPacket.getToggleKungFu().getTab(), clientPacket.getToggleKungFu().getSlot());
            case SITDOWN -> new ClientSitDownEvent(new Coordinate(clientPacket.getSitDown().getX(), clientPacket.getSitDown().getY()));
            case STANDUP -> ClientStandUpEvent.INSTANCE;
            case SELLITEMS -> ClientSellEvent.fromPacket(clientPacket.getSellItems());
            case BUYITEMS -> ClientBuyItemsEvent.fromPacket(clientPacket.getBuyItems());
            case RIGHTCLICK -> ClientRightClickEvent.fromPacket(clientPacket.getRightClick());
            case TRADEREQUEST -> new ClientTradePlayerEvent(clientPacket.getTradeRequest().getTargetId(), clientPacket.getTradeRequest().getSlot());
            case UPDATETRADE -> ClientUpdateTradeEvent.fromPacket(clientPacket.getUpdateTrade());
            case TRIGGERDYNAMICOBJECT -> new ClientTriggerDynamicObjectEvent(clientPacket.getTriggerDynamicObject().getId(), clientPacket.getTriggerDynamicObject().getUseSlot());
            case SWAPKUNGFUSLOT -> new ClientSwapKungFuSlotEvent(clientPacket.getSwapKungFuSlot().getPage(), clientPacket.getSwapKungFuSlot().getSlot1(), clientPacket.getSwapKungFuSlot().getSlot2());
            case DRAGPLAYER -> new ClientDragPlayerEvent(clientPacket.getDragPlayer().getTargetId(), clientPacket.getDragPlayer().getRopeSlot());
            case SIMPLECOMMAND -> ClientSimpleCommandEvent.parse(clientPacket.getSimpleCommand().getCommand());
            case DYE -> new ClientDyeEvent(clientPacket.getDye().getDyedSlotId(), clientPacket.getDye().getDyeSlotId());
            case SAY -> ClientChatEvent.create(clientPacket.getSay().getText());
            case BANKOPERATION -> ClientOperateBankEvent.fromPacket(clientPacket.getBankOperation());
            case CHANGETEAM -> new ClientChangeTeamEvent(clientPacket.getChangeTeam().getTeamNumber());
            case CLICKPACKET -> new ClientClickEvent(clientPacket.getClickPacket().getId());
            default -> throw new IllegalArgumentException();
        };
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ClientPacket packet) {
            try {
                var message = createMessage(packet);
                if (message == null) {
                    // Not something we can deal with.
                    return;
                }
                realmManager.queueEvent(new ConnectionDataEvent(this, message));
                //log.debug("Received message {}.", message);
            } catch (Exception e) {
                log.error("Exception ", e);
            }
        }
    }

    ChannelHandlerContext getContext() {
        return context.get();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
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
