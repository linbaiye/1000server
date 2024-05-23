package org.y1000.entities.repository;

import org.y1000.entities.item.ItemType;
import org.y1000.entities.item.StackItem;
import org.y1000.entities.item.Weapon;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.entities.players.kungfu.KungFuBook;
import org.y1000.entities.players.kungfu.attack.AttackKungFu;
import org.y1000.entities.players.kungfu.attack.AttackKungFuType;
import org.y1000.entities.players.kungfu.attack.BladeKungFu;
import org.y1000.util.Coordinate;

public final class PlayerRepositoryImpl implements PlayerRepository {

    private static final int[] slots = new int[]{-1, -1, 1,-1,-1,-1,-1,-1,-1,-1};

    private int findSlot() {
        for (int i = 0; i < slots.length; i++) {
            if (slots[i] == -1) {
                slots[i] = 0;
                return i;
            }
        }
        return 0;
    }

    private Inventory loadInventory() {
        Inventory inventory = new Inventory();
        inventory.add(Weapon.builder()
                .attackKungFuType(AttackKungFuType.SWORD)
                .name("长剑").build());
        inventory.add(Weapon.builder()
                .attackKungFuType(AttackKungFuType.BOW)
                .name("木弓").build());
        inventory.add(StackItem.builder()
                .name("箭").number(10000)
                .build());

        return inventory;
    }


    private Weapon weapon() {
        return Weapon
                .builder()
                .name("长刀")
                .attackKungFuType(AttackKungFuType.BLADE)
                .build();
    }

    private AttackKungFu loadKungFu(Weapon weapon) {
        return BladeKungFu.builder()
                .level(85)
                .attackSpeed(35)
                .recovery(50)
                .name("无名刀法")
                .bodyArmor(1)
                .bodyDamage(1)
                .build();
    }


    @Override
    public Player load(String token) {
        int slot = findSlot();
        Weapon weapon = weapon();
        return PlayerImpl.builder()
                .id(slot)
                .name("杨过")
                .coordinate(new Coordinate(39 + slot, 27))
                .weapon(weapon)
                .attackKungFu(loadKungFu(weapon))
                .kungFuBook(KungFuBook.newInstance())
                .inventory(loadInventory())
                .build();
    }

    @Override
    public void save(Player player) {
        slots[(int)player.id()] = -1;
    }

}
