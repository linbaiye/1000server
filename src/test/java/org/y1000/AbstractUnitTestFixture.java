package org.y1000;

import org.mockito.Mockito;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.monster.PassiveMonster;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.kungfu.KungFuBook;
import org.y1000.kungfu.KungFuBookFactory;
import org.y1000.kungfu.attack.AttackKungFuType;
import org.y1000.realm.Realm;
import org.y1000.realm.RealmMap;
import org.y1000.repository.KungFuBookRepositoryImpl;
import org.y1000.util.Coordinate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public abstract class AbstractUnitTestFixture {

    protected final KungFuBookFactory kungFuBookFactory = new KungFuBookRepositoryImpl();

    private int id;

    protected Realm mockAllFlatRealm() {
        Realm mock = Mockito.mock(Realm.class);
        RealmMap map = Mockito.mock(RealmMap.class);
        when (map.movable(any(Coordinate.class))).thenReturn(true);
        when(mock.map()).thenReturn(map);
        return mock;
    }

    protected PlayerImpl.PlayerImplBuilder playerBuilder() {
        KungFuBook kungFuBook = kungFuBookFactory.create();
        return PlayerImpl.builder()
                .id(1L)
                .coordinate(new Coordinate(1, 1))
                .name("test")
                .kungFuBook(kungFuBook)
                .attackKungFu(kungFuBook.findUnnamedAttack(AttackKungFuType.QUANFA))
                .inventory(new Inventory());
    }

    protected int nextId() {
        return id++;
    }

    protected PassiveMonster createMonster(Coordinate coordinate) {
        return PassiveMonster.builder().id(id++)
                .coordinate(coordinate)
                .direction(Direction.UP)
                .name("test")
                .realmMap(Mockito.mock(RealmMap.class))
                .avoidance(0)
                .build();
    }
}
