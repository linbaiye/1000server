package org.y1000;

import jakarta.persistence.EntityManagerFactory;
import org.mockito.Mockito;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.monster.PassiveMonster;
import org.y1000.entities.creatures.monster.TestingMonsterAttributeProvider;
import org.y1000.entities.objects.DynamicObjectFactory;
import org.y1000.entities.objects.DynamicObjectFactoryImpl;
import org.y1000.entities.players.*;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.item.ItemFactory;
import org.y1000.item.ItemSdbImpl;
import org.y1000.kungfu.KungFuBook;
import org.y1000.kungfu.KungFuBookFactory;
import org.y1000.kungfu.KungFuFactory;
import org.y1000.kungfu.attack.AttackKungFuType;
import org.y1000.realm.Realm;
import org.y1000.realm.RealmMap;
import org.y1000.repository.BankRepository;
import org.y1000.repository.ItemRepository;
import org.y1000.repository.ItemRepositoryImpl;
import org.y1000.repository.KungFuBookRepositoryImpl;
import org.y1000.sdb.DynamicObjectSdbImpl;
import org.y1000.sdb.ItemDrugSdb;
import org.y1000.sdb.ItemDrugSdbImpl;
import org.y1000.util.Coordinate;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public abstract class AbstractUnitTestFixture {

    protected final KungFuBookFactory kungFuBookFactory = new KungFuBookRepositoryImpl();
    protected final KungFuFactory kungFuFactory = new KungFuBookRepositoryImpl();

    private int id;

    protected RealmMap mockRealmMap() {
        RealmMap mockedMap= Mockito.mock(RealmMap.class);
        when(mockedMap.mapFile()).thenReturn("map");
        when(mockedMap.tileFile()).thenReturn("til");
        when(mockedMap.objectFile()).thenReturn("obj");
        when(mockedMap.roofFile()).thenReturn("rof");
        return mockedMap;
    }

    private ItemRepositoryImpl createItemRepositoryImpl() {
        return new ItemRepositoryImpl(ItemSdbImpl.INSTANCE, ItemDrugSdbImpl.INSTANCE, kungFuFactory, Mockito.mock(EntityManagerFactory.class));
    }

    protected DynamicObjectFactory createDynamicObjectFactory() {
        return new DynamicObjectFactoryImpl(DynamicObjectSdbImpl.INSTANCE);
    }

    protected ItemFactory createItemFactory() {
        return createItemRepositoryImpl();
    }

    protected ItemRepository createItemRepository() {
        return createItemRepositoryImpl();
    }

    protected BankRepository createBankRepository() {
        return createItemRepositoryImpl();
    }

    protected Realm mockRealm(RealmMap map) {
        Realm mockedRealm = Mockito.mock(Realm.class);
        when(mockedRealm.name()).thenReturn("realm");
        when(mockedRealm.bgm()).thenReturn("bgm");
        when(mockedRealm.map()).thenReturn(map);
        when(mockedRealm.id()).thenReturn(1);
        return mockedRealm;
    }

    protected Realm mockAllFlatRealm() {
        RealmMap mockedMap = mockRealmMap();
        when (mockedMap.movable(any(Coordinate.class))).thenReturn(true);
        return mockRealm(mockedMap);
    }


    protected static final Map<State, Integer> MONSTER_STATE_MILLIS = new HashMap<>() {
        {
            put(State.IDLE, 1000);
            put(State.WALK, 770);
            put(State.HURT, 540);
            put(State.ATTACK, 700);
            put(State.DIE, 700);
            put(State.FROZEN, 900);
        }
    };


    protected PassiveMonster.PassiveMonsterBuilder monsterBuilder() {
        return PassiveMonster.builder()
                .id(nextId())
                .coordinate(Coordinate.xy(1, 1))
                .direction(Direction.UP)
                .name("test")
                .realmMap(Mockito.mock(RealmMap.class))
                .skill(null)
                .attributeProvider(new TestingMonsterAttributeProvider())
                .stateMillis(MONSTER_STATE_MILLIS)
                ;
    }

    protected PlayerImpl.PlayerImplBuilder playerBuilder() {
        KungFuBook kungFuBook = kungFuBookFactory.create();
        return PlayerImpl.builder()
                .id(nextId())
                .coordinate(new Coordinate(1, 1))
                .name("test")
                .kungFuBook(kungFuBook)
                .attackKungFu(kungFuBook.findUnnamedAttack(AttackKungFuType.QUANFA))
                .innateAttributesProvider(PlayerDefaultAttributes.INSTANCE)
                .yinYang(new YinYang())
                .life(PlayerLife.create())
                .arm(PlayerLife.create())
                .leg(PlayerLife.create())
                .head(PlayerLife.create())
                .innerPower(PlayerExperiencedAgedAttribute.createInnerPower())
                .outerPower(PlayerExperiencedAgedAttribute.createOuterPower())
                .power(PlayerExperiencedAgedAttribute.createPower())
                .pillSlots(new PillSlots())
                .inventory(new Inventory())
                ;
    }

    protected int nextId() {
        return id++;
    }

}
