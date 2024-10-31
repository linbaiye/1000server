package org.y1000.repository;

import jakarta.persistence.EntityManager;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.item.Equipment;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {
    default void save(EntityManager entityManager, Player player) {
        Validate.notNull(player);
        saveInventory(entityManager, player.id(), player.inventory());
    }

    Optional<Inventory> findInventory(EntityManager entityManager, long playerId);

    void saveInventory(EntityManager entityManager, long playerId, Inventory inventory);

    void saveEquipments(EntityManager entityManager, Collection<Equipment> equipments);
}
