package org.y1000.repository;

import jakarta.persistence.EntityManager;
import org.apache.commons.lang3.tuple.Pair;
import org.y1000.entities.players.Player;
import org.y1000.item.ItemFactory;

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
        Player male = playerFactory.create("测试男", true, 100000251L);
        playerMap.put(male.id(), male);
        playerRealmMap.put(male.id(), 6);
        male.inventory().add(itemFactory.createItem("生药", 100));
        male.inventory().add(itemFactory.createEquipment("三叉戟"));
        male.inventory().add(itemFactory.createEquipment("龙光剑"));
        male.inventory().add(itemFactory.createEquipment("男子黄金铠甲"));
    }

    @Override
    public Optional<Pair<Player, Integer>> find(int accountId, String charName) {
        return Optional.empty();
    }

    @Override
    public Optional<Integer> findRealm(long id) {
        return Optional.ofNullable(playerRealmMap.get(id));
    }


    @Override
    public void update(Player player) {
        playerMap.put(player.id(), player);
        playerRealmMap.put(player.id(), player.getRealm().id());
    }

    @Override
    public Optional<Player> load(long id) {
        return Optional.ofNullable(playerMap.get(id));
    }

    @Override
    public long save(EntityManager entityManager, int accountId, Player player) {
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
