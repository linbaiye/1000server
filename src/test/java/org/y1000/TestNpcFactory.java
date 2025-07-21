package org.y1000;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.mockito.Mockito;
import org.y1000.entities.creatures.npc.NpcMeleeAbility;
import org.y1000.entities.creatures.npc.NpcFactory;
import org.y1000.entities.creatures.npc.NpcFactoryImpl;
import org.y1000.entities.creatures.npc.NpcImpl;
import org.y1000.item.ItemSdbImpl;
import org.y1000.kungfu.KungFuFactory;
import org.y1000.kungfu.KungFuSdb;
import org.y1000.realm.RealmMap;
import org.y1000.repository.ItemRepositoryImpl;
import org.y1000.repository.KungFuBookRepository;
import org.y1000.sdb.*;
import org.y1000.util.Coordinate;

public class TestNpcFactory {

    private TestNpcFactory(){}

    private long id = 0;

    public static final TestNpcFactory Instance = new TestNpcFactory();
    private static final NpcFactory npcFactory = new NpcFactoryImpl(ActionSdb.INSTANCE, MonstersSdbImpl.INSTANCE, KungFuSdb.INSTANCE, NonMonsterNpcSdbImpl.Instance,
            MagicParamSdb.INSTANCE, ItemSdbImpl.INSTANCE, new ItemRepositoryImpl(ItemSdbImpl.INSTANCE, ItemDrugSdbImpl.INSTANCE, Mockito.mock(KungFuFactory.class),
            Mockito.mock(EntityManagerFactory.class)));

    public NpcImpl create(RealmMap realmMap, Coordinate coordinate) {
        return npcFactory.create(id++, "牛", realmMap, coordinate, null);
    }

    public static void main(String[] args) {
        NpcImpl npc = npcFactory.create(0, "马", Mockito.mock(RealmMap.class), Coordinate.xy(1, 1), null);
        npc.findAbility(NpcMeleeAbility.class)
                .ifPresent(System.out::println);
    }

}
