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
public final class PlayerImpl extends AbstractCreature implements Player,
        ClientEventListener {

    private Realm realm;

    private PlayerState state;

    private final Queue<ClientEvent> eventQueue;

    private final Connection connection;

    private AttackKungFu attackKungFu;

    private FootKungFu footKungfu;

    private Weapon weapon;

    public PlayerImpl(long id, Coordinate coordinate,
                      Direction direction,
                      String name, Connection connection) {
        super(id, coordinate, direction, name);
        eventQueue = new ConcurrentLinkedQueue<>();
        this.state = new PlayerIdleState();
        changeDirection(Direction.DOWN);
        this.connection = connection;
        this.footKungfu = new UnnamedBufa(98.91f);
        attackKungFu = UnnamedQuanFa.builder()
                .level(55f)
                .bodyArmor(1)
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

    public void changeState(PlayerState newState) {
        state = newState;
    }

    @Override
    public State stateEnum() {
        return state.stateEnum();
    }

    @Override
    public Connection connection() {
        return connection;
    }

    public PlayerState currentState() {
        return state;
    }

    @Override
    public void joinReam(RealmImpl realm) {
        if (this.realm != null) {
            leaveRealm();
        }
        this.realm = realm;
        realmMap().occupy(this);
        this.state = new PlayerIdleState();
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
    public PlayerInterpolation captureInterpolation() {
        return PlayerInterpolation.FromPlayer(this, state.elapsedMillis());
    }


    @Override
    public void update(int delta) {
        state.update(this, delta);
    }

    public void reset(long sequence) {
        eventQueue.clear();
        emitEvent(new InputResponseMessage(sequence, SetPositionEvent.fromPlayer(this)));
        realmMap().occupy(this);
    }


    @Override
    public String toString() {
        return "PlayerImpl{" +
                "id=" + id() +
                ", coordinate=" + coordinate() +
                ", direction=" + direction() +
                ", state=" + state.stateEnum() +
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
