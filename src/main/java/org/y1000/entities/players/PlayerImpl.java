package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.players.equipment.weapon.Weapon;
import org.y1000.entities.players.kungfu.attack.AttackKungFu;
import org.y1000.entities.players.kungfu.UnnamedBufa;
import org.y1000.entities.players.kungfu.attack.basic.UnnamedQuanFa;
import org.y1000.network.ClientEventListener;
import org.y1000.network.Connection;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.AbstractCreature;
import org.y1000.entities.players.kungfu.FootKungFu;
import org.y1000.message.*;
import org.y1000.message.clientevent.ClientEvent;
import org.y1000.message.serverevent.PlayerLeftEvent;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

@Slf4j
final class PlayerImpl extends AbstractCreature implements Player,
        ClientEventListener {

    private RealmMap realmMap;

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
        eventQueue = new ConcurrentLinkedDeque<>();
        this.state = new PlayerIdleState();
        changeDirection(Direction.DOWN);
        this.connection = connection;
        this.footKungfu = new UnnamedBufa(98.91f);
        attackKungFu = UnnamedQuanFa.builder()
                .level(55f)
                .bodyArmor(1)
                .build();
        //attackKungFu = UnnamedQuanFa.create();
        this.connection.registerClientEventListener(this);
    }


    boolean hasClientEvent() {
        return !eventQueue.isEmpty();
    }

    ClientEvent takeClientEvent() {
        return eventQueue.poll();
    }

    RealmMap realmMap() {
        return realmMap;
    }

    @Override
    public Optional<FootKungFu> footKungFu() {
        return Optional.ofNullable(footKungfu);
    }

    @Override
    public Optional<AttackKungFu> attackKungFu() {
        return Optional.ofNullable(attackKungFu);
    }

    void changeState(PlayerState newState) {
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

    @Override
    public void joinReam(RealmMap realm) {
        if (realmMap != null) {
            leaveRealm();
        }
        this.realmMap = realm;
        realmMap.occupy(this);
        this.state = new PlayerIdleState();
        changeDirection(Direction.DOWN);
        emitEvent(new JoinedRealmEvent(this, coordinate()));
    }

    @Override
    public void leaveRealm() {
        if (realmMap != null) {
            realmMap.free(this);
        }
        emitEvent(new PlayerLeftEvent(this));
    }

    @Override
    public PlayerInterpolation captureInterpolation() {
        return PlayerInterpolation.FromPlayer(this, state.elapsedMillis());
    }


    @Override
    public void update(long delta) {
        state.update(this, delta);
    }

    public void reset(long sequence) {
        eventQueue.clear();
        emitEvent(new InputResponseMessage(sequence, SetPositionEvent.fromPlayer(this)));
        realmMap.occupy(this);
    }


    boolean CanMoveOneUnit(Direction direction) {
        var nextCoordinate = coordinate().moveBy(direction);
        return realmMap().movable(nextCoordinate);
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
