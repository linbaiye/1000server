package org.y1000.realm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.entities.ActiveEntity;
import org.y1000.entities.RemoveEntityEvent;
import org.y1000.entities.creatures.CreatureState;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.CreatureDieEvent;
import org.y1000.entities.creatures.event.NpcJoinedEvent;
import org.y1000.entities.creatures.event.NpcShiftEvent;
import org.y1000.entities.creatures.monster.PassiveMonster;
import org.y1000.entities.creatures.monster.TestingMonsterAttributeProvider;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.entities.creatures.npc.NpcFactory;
import org.y1000.entities.creatures.npc.NpcFactoryImpl;
import org.y1000.entities.players.Damage;
import org.y1000.item.EquipmentType;
import org.y1000.item.ItemSdbImpl;
import org.y1000.item.Weapon;
import org.y1000.kungfu.KungFuSdb;
import org.y1000.kungfu.attack.AttackKungFuType;
import org.y1000.message.clientevent.ClientToggleKungFuEvent;
import org.y1000.sdb.*;
import org.y1000.util.Coordinate;
import org.y1000.util.Rectangle;


import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class NpcManagerImplTest extends AbstractUnitTestFixture  {

    private NpcManagerImpl npcManager;

    private EntityEventSender eventSender;

    private GroundItemManager itemManager;

    private CreateEntitySdbRepository npcSdbRepository;

    private List<NpcSpawnSetting> monsterSettings;

    private List<NpcSpawnSetting> npcSettings;

    private EntityIdGenerator idGenerator;

    private MonstersSdb monstersSdb;

    private AOIManager aoiManager;

    private RealmMap map;


    @BeforeEach
    void setUp() {
        NpcFactory npcFactory = new NpcFactoryImpl(ActionSdb.INSTANCE, MonstersSdbImpl.INSTANCE, KungFuSdb.INSTANCE, NpcSdbImpl.Instance, MagicParamSdb.INSTANCE, new MerchantItemSdbRepositoryImpl(ItemSdbImpl.INSTANCE));
        eventSender = Mockito.mock(EntityEventSender.class);
        itemManager = Mockito.mock(GroundItemManager.class);
        npcSdbRepository = Mockito.mock(CreateEntitySdbRepository.class);
        monsterSettings = new ArrayList<>();
        CreateNpcSdb monsterSdb = Mockito.mock(CreateNpcSdb.class);
        when(monsterSdb.getAllSettings()).thenReturn(monsterSettings);
        when(monsterSdb.getSettings(anyString())).thenReturn(monsterSettings);
        npcSettings = new ArrayList<>();
        CreateNpcSdb npcSdb = Mockito.mock(CreateNpcSdb.class);
        when(npcSdb.getAllSettings()).thenReturn(npcSettings);
        when(npcSdb.getSettings(anyString())).thenReturn(npcSettings);
        when(npcSdbRepository.loadMonster(anyInt())).thenReturn(monsterSdb);
        when(npcSdbRepository.loadNpc(anyInt())).thenReturn(npcSdb);
        idGenerator = new EntityIdGenerator();
        monstersSdb = Mockito.mock(MonstersSdb.class);
        aoiManager = Mockito.mock(AOIManager.class);
        map = Mockito.mock(RealmMap.class);
        when(map.movable(any(Coordinate.class))).thenReturn(true);
        npcManager = new NpcManagerImpl(eventSender, idGenerator, npcFactory, itemManager, monstersSdb, aoiManager, monsterSdb, npcSdb, map);
    }

    @Test
    void init() {
        Rectangle range = new Rectangle(Coordinate.xy(1, 1), Coordinate.xy(4, 4));
        monsterSettings.add(new NpcSpawnSetting(range, 2, "牛"));
        when(npcSdbRepository.monsterSdbExists(49)).thenReturn(true);
        npcManager.init();
        Npc npc = npcManager.find(1).get();
        assertEquals("牛", npc.viewName());
        assertTrue(range.contains(npc.spawnCoordinate()));
        npc = npcManager.find(2).get();
        assertEquals("牛", npc.viewName());
        assertTrue(range.contains(npc.spawnCoordinate()));
        verify(eventSender, times(2)).notifyVisiblePlayers(any(ActiveEntity.class), any(NpcJoinedEvent.class));
    }

    @Test
    void respawnNpc() {
        Rectangle range = new Rectangle(Coordinate.xy(1, 1), Coordinate.xy(4, 4));
        monsterSettings.add(new NpcSpawnSetting(range, 1, "一级牛"));
        when(npcSdbRepository.monsterSdbExists(49)).thenReturn(true);
        npcManager.init();
        verify(eventSender, times(1)).notifyVisiblePlayers(any(ActiveEntity.class), any(NpcJoinedEvent.class));
        Npc monster = npcManager.find(1L).get();
        Weapon weapon = Mockito.mock(Weapon.class);
        when(weapon.damage()).thenReturn(new Damage(10000000, 1, 1,1));
        when(weapon.kungFuType()).thenReturn(AttackKungFuType.QUANFA);
        when(weapon.equipmentType()).thenReturn(EquipmentType.WEAPON);
        while (monster.stateEnum() != State.DIE) {
            var player = playerBuilder().weapon(weapon).build();
            player.handleClientEvent(new ClientToggleKungFuEvent(1, 1));
            monster.attackedBy(player);
        }
        CreatureState<?> state = monster.state();
        npcManager.update(state.totalMillis());
        assertEquals(State.IDLE, monster.stateEnum());
        assertTrue(range.contains(monster.spawnCoordinate()));
        assertTrue(range.contains(monster.coordinate()));
        verify(eventSender, times(2)).notifyVisiblePlayers(any(ActiveEntity.class), any(NpcJoinedEvent.class));
    }


    @Test
    void whenAMonsterDies() {
        when(monstersSdb.getHaveItem(any(String.class))).thenReturn("皮:2:1:肉:4:1");
        TestingMonsterAttributeProvider attributeProvider = new TestingMonsterAttributeProvider();
        attributeProvider.idName = "牛";
        PassiveMonster monster = monsterBuilder().attributeProvider(attributeProvider).name("牛").build();
        npcManager.onEvent(new CreatureDieEvent(monster));
        verify(itemManager, times(1)).dropItem("皮:2:1:肉:4:1", monster.coordinate());
    }

    @Test
    void handleShiftEvent() {
        Rectangle range = new Rectangle(Coordinate.xy(1, 1), Coordinate.xy(4, 4));
        monsterSettings.add(new NpcSpawnSetting(range, 2, "白狐狸"));
        when(npcSdbRepository.monsterSdbExists(49)).thenReturn(true);
        npcManager.init();
        Npc npc = npcManager.find(1L).orElseThrow(IllegalAccessError::new);
        npcManager.onEvent(new NpcShiftEvent("白狐狸变身", npc));
        assertTrue(npcManager.find(1L).isEmpty());
        npc = npcManager.find(3L).orElseThrow(IllegalAccessError::new);
        assertEquals("白狐狸变身", npc.idName());
        npcManager.onEvent(new RemoveEntityEvent(npc));
        npcManager.update(1000000);
        // original npc should be back.
        assertTrue(npcManager.find(1L).isPresent());
    }

    @Test
    void handleCloneEvent() {
        Rectangle range = new Rectangle(Coordinate.xy(1, 1), Coordinate.xy(4, 4));
        monsterSettings.add(new NpcSpawnSetting(range, 2, "分身忍者"));
        when(npcSdbRepository.monsterSdbExists(49)).thenReturn(true);
        npcManager.init();
        Npc npc = npcManager.find(1L).orElseThrow(IllegalAccessError::new);
    }

    @Test
    void findMerchants() {
        Rectangle range = new Rectangle(Coordinate.xy(1, 1), Coordinate.xy(4, 4));
        npcSettings.add(new NpcSpawnSetting(range, 1, "老板娘"));
        when(npcSdbRepository.npcSdbExists(49)).thenReturn(true);
        npcManager.init();
        assertFalse(npcManager.findMerchants().isEmpty());
    }
}