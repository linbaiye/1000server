package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.creatures.State;
import org.y1000.entities.players.equipment.weapon.Weapon;
import org.y1000.entities.players.kungfu.attack.AttackKungFu;
import org.y1000.entities.players.kungfu.UnnamedBufa;
import org.y1000.entities.players.kungfu.attack.unnamed.UnnamedQuanFa;
import org.y1000.message.serverevent.JoinedRealmEvent;
import org.y1000.network.ClientEventListener;
import org.y1000.network.Connection;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.AbstractCreature;
import org.y1000.entities.players.kungfu.FootKungFu;
import org.y1000.message.*;
import org.y1000.message.clientevent.ClientEvent;
import org.y1000.message.serverevent.PlayerLeftEvent;
import org.y1000.realm.Realm;
import org.y1000.realm.RealmImpl;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
public final class PlayerImpl extends AbstractCreature<PlayerImpl> implements Player,
        ClientEventListener {

    private Realm realm;

    private final Queue<ClientEvent> eventQueue;

    private final Connection connection;

    private AttackKungFu attackKungFu;

    private FootKungFu footKungfu;

    private Weapon weapon;

    private int recoveryCooldown;

    private int attackCooldown;

    private static final Map<State, Integer> STATE_MILLIS = new HashMap<>() {{
        put(State.IDLE, 2200);
        put(State.WALK, 800);
        put(State.RUN, 400);
        put(State.FLY, 360);
        put(State.COOLDOWN, 1400);
        put(State.FIST, 400);
        put(State.KICK, 560);
    }};



    public PlayerImpl(long id, Coordinate coordinate,
                      Direction direction,
                      String name, Connection connection) {
        super(id, coordinate, direction, name, STATE_MILLIS);
        eventQueue = new ConcurrentLinkedQueue<>();
        changeState(new PlayerIdleState());
        changeDirection(Direction.DOWN);
        this.connection = connection;
        this.footKungfu = new UnnamedBufa(8500);
        attackKungFu = UnnamedQuanFa.builder()
                .level(5501)
                .bodyArmor(1)
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
    public Optional<AttackKungFu> attackKungFu() {
        return Optional.ofNullable(attackKungFu);
    }


    @Override
    public Connection connection() {
        return connection;
    }


    @Override
    public void joinReam(RealmImpl realm) {
        if (this.realm != null) {
            leaveRealm();
        }
        this.realm = realm;
        realmMap().occupy(this);
        changeState(new PlayerIdleState());
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
        emitEvent(new PlayerLeftEvent(this));
    }

    @Override
    public int attackSpeed() {
        int kungfuSpeed = attackKungFu != null ? attackKungFu.getAttackSpeed() : 0;
        return kungfuSpeed + harhAttribute().attackSpeed();
    }

    @Override
    public PlayerInterpolation captureInterpolation() {
        return PlayerInterpolation.FromPlayer(this, state().elapsedMillis());
    }


    @Override
    public void update(int delta) {
        recoveryCooldown = recoveryCooldown > delta ? recoveryCooldown - delta : 0;
        attackCooldown = attackCooldown > delta ? attackCooldown - delta : 0;
        state().update(this, delta);
    }

    public void reset(long sequence) {
        eventQueue.clear();
        emitEvent(new InputResponseMessage(sequence, SetPositionEvent.fromCreature(this)));
        realmMap().occupy(this);
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
