package org.y1000.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.y1000.ServerContext;
import org.y1000.entities.Direction;
import org.y1000.message.input.chat.ClientInputTextEvent;
import org.y1000.item.EquipmentType;
import org.y1000.message.input.*;
import org.y1000.message.input.MoveInput;
import org.y1000.message.input.TurnInput;
import org.y1000.network.gen.ClientPacket;
import org.y1000.network.gen.ClientSimpleCommandPacket;
import org.y1000.realm.RealmManager;
import org.y1000.util.Coordinate;

import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public abstract class AbstractConnection extends ChannelInboundHandlerAdapter implements Connection {

    private final AtomicReference<ChannelHandlerContext> context;

    private final RealmManager realmManager;

    private final ServerContext serverContext;

    public AbstractConnection(RealmManager realmManager,
                              ServerContext serverContext) {
        this.realmManager = realmManager;
        this.serverContext = serverContext;
        context = new AtomicReference<>();
    }

    private ClientEvent parseSimpleCommand(ClientSimpleCommandPacket packet) {
        if (packet.getCommand() == SimpleCommand.CANCEL_BUFF.value()) {
            return new CancelBuffEvent();
        } else if (packet.getCommand() == SimpleCommand.PONG.value()) {
            log.info("Received ping from.");
            return null;
        }
        return ClientSimpleCommandEvent.parse(packet.getCommand());
    }

    private Object createMessage(ClientPacket clientPacket) {
        return switch (clientPacket.getTypeCase()) {
            case MOVEEVENTPACKET -> ClientMovementEvent.fromPacket(clientPacket);
            case LOGINPACKET -> LoginEvent.fromPacket(clientPacket.getLoginPacket());
            case ATTACKEVENTPACKET -> ClientAttackEvent.fromPacket(clientPacket.getAttackEventPacket());
            case SWAPINVENTORYSLOTPACKET -> SwapInventoryItemInput.fromPacket(clientPacket.getSwapInventorySlotPacket());
            case DOUBLECLICKINVENTORYSLOTPACKET -> new ClientDoubleClickSlotEvent(clientPacket.getDoubleClickInventorySlotPacket().getSlot());
            case DROPITEM -> new ClientDropItemEvent(clientPacket.getDropItem().getNumber(), clientPacket.getDropItem().getSlot(),
                    clientPacket.getDropItem().getX(), clientPacket.getDropItem().getY(),
                    new Coordinate(clientPacket.getDropItem().getCoordinateX(), clientPacket.getDropItem().getCoordinateY()));
            case PICKITEM -> new ClientPickItemEvent(clientPacket.getPickItem().getId());
            case UNEQUIP -> new ClientUnequipEvent(EquipmentType.fromValue(clientPacket.getUnequip().getType()));
            case TOGGLEKUNGFU -> new ClientToggleKungFuEvent(clientPacket.getToggleKungFu().getTab(), clientPacket.getToggleKungFu().getSlot());
            case SITDOWN -> new ClientSitDownEvent(new Coordinate(clientPacket.getSitDown().getX(), clientPacket.getSitDown().getY()));
            case STANDUP -> ClientStandUpEvent.INSTANCE;
            case SELLITEMS -> ClientSellEvent.fromPacket(clientPacket.getSellItems(), serverContext.getItemFactory());
            case BUYITEMS -> ClientBuyItemsEvent.fromPacket(clientPacket.getBuyItems(), serverContext.getItemFactory());
            case RIGHTCLICK -> ClientRightClickEvent.fromPacket(clientPacket.getRightClick());
            case TRADEREQUEST -> new ClientTradePlayerEvent(clientPacket.getTradeRequest().getTargetId(), clientPacket.getTradeRequest().getSlot());
            case UPDATETRADE -> ClientUpdateTradeEvent.fromPacket(clientPacket.getUpdateTrade());
            case TRIGGERDYNAMICOBJECT -> new ClientTriggerDynamicObjectEvent(clientPacket.getTriggerDynamicObject().getId(), clientPacket.getTriggerDynamicObject().getUseSlot());
            case SWAPKUNGFUSLOT -> new ClientSwapKungFuSlotEvent(clientPacket.getSwapKungFuSlot().getPage(), clientPacket.getSwapKungFuSlot().getSlot1(), clientPacket.getSwapKungFuSlot().getSlot2());
            case DRAGPLAYER -> new ClientDragPlayerEvent(clientPacket.getDragPlayer().getTargetId(), clientPacket.getDragPlayer().getRopeSlot());
            case SIMPLECOMMAND -> parseSimpleCommand(clientPacket.getSimpleCommand());
            case DYE -> new ClientDyeEvent(clientPacket.getDye().getDyedSlotId(), clientPacket.getDye().getDyeSlotId());
            case SAY -> ClientInputTextEvent.create(clientPacket.getSay().getText());
            case BANKOPERATION -> ClientOperateBankEvent.fromPacket(clientPacket.getBankOperation());
            case CHANGETEAM -> new ClientChangeTeamEvent(clientPacket.getChangeTeam().getTeamNumber());
            case CLICKPACKET -> new ClientClickEvent(clientPacket.getClickPacket().getId());
            case FOUNDGUILD -> ClientFoundGuildEvent.parse(clientPacket.getFoundGuild());
            case CREATEGUILDKUNGFU -> ClientCreateGuildKungFuEvent.parse(clientPacket.getCreateGuildKungFu());
            case MANAGEGUILD -> new ClientManageGuildEvent(clientPacket.getManageGuild().getType(), clientPacket.getManageGuild().getTarget());
            case SUBMITQUEST -> new ClientSubmitQuestEvent(clientPacket.getSubmitQuest().getId(), clientPacket.getSubmitQuest().getQuestName(), serverContext.getItemFactory());
            case INTERACT -> new ClientClickInteractabilityEvent(clientPacket.getInteract().getId(), clientPacket.getInteract().getName());
            case DEBUG -> new DebugInput(1);
            case MOVEINPUT -> new MoveInput(Coordinate.xy(clientPacket.getMoveInput().getX(), clientPacket.getMoveInput().getY()), Direction.fromValue(clientPacket.getMoveInput().getDirection()));
            case TURNINPUT -> new TurnInput(Direction.fromValue(clientPacket.getTurnInput().getDirection()));
            case SIMPLEINPUT -> SimpleInput.fromValue(clientPacket.getSimpleInput().getType());
            case CLICKKUNGFUINPUT -> ClickKungFuInput.fromPacket(clientPacket.getClickKungFuInput());
            case CLICKINVENTORYSLOTINPUT -> ClickInventorySlotInput.fromPacket(clientPacket.getClickInventorySlotInput());
            case ATTACKINPUT -> new AttackInput(clientPacket.getAttackInput().getId());
            default -> null;
        };
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof ClientPacket packet) {
            try {
                var message = createMessage(packet);
                if (message != null) {
                    realmManager.queueEvent(ConnectionEvent.Data(this, message));
                }
            } catch (Exception e) {
                log.error("Exception ", e);
            }
        }
    }

    ChannelHandlerContext getContext() {
        return context.get();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        realmManager.queueEvent(ConnectionEvent.Close(this));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (!ctx.channel().isActive()) {
            context.get().close();
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        context.set(ctx);
    }

    @Override
    public void tryClose() {
        try {
            if (context.get().channel().isActive())
                context.get().channel().close();
        } catch (Exception e) {
            //ignored.
        }
    }
}
