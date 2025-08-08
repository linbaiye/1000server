package org.y1000;

import jakarta.persistence.EntityManagerFactory;
import org.mockito.Mockito;
import org.y1000.entities.creatures.npc.NpcFactoryImpl;
import org.y1000.entities.objects.DynamicObjectFactory;
import org.y1000.entities.objects.DynamicObjectFactoryImpl;
import org.y1000.entities.players.*;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.item.ItemFactory;
import org.y1000.item.ItemSdbImpl;
import org.y1000.kungfu.KungFuBook;
import org.y1000.kungfu.KungFuBookFactory;
import org.y1000.kungfu.KungFuFactory;
import org.y1000.kungfu.KungFuSdb;
import org.y1000.kungfu.attack.AttackKungFuType;
import org.y1000.realm.Realm;
import org.y1000.realm.RealmMap;
import org.y1000.repository.BankRepository;
import org.y1000.repository.ItemRepository;
import org.y1000.repository.ItemRepositoryImpl;
import org.y1000.repository.KungFuBookRepositoryImpl;
import org.y1000.sdb.*;
import org.y1000.util.Coordinate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public abstract class AbstractUnitTestFixture {

    protected final KungFuBookFactory kungFuBookFactory = createKungFuBookFactory();
    protected final KungFuFactory kungFuFactory = createKungFuFactory();

    protected final TestNpcFactory npcFactory = TestNpcFactory.Instance;

    private int id;

    protected RealmMap mockRealmMap() {
        RealmMap mockedMap= Mockito.mock(RealmMap.class);
        when(mockedMap.mapFile()).thenReturn("map");
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

    protected KungFuBookRepositoryImpl createKungFuBookRepositoryImpl() {
        return new KungFuBookRepositoryImpl(Mockito.mock(EntityManagerFactory.class));
    }
    protected NpcFactoryImpl createNpcFactory() {
        return new NpcFactoryImpl(ActionSdb.INSTANCE, MonstersSdbImpl.INSTANCE, KungFuSdb.INSTANCE, NonMonsterNpcSdbImpl.Instance,
                MagicParamSdb.INSTANCE, ItemSdbImpl.INSTANCE, Mockito.mock(ItemFactory.class), QuestSdbImpl.Instance, Mockito.mock(BankRepository.class));
    }

    protected KungFuBookFactory createKungFuBookFactory() {
        return createKungFuBookRepositoryImpl();
    }

    protected KungFuFactory createKungFuFactory() {
        return createKungFuBookRepositoryImpl();
    }

    protected Realm mockRealm(RealmMap map) {
        Realm mockedRealm = Mockito.mock(Realm.class);
        when(mockedRealm.title()).thenReturn("realm");
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




    protected PlayerImpl.PlayerImplBuilder playerBuilder() {
        KungFuBook kungFuBook = kungFuBookFactory.create();
        return PlayerImpl.builder()
                .id(nextId())
                .coordinate(new Coordinate(1, 1))
                .name("test")
                .kungFuBook(kungFuBook)
                .attackKungFu(kungFuBook.findUnnamedAttack(AttackKungFuType.Fist))
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
