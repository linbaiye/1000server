package org.y1000.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.y1000.ServerContext;
import org.y1000.entities.Direction;
import org.y1000.item.EquipmentType;
import org.y1000.message.input.*;
import org.y1000.message.input.MoveInput;
import org.y1000.message.input.TurnInput;
import org.y1000.network.gen.ClientPacket;
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

    RealmManager getRealmManager() {
        return realmManager;
    }

    Object createMessage(ClientPacket clientPacket) {
        return switch (clientPacket.getTypeCase()) {
            case LOGINPACKET -> LoginEvent.fromPacket(clientPacket.getLoginPacket());
            case PICKITEM -> new ClientPickItemEvent(clientPacket.getPickItem().getId());
            case UNEQUIP -> new ClientUnequipEvent(EquipmentType.fromValue(clientPacket.getUnequip().getType()));
            case TOGGLEKUNGFU -> new ClientToggleKungFuEvent(clientPacket.getToggleKungFu().getTab(), clientPacket.getToggleKungFu().getSlot());
            case SITDOWN -> new ClientSitDownEvent(new Coordinate(clientPacket.getSitDown().getX(), clientPacket.getSitDown().getY()));
            case STANDUP -> ClientStandUpEvent.INSTANCE;
            case RIGHTCLICK -> ClientRightClickEvent.fromPacket(clientPacket.getRightClick());
            case TRADEREQUEST -> new ClientTradePlayerEvent(clientPacket.getTradeRequest().getTargetId(), clientPacket.getTradeRequest().getSlot());
            case UPDATETRADE -> ClientUpdateTradeEvent.fromPacket(clientPacket.getUpdateTrade());
            case BANKOPERATION -> ClientOperateBankEvent.fromPacket(clientPacket.getBankOperation());
            case CHANGETEAM -> new ClientChangeTeamEvent(clientPacket.getChangeTeam().getTeamNumber());
            case FOUNDGUILD -> ClientFoundGuildEvent.parse(clientPacket.getFoundGuild());
            case CREATEGUILDKUNGFU -> ClientCreateGuildKungFuEvent.parse(clientPacket.getCreateGuildKungFu());
            case MANAGEGUILD -> new ClientManageGuildEvent(clientPacket.getManageGuild().getType(), clientPacket.getManageGuild().getTarget());
            case SUBMITQUEST -> new ClientSubmitQuestEvent(clientPacket.getSubmitQuest().getId(), clientPacket.getSubmitQuest().getQuestName(), serverContext.getItemFactory());

            case DEBUG -> new DebugInput(1);
            case SWAPINVENTORYSLOTPACKET -> SwapInventoryItemInput.fromPacket(clientPacket.getSwapInventorySlotPacket());
            case CLICKPACKET -> new ClickEntityInput(clientPacket.getClickPacket().getId());
            case MOVEINPUT -> new MoveInput(Coordinate.xy(clientPacket.getMoveInput().getX(), clientPacket.getMoveInput().getY()), Direction.fromValue(clientPacket.getMoveInput().getDirection()));
            case TURNINPUT -> new TurnInput(Direction.fromValue(clientPacket.getTurnInput().getDirection()));
            case SIMPLEINPUT -> SimpleInput.fromValue(clientPacket.getSimpleInput().getType());
            case CLICKKUNGFUINPUT -> ClickKungFuInput.fromPacket(clientPacket.getClickKungFuInput());
            case CLICKINVENTORYSLOTINPUT -> ClickInventorySlotInput.fromPacket(clientPacket.getClickInventorySlotInput());
            case ATTACKINPUT -> new AttackInput(clientPacket.getAttackInput().getId());
            case UNEQUIPINPUT -> new UnequipInput(EquipmentType.fromValue(clientPacket.getUnequipInput().getType()));
            case SWAPKUNGFUSLOTPACKET -> SwapKungFuInput.fromPacket(clientPacket.getSwapKungFuSlotPacket());
            case PICKINPUT -> new PickItemInput(clientPacket.getPickInput().getId());
            case DROPINPUT -> new DropItemInput(clientPacket.getDropInput().getSlot(), Coordinate.xy(clientPacket.getDropInput().getX(), clientPacket.getDropInput().getY()));
            case CONFIRMDROPINPUT -> ConfirmDropItemInput.fromPacket(clientPacket.getConfirmDropInput());
            case CLICKNPCABILITYINPUT -> new ClickNpcAbilityInput(clientPacket.getClickNpcAbilityInput().getId(), clientPacket.getClickNpcAbilityInput().getAbilityName());
            case BUYITEM -> new BuyItemInput(clientPacket.getBuyItem().getId(), clientPacket.getBuyItem().getName(), clientPacket.getBuyItem().getNumber());
            case SELLITEM -> new SellItemInput(clientPacket.getSellItem().getId(), clientPacket.getSellItem().getSlot(), clientPacket.getSellItem().getNumber());
            case DROPONENTITYINPUT -> new DropOnEntityInput(clientPacket.getDropOnEntityInput().getId(), clientPacket.getDropOnEntityInput().getSlot());
            case TRADESTATEINPUT -> new PlayerTradeStateInput(clientPacket.getTradeStateInput().getState());
            case ADDPLAYERTRADEINPUT -> new AddPlayerTradeItemInput(clientPacket.getAddPlayerTradeInput().getSlot(), clientPacket.getAddPlayerTradeInput().getNumber());
            case USEPILL -> new UsePillInput(clientPacket.getUsePill().getName());
            case CHAT -> new ChatInput(clientPacket.getChat().getText());
            case CLICKEQUIPMENT -> new ClickEquipmentInput(clientPacket.getClickEquipment().getEquipType());
            default -> null;
        };
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
    public void flush() {
        var context = getContext();
        if (context == null) {
            return;
        }
        context.channel().flush();
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
