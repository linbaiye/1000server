package org.y1000.repository;

import jakarta.persistence.EntityManager;
import org.apache.commons.lang3.tuple.Pair;
import org.y1000.entities.players.Player;
import org.y1000.item.ItemFactory;
import org.y1000.util.Coordinate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PlayerDevRepository implements PlayerRepository {

    private final Map<Long, Player> playerMap = new HashMap<>();
    private final Map<Long, Integer> playerRealmMap = new HashMap<>();

    private final PlayerRepositoryImpl playerFactory;

    private final ItemFactory itemFactory;

    private final List<Long> availableDebugPlayers = List.of(100000251L, 100000301L);

    public PlayerDevRepository(PlayerRepositoryImpl factory, ItemFactory itemFactory) {
        this.playerFactory = factory;
        this.itemFactory = itemFactory;
        Player male = playerFactory.create("测试男名字不能太长了", true, 100000251L);
        playerMap.put(male.id(), male);
        playerRealmMap.put(male.id(), 6);
        male.inventory().add(itemFactory.createItem("生药", 10000));
        male.inventory().add(itemFactory.createItem("丹药", 10000));
        male.inventory().add(itemFactory.createItem("丸药", 10000));
        male.inventory().add(itemFactory.createEquipment("三叉戟"));
        male.inventory().add(itemFactory.createEquipment("龙光剑"));
        male.inventory().add(itemFactory.createEquipment("男子黄金铠甲"));
        male.inventory().add(itemFactory.createEquipment("男子黄龙鞋"));
        male.inventory().add(itemFactory.createItem("黑沙刚体", 1));
        male.inventory().add(itemFactory.createItem("闪光剑破解", 1));
        male.inventory().add(itemFactory.createItem("风灵旋", 1));
        male.inventory().add(itemFactory.createItem("灵动八方", 1));
        male.inventory().add(itemFactory.createItem("壁射剑法", 1));
        male.inventory().add(itemFactory.createEquipment("驱魔烈火弓"));
        male.inventory().add(itemFactory.createEquipment("斗甲"));
        male.inventory().add(itemFactory.createEquipment("男子妖华袍"));
        male.inventory().add(itemFactory.createEquipment("男子黄金护腕"));
        male.inventory().add(itemFactory.createEquipment("月光刀"));
        male.inventory().add(itemFactory.createEquipment("狼牙戟"));
        male.inventory().add(itemFactory.createEquipment("炎帝火灵斧"));
        male.inventory().add(itemFactory.createItem("箭", 10000));
        male.inventory().add(itemFactory.createItem("飞刀", 10000));
        male.inventory().add(itemFactory.createItem("钱币", 10000));
        male.inventory().add(itemFactory.createItem("骨钥匙", 10000));
        male.inventory().add(itemFactory.createItem("火石", 10000));

        Player female = playerFactory.create("测试女", false, 100000301L);
        playerMap.put(female.id(), female);
        playerRealmMap.put(female.id(), 6);
        female.inventory().add(itemFactory.createItem("生药", 10000));
        female.inventory().add(itemFactory.createEquipment("三叉戟"));
        female.inventory().add(itemFactory.createEquipment("龙光剑"));
        female.inventory().add(itemFactory.createEquipment("女子黄金铠甲"));
        female.inventory().add(itemFactory.createEquipment("女子黄龙鞋"));
        female.inventory().add(itemFactory.createEquipment("女子黄龙手套"));
        female.inventory().add(itemFactory.createEquipment("女子长发"));
        female.inventory().add(itemFactory.createItem("黑沙刚体", 1));
        female.inventory().add(itemFactory.createItem("应龙大天神", 1));
        female.inventory().add(itemFactory.createEquipment("驱魔烈火弓"));
        female.inventory().add(itemFactory.createEquipment("斗甲"));
        female.inventory().add(itemFactory.createItem("箭", 10000));
        female.inventory().add(itemFactory.createItem("飞刀", 10000));
    }

    @Override
    public Optional<Pair<Player, Integer>> find(int accountId, String charName) {
        return Optional.empty();
    }

    @Override
    public synchronized Optional<Integer> findRealm(long id) {
        return Optional.ofNullable(playerRealmMap.get(id));
    }


    @Override
    public synchronized void update(Player player) {
        playerMap.put(player.id(), player);
        playerRealmMap.put(player.id(), player.getRealm().id());
    }

    @Override
    public Optional<Player> load(long id) {
        return Optional.ofNullable(playerMap.get(id));
    }

    @Override
    public long save(EntityManager entityManager, int accountId, Player player) {
        player.leaveRealm();
        playerMap.put(player.id(), player);
        playerRealmMap.put(player.id(), player.getRealm().id());
        return 0;
    }

    @Override
    public int countByName(EntityManager entityManager, String name) {
        return 0;
    }

    @Override
    public int countByAccount(EntityManager entityManager, int accountId) {
        return 0;
    }
}
