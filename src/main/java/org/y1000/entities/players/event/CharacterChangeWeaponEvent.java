package org.y1000.entities.players.event;

import org.y1000.entities.item.Item;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.kungfu.attack.AttackKungFu;
import org.y1000.entities.players.kungfu.attack.AttackKungFuType;
import org.y1000.message.ChangeWeaponEvent;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.network.gen.CharacterChangeWeaponPacket;
import org.y1000.network.gen.Packet;

public final class CharacterChangeWeaponEvent extends AbstractPlayerEvent {
    private final int affectedSlot;
    private final String slotNewItemName;

    private final int slotnewItemType;
    private final String weaponName;

    private final String currentKungFuName;

    private final int currentKungFuLevel;

    private final AttackKungFuType currentKungFuType;

    public CharacterChangeWeaponEvent(Player source, int slot, Item item, String name, AttackKungFu attackKungFu) {
        super(source);
        weaponName = name;
        this.affectedSlot = slot;
        this.slotNewItemName = item != null ? item.name() : null;
        this.slotnewItemType = item != null ? item.type().value() : 0;
        this.currentKungFuName = attackKungFu.name();
        this.currentKungFuLevel = attackKungFu.level();
        this.currentKungFuType = attackKungFu.getType();
    }

    public CharacterChangeWeaponEvent(Player source, int slot, Item item, String name) {
        super(source);
        weaponName = name;
        this.affectedSlot = slot;
        this.slotNewItemName = item != null ? item.name() : null;
        this.slotnewItemType = item != null ? item.type().value() : 0;
        this.currentKungFuName = null;
        this.currentKungFuLevel = 0;
        this.currentKungFuType = null;
    }

    @Override
    protected void accept(PlayerEventVisitor playerEventHandler) {
        playerEventHandler.visit(this);
    }

    public ChangeWeaponEvent packetForOtherPlayers() {
        return new ChangeWeaponEvent(source().id(), weaponName);
    }

    @Override
    protected Packet buildPacket() {
        CharacterChangeWeaponPacket.Builder builder = CharacterChangeWeaponPacket.newBuilder()
                .setName(weaponName)
                .setAffectedSlot(affectedSlot);
        if (slotNewItemName != null) {
            builder.setSlotNewItemName(slotNewItemName)
                    .setSlotNewItemType(slotnewItemType);
        }
        if (currentKungFuName != null) {
            builder.setAttackKungFuLevel(currentKungFuLevel)
                    .setAttackKungFuName(currentKungFuName)
                    .setAttackKungFuType(currentKungFuType.value());
        }
        return Packet.newBuilder().setCharacterChangeWeaponPacket(builder.build()).build();
    }
}
