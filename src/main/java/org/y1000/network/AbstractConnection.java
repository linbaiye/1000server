package org.y1000.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.Direction;
import org.y1000.entities.players.equipment.EquipmentType;
import org.y1000.account.CreateCharacterRequest;
import org.y1000.account.LoginAccountRequest;
import org.y1000.account.LoginCharacterRequest;
import org.y1000.account.RegisterAccountRequest;
import org.y1000.input.*;
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

    RealmManager getRealmManager() {
        return realmManager;
    }

    Object createMessage(ClientPacket clientPacket) {
        return switch (clientPacket.getTypeCase()) {

            case LOGINCHARACTER -> new LoginCharacterRequest(clientPacket.getLoginCharacter().getCharacterName());
            case LOGINACCOUNT -> new LoginAccountRequest(clientPacket.getLoginAccount().getUsername(), clientPacket.getLoginAccount().getPassword());
            case REGISTERACCOUNT -> new RegisterAccountRequest(clientPacket.getRegisterAccount().getUsername(), clientPacket.getRegisterAccount().getPassword());
            case CREATECHARACTER -> new CreateCharacterRequest(clientPacket.getCreateCharacter().getCharacterName(), clientPacket.getCreateCharacter().getMale());

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
            case SUBMITQUESTINPUT -> new SubmitQuestInput(clientPacket.getSubmitQuestInput().getId(), clientPacket.getSubmitQuestInput().getQuestName());
            case UNLOCKBANK -> new UnlockBankInput(clientPacket.getUnlockBank().getNpcId());
            case BANKOPERATION -> BankOperationInput.fromPacket(clientPacket.getBankOperation());
            case REALMINPUT -> RealmInput.of(clientPacket.getRealmInput().getType());
            case CREATEGUILDINPUT -> CreateGuildInput.fromPacket(clientPacket.getCreateGuildInput());
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
