package org.y1000.entities.repository;

import org.y1000.item.Chest;
import org.y1000.item.Hat;
import org.y1000.item.ItemFactory;
import org.y1000.item.Weapon;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.entities.players.kungfu.KungFuBook;
import org.y1000.entities.players.kungfu.attack.AttackKungFu;
import org.y1000.entities.players.kungfu.attack.BladeKungFu;
import org.y1000.util.Coordinate;

public final class PlayerRepositoryImpl implements PlayerRepository {

    private static final int[] slots = new int[]{-1, -1, 1,-1,-1,-1,-1,-1,-1,-1};

    private static final long playerIdStart = 1000000000;

    private final ItemFactory itemFactory;

    public PlayerRepositoryImpl(ItemFactory itemFactory) {
        this.itemFactory = itemFactory;
    }

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
        inventory.pick(itemFactory.createItem("长剑"));
        inventory.pick(itemFactory.createItem("木弓"));
        inventory.pick(itemFactory.createItem("箭", 10000));
        return inventory;
    }

    private Weapon weapon() {
        return (Weapon) itemFactory.createItem("长刀");
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
                .id(slot + playerIdStart)
                .name("杨过")
                .coordinate(new Coordinate(39 + slot, 27))
                .weapon(weapon)
                .attackKungFu(loadKungFu(weapon))
                .kungFuBook(KungFuBook.newInstance())
                .inventory(loadInventory())
                .male(false)
                .hat(new Hat("女子雨中客斗笠"))
                .chest(new Chest("女子雨中客道袍"))
                .build();
    }

    @Override
    public void save(Player player) {
        slots[(int)player.id()] = -1;
    }

}
