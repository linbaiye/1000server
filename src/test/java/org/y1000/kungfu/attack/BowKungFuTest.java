package org.y1000.kungfu.attack;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.monster.PassiveMonster;
import org.y1000.entities.players.AbstractPlayerUnitTestFixture;
import org.y1000.entities.players.PlayerStillState;
import org.y1000.entities.players.event.PlayerAttackEvent;
import org.y1000.entities.players.event.PlayerAttackEventResponse;
import org.y1000.entities.players.fight.PlayerAttackState;
import org.y1000.entities.players.fight.PlayerCooldownState;
import org.y1000.item.DefaultStackItem;
import org.y1000.kungfu.TestingAttackKungFuParameters;
import org.y1000.message.PlayerTextEvent;
import org.y1000.message.clientevent.ClientAttackEvent;
import org.y1000.message.serverevent.UpdateInventorySlotEvent;

import static org.junit.jupiter.api.Assertions.*;

class BowKungFuTest extends AbstractPlayerUnitTestFixture {

    private BowKungFu bowKungFu;

    private TestingAttackKungFuParameters parameters;

    @BeforeEach
    void setUp() {
        setup();
        parameters = new TestingAttackKungFuParameters();
        bowKungFu = BowKungFu.builder().name("test").parameters(parameters).exp(0).build();
    }

    @Test
    void startBowAttack() {
        PassiveMonster monster = monsterBuilder().coordinate(player.coordinate().move(2, 0)).build();
        ClientAttackEvent clientAttackEvent = new ClientAttackEvent(1, monster.id(), State.BOW, Direction.RIGHT);
        bowKungFu.startAttack(player, clientAttackEvent, monster);
        // no ammo.
        PlayerTextEvent event = eventListener.removeFirst(PlayerTextEvent.class);
        assertEquals(PlayerTextEvent.TextType.OUT_OF_AMMO.value(), event.toPacket().getText().getType());
        var response = eventListener.removeFirst(PlayerAttackEventResponse.class);
        assertFalse(response.isAccepted());
        assertTrue(response.toPacket().getAttackEventResponsePacket().hasBackToState());
        assertTrue(player.state() instanceof PlayerStillState);

        eventListener.clearEvents();
        player.inventory().add(new DefaultStackItem("箭", 3));
        bowKungFu.startAttack(player, clientAttackEvent, monster);
        response = eventListener.removeFirst(PlayerAttackEventResponse.class);
        assertTrue(player.state() instanceof PlayerAttackState);
        assertTrue(response.isAccepted());
        assertNotNull(eventListener.removeFirst(UpdateInventorySlotEvent.class));
    }

    @Test
    void attackAgainNoPower() {
        PassiveMonster monster = monsterBuilder().coordinate(player.coordinate().move(2, 0)).build();
        player.inventory().add(new DefaultStackItem("箭", 3));
        ClientAttackEvent clientAttackEvent = new ClientAttackEvent(1, monster.id(), State.BOW, Direction.RIGHT);
        // trigger attack counter.
        bowKungFu.startAttack(player, clientAttackEvent, monster);
        assertNotNull(player.getFightingEntity());
        player.update(player.cooldown());
        // use all power.
        player.consumePower(player.power());
        parameters.setPowerToSwing(1);
        eventListener.clearEvents();

        bowKungFu.attackAgain(player);
        assertTrue(player.state() instanceof PlayerCooldownState);
        PlayerTextEvent textEvent = eventListener.removeFirst(PlayerTextEvent.class);
        assertEquals(PlayerTextEvent.TextType.NO_POWER.value(), textEvent.toPacket().getText().getType());
        assertEquals(0, eventListener.eventSize());
    }

    @Test
    void attackAgain() {
        PassiveMonster monster = monsterBuilder().coordinate(player.coordinate().move(2, 0)).build();
        player.inventory().add(new DefaultStackItem("箭", 3));
        ClientAttackEvent clientAttackEvent = new ClientAttackEvent(1, monster.id(), State.BOW, Direction.RIGHT);
        // trigger attack counter.
        bowKungFu.startAttack(player, clientAttackEvent, monster);
        assertNotNull(player.getFightingEntity());
        player.update(player.cooldown());
        bowKungFu.attackAgain(player);
        assertTrue(player.state() instanceof PlayerAttackState);
        assertNotNull(eventListener.removeFirst(PlayerAttackEvent.class));
    }


    @Test
    void description() {
        String description = bowKungFu.description();
        assertTrue(description.contains("修炼等级"));
        assertTrue(description.contains("攻击速度"));
        assertTrue(description.contains("恢复"));
        assertTrue(description.contains("闪躲"));
        assertTrue(description.contains("破坏力"));
        assertTrue(description.contains("防御力"));
    }
}