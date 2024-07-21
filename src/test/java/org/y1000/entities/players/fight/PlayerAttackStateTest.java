package org.y1000.entities.players.fight;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.TestingEventListener;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.CreatureDieEvent;
import org.y1000.entities.creatures.event.PlayerShootEvent;
import org.y1000.entities.creatures.npc.NpcCommonState;
import org.y1000.entities.players.AbstractPlayerUnitTestFixture;
import org.y1000.item.ItemSdb;
import org.y1000.item.WeaponImpl;
import org.y1000.kungfu.attack.AttackKungFuParameters;
import org.y1000.kungfu.attack.AttackKungFuType;
import org.y1000.kungfu.attack.QuanfaKungFu;
import org.y1000.message.InputResponseMessage;
import org.y1000.message.clientevent.ClientMovementEvent;
import org.y1000.message.clientevent.input.RightMouseClick;
import org.y1000.realm.Realm;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class PlayerAttackStateTest extends AbstractPlayerUnitTestFixture  {

    private static class TestKungFuParameters implements AttackKungFuParameters {

        public TestKungFuParameters(int attackSpeed) {
            this.attackSpeed = attackSpeed;
        }

        private int attackSpeed;

        @Override
        public int powerToSwing() {
            return 0;
        }

        @Override
        public int innerPowerToSwing() {
            return 0;
        }

        @Override
        public int recovery() {
            return 0;
        }

        @Override
        public int outerPowerToSwing() {
            return 0;
        }

        @Override
        public int lifeToSwing() {
            return 0;
        }

        @Override
        public int attackSpeed() {
            return attackSpeed;
        }
    }



    @BeforeEach
    public void setUp() {
        setup();
    }

    @Test
    void hurtRelated() {
        var attackState = PlayerAttackState.of(player);
        assertEquals(State.COOLDOWN, attackState.decideAfterHurtState());
        attackState.afterHurt(player);
        assertTrue(player.state() instanceof PlayerCooldownState);
        var before = player.coordinate();
        attackState.moveToHurtCoordinate(player);
        assertEquals(before, player.coordinate());
    }

    @Test
    void considerAttackSpeed() {
        player = playerBuilder().attackKungFu(QuanfaKungFu.builder().parameters(new TestKungFuParameters(-60)).name("test").exp(0).build()).build();
        var state = PlayerAttackState.of(player);
        assertEquals(100, state.totalMillis());
        player = playerBuilder().attackKungFu(QuanfaKungFu.builder().parameters(new TestKungFuParameters(160)).name("test").exp(0).build()).build();
        state = PlayerAttackState.of(player);
        assertEquals(player.getStateMillis(state.stateEnum()), state.totalMillis());
    }

    @Test
    void emitProjectileEvent() {
        TestingEventListener eventListener = new TestingEventListener();
        player = playerBuilder().attackKungFu(kungFuBookFactory.create().findUnnamedAttack(AttackKungFuType.BOW)).build();
        player.setFightingEntity(monsterBuilder().coordinate(Coordinate.xy(2, 2)).build());
        player.registerEventListener(eventListener);
        var state = PlayerAttackState.of(player);
        state.update(player, state.totalMillis());
        PlayerShootEvent dequeue = eventListener.dequeue(PlayerShootEvent.class);
        assertNotNull(dequeue);
    }


    @Test
    void move() {
        Realm realm = mockAllFlatRealm();
        player.joinReam(realm);
        var state = PlayerAttackState.of(player);
        player.changeState(state);
        eventListener.clearEvents();
        state.move(player, new ClientMovementEvent(new RightMouseClick(1, Direction.RIGHT), player.coordinate()));
        assertTrue(player.state() instanceof PlayerFightWalkState);
        InputResponseMessage event = eventListener.dequeue(InputResponseMessage.class);
        assertSame(event.source(), player);
        assertEquals(event.toPacket().getResponsePacket().getPositionPacket().getDirection(), Direction.RIGHT.value());
    }

    @Test
    void doables() {
        var state = PlayerAttackState.of(player);
        assertTrue(state.attackable());
        assertTrue(state.canSitDown());
        assertFalse(state.canStandUp());
        assertTrue(state.canUseFootKungFu());
    }

    @Test
    void rangedAttackWhenTargetDead() {
        ItemSdb itemSdb = Mockito.mock(ItemSdb.class);
        player.inventory().add(new WeaponImpl("bow", itemSdb));
        enableBowKungFu();

        var monster = monsterBuilder().coordinate(player.coordinate().move(3, 0)).build();
        player.setFightingEntity(monster);
        player.changeState(PlayerAttackState.of(player));
        monster.changeState(NpcCommonState.die(1000));
        player.onEvent(new CreatureDieEvent(monster));
        assertNull(player.getFightingEntity());
        player.update(player.getStateMillis(State.BOW));

        // shoot anyway as state changed before creature died.
        PlayerShootEvent playerShootEvent = eventListener.removeFirst(PlayerShootEvent.class);
        assertEquals(playerShootEvent.projectile().target(), monster);
    }
}