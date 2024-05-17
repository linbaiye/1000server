package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.Entity;
import org.y1000.entities.attribute.Damage;
import org.y1000.entities.creatures.*;
import org.y1000.entities.creatures.event.ChangeStateEvent;
import org.y1000.entities.players.equipment.weapon.Weapon;
import org.y1000.entities.players.event.PlayerAttackEvent;
import org.y1000.entities.players.kungfu.attack.AttackKungFu;
import org.y1000.entities.players.kungfu.UnnamedBufa;
import org.y1000.entities.players.kungfu.attack.AttackKungFuType;
import org.y1000.entities.players.kungfu.attack.unnamed.UnnamedQuanFa;
import org.y1000.message.serverevent.JoinedRealmEvent;
import org.y1000.network.ClientEventListener;
import org.y1000.network.Connection;
import org.y1000.entities.Direction;
import org.y1000.entities.players.kungfu.FootKungFu;
import org.y1000.message.*;
import org.y1000.message.clientevent.ClientEvent;
import org.y1000.message.serverevent.PlayerLeftEvent;
import org.y1000.realm.Realm;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
public final class PlayerImpl extends AbstractViolentCreature<PlayerImpl, PlayerState> implements Player,
        ClientEventListener {

    private Realm realm;

    private final Queue<ClientEvent> eventQueue;

    private final Connection connection;

    private AttackKungFu attackKungFu;

    private FootKungFu footKungfu;

    private Weapon weapon;

    private static final Map<State, Integer> STATE_MILLIS = new HashMap<>() {{
        put(State.IDLE, 2200);
        put(State.WALK, 840);
        put(State.RUN, 420);
        put(State.FLY, 360);
        put(State.COOLDOWN, 1400);
        put(State.FIST, AttackKungFuType.QUANFA.below50Millis());
        put(State.KICK, AttackKungFuType.QUANFA.above50Millis());
        put(State.HURT, 280);
        put(State.ENFIGHT_WALK, 840);
    }};


    public PlayerImpl(long id, Coordinate coordinate,
                      Direction direction,
                      String name, Connection connection) {
        super(id, coordinate, direction, name, STATE_MILLIS);
        eventQueue = new ConcurrentLinkedQueue<>();
        changeState(new PlayerIdleState(getStateMillis(State.IDLE)));
        changeDirection(Direction.DOWN);
        this.connection = connection;
        this.footKungfu = new UnnamedBufa(8500);
        attackKungFu = UnnamedQuanFa.builder()
                .level(5501)
                .bodyArmor(1)
                .recovery(50)
                .attackSpeed(40)
                .build();
        this.connection.registerClientEventListener(this);
    }

    Optional<ClientEvent> takeClientEvent() {
        return Optional.ofNullable(eventQueue.poll());
    }

    RealmMap realmMap() {
        return realm.map();
    }

    @Override
    public Optional<FootKungFu> footKungFu() {
        return Optional.ofNullable(footKungfu);
    }

    @Override
    public AttackKungFu attackKungFu() {
        return attackKungFu;
    }

    @Override
    public Connection connection() {
        return connection;
    }


    @Override
    public void joinReam(Realm realm) {
        if (this.realm != null) {
            leaveRealm();
        }
        this.realm = realm;
        realmMap().occupy(this);
        changeState(new PlayerIdleState(getStateMillis(State.IDLE)));
        changeDirection(Direction.DOWN);
        emitEvent(new JoinedRealmEvent(this, coordinate()));
    }

    @Override
    public Realm getRealm() {
        return this.realm;
    }

    @Override
    public void leaveRealm() {
        if (realm != null) {
            realmMap().free(this);
        }
        changeState(PlayerFrozenState.Instance);
        emitEvent(new PlayerLeftEvent(this));
    }

    @Override
    public int attackSpeed() {
        int kungfuSpeed = attackKungFu != null ? attackKungFu.getAttackSpeed() : 0;
        return kungfuSpeed + 70;
    }

    @Override
    protected PlayerState createHurtState(ViolentCreature attacker) {
        return PlayerHurtState.interruptCurrentState(this);
        //return new PlayerHurtState(attacker, getStateMillis(State.HURT), state()::afterHurt);
    }

    @Override
    public PlayerInterpolation captureInterpolation() {
        return PlayerInterpolation.FromPlayer(this, state().elapsedMillis());
    }

    public void attack(Entity target) {
        if (!target.attackable()) {
            changeState(PlayerIdleState.chillOut(this));
            emitEvent(ChangeStateEvent.of(this));
            return;
        }
        int cooldown = cooldown();
        if (cooldown > 0) {
            changeState(new PlayerCooldownState(getStateMillis(State.COOLDOWN), target));
            emitEvent(ChangeStateEvent.of(this));
            return;
        }
        Direction direction = coordinate().computeDirection(target.coordinate());
        if (direction != direction()) {
            changeDirection(direction);
        }
        int distance = coordinate().distance(target.coordinate());
        if (distance <= 1) {
            cooldownAttack();
            var actionMillis = attackSpeed() * Realm.STEP_MILLIS;
            State attackState = attackKungFu.randomAttackState();
            changeState(PlayerAttackState.attack(target, attackState, actionMillis));
            target.attackedBy(this);
            emitEvent(PlayerAttackEvent.of(this));
        } else {
            log.debug("Change to enfight.");
            changeState(new PlayerEnfightState(getStateMillis(State.COOLDOWN), target));
            emitEvent(ChangeStateEvent.of(this));
        }
    }

    @Override
    public void update(int delta) {
        cooldown(delta);
        state().update(this, delta);
    }

    public void clearEventQueue() {
        eventQueue.clear();
    }

    public int recovery() {
        int kfr = attackKungFu != null ? attackKungFu.getRecovery() : 0;
        return 50 + kfr;
    }

    @Override
    public int hit() {
        return 0;
    }

    @Override
    public Damage damage() {
        return null;
    }

    @Override
    public String toString() {
        return "PlayerImpl{" +
                "id=" + id() +
                ", coordinate=" + coordinate() +
                ", direction=" + direction() +
                ", state=" + stateEnum() +
                '}';
    }

    @Override
    public String name() {
        return "";
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerImpl player = (PlayerImpl) o;
        return id() == player.id();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id());
    }

    @Override
    public void OnEvent(ClientEvent clientEvent) {
        eventQueue.add(clientEvent);
    }
}
