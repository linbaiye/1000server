package org.y1000.repository;

import jakarta.persistence.EntityManager;
import org.apache.commons.lang3.tuple.Pair;
import org.y1000.entities.players.Player;
import org.y1000.item.ItemFactory;
import org.y1000.item.KungFuItem;
import org.y1000.item.StackItem;
import org.y1000.kungfu.KungFu;
import org.y1000.kungfu.KungFuBook;
import org.y1000.kungfu.KungFuFactory;
import org.y1000.kungfu.protect.ProtectKungFu;
import org.y1000.util.Coordinate;

import java.util.*;

public class DevPlayerRepository implements PlayerRepository {

    private final Map<Long, Player> playerMap = new HashMap<>();
    private final Map<Long, Integer> playerRealmMap = new HashMap<>();

    private final PlayerRepositoryImpl playerFactory;

    private final ItemFactory itemFactory;
    private final KungFuFactory kungFuFactory;

    private final Set<Long> used = new HashSet<>();


    private void add(KungFuBook book, String name) {
        var stackItem = (StackItem) itemFactory.createItem(name, 1);
        book.addToBasic(((KungFuItem)stackItem.item()).kungFu());
    }

    private void addKungFu(KungFuBook book) {
        add(book, "金钟罩");
        add(book, "风灵旋");
        add(book, "闪光剑破解");
        add(book, "壁射剑法");
        add(book, "杨家枪法");
        add(book, "点枪术");
        add(book, "无击阵");
        add(book, "三弓合体");
    }

    public synchronized long[] getAvailablePlayer() {
        for (Map.Entry<Long, Integer> longIntegerEntry : playerRealmMap.entrySet()) {
            if (!used.contains(longIntegerEntry.getKey())) {
                used.add(longIntegerEntry.getKey());
                return new long[]{longIntegerEntry.getKey(), longIntegerEntry.getValue()};
            }
        }
        return null;
    }

    public DevPlayerRepository(PlayerRepositoryImpl factory, ItemFactory itemFactory, KungFuFactory kungFuFactory) {
        this.playerFactory = factory;
        this.itemFactory = itemFactory;
        this.kungFuFactory = kungFuFactory;
        Player male = playerFactory.create("测试男名字不能太长了", true, 100000251L);
        while (male.age() < 3000) {
            male.update(1000);
        }
        playerMap.put(male.id(), male);
        playerRealmMap.put(male.id(), 6);
        male.inventory().add(itemFactory.createItem("生药", 10000));
        male.inventory().add(itemFactory.createItem("丹药", 10000));
        male.inventory().add(itemFactory.createItem("丸药", 10000));
        male.inventory().add(itemFactory.createItem("汤药", 10000));
        male.inventory().add(itemFactory.createItem("熊掌", 10000));
        male.inventory().add(itemFactory.createItem("熊胆", 10000));
        male.inventory().add(itemFactory.createItem("老虎指甲", 10000));
        male.inventory().add(itemFactory.createEquipment("银狼破皇剑"));
        male.inventory().add(itemFactory.createEquipment("男子黄龙鞋"));
        male.inventory().add(itemFactory.createEquipment("木弓"));
        male.inventory().add(itemFactory.createEquipment("男子黄龙弓服"));
        male.inventory().add(itemFactory.createEquipment("男子黄龙手套"));
        male.inventory().add(itemFactory.createEquipment("龙恨"));
        male.inventory().add(itemFactory.createEquipment("黄龙斧"));
        male.inventory().add(itemFactory.createEquipment("男子斗笠"));
        addKungFu(male.kungFuBook());

        male.inventory().add(itemFactory.createItem("箭", 10000));
        male.inventory().add(itemFactory.createItem("灵动八方"));
        male.inventory().add(itemFactory.createItem("钱币", 10000));
        male.inventory().add(itemFactory.createItem("白酒", 10000));
        male.inventory().add(itemFactory.createItem("门派石"));

        Player female = playerFactory.create("测试女", false, 100000301L);
        female.changeCoordinate(Coordinate.xy(24, 174));
        playerMap.put(female.id(), female);
        playerRealmMap.put(female.id(), 4);
        female.inventory().add(itemFactory.createItem("生药", 10000));
        female.inventory().add(itemFactory.createItem("仙豆", 100000));
        female.inventory().add(itemFactory.createEquipment("三叉戟"));
        female.inventory().add(itemFactory.createEquipment("龙光剑"));
        female.inventory().add(itemFactory.createEquipment("女子黄金铠甲"));
        female.inventory().add(itemFactory.createEquipment("女子黄龙鞋"));
        female.inventory().add(itemFactory.createEquipment("女子黄龙手套"));
        female.inventory().add(itemFactory.createEquipment("女子长发"));
        female.inventory().add(itemFactory.createEquipment("女子束上发"));
        female.inventory().add(itemFactory.createItem("黑沙刚体", 1));
        female.inventory().add(itemFactory.createItem("应龙大天神", 1));
        female.inventory().add(itemFactory.createEquipment("驱魔烈火弓"));
        female.inventory().add(itemFactory.createEquipment("斗甲"));
        female.inventory().add(itemFactory.createItem("箭", 10000));
        female.inventory().add(itemFactory.createItem("飞刀", 10000));
        female.inventory().add(itemFactory.createItem("门派石"));
        addMaxLevelKungFu(female, "金钟罩");
        addMaxLevelKungFu(female, "壁射剑法");
        addMaxLevelKungFu(female, "灵动八方");
        addMaxLevelKungFu(female, "闪光剑破解");
        for (int i = 0; i< 1000000000; i++)
            female.update(10);
    }

    private void addMaxLevelKungFu(Player player, String name) {
        KungFu k = kungFuFactory.create(name);
        while(k.gainPermittedExp(1000000));
        player.kungFuBook().addToBasic(k);
    }

    @Override
    public Optional<Pair<Player, Integer>> find(int accountId, String charName) {
        return Optional.empty();
    }


    @Override
    public synchronized void update(Player player) {
        playerMap.put(player.id(), player);
        used.remove(player.id());
        playerRealmMap.put(player.id(), player.getRealm().id());
    }

    @Override
    public Optional<Player> load(long id) {
        return Optional.ofNullable(playerMap.get(id));
    }

    @Override
    public Optional<Long> findId(String name) {
        return playerMap.values().stream().filter(p -> p.viewName().equals(name))
                .findFirst()
                .map(Player::id);
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

}
