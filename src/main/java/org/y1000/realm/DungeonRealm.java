package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.y1000.entities.players.Player;
import org.y1000.message.PlayerTextEvent;
import org.y1000.realm.event.RealmTeleportEvent;
import org.y1000.sdb.MapSdb;
import org.y1000.util.Coordinate;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.function.Supplier;

@Slf4j
final class DungeonRealm extends AbstractRealm {

    private final int interval;

    private final Supplier<LocalDateTime> dateTimeSupplier;

    private boolean closing;

    public DungeonRealm(int id,
                        RealmMap realmMap,
                        RealmEntityEventSender eventSender,
                        GroundItemManager itemManager,
                        AbstractNpcManager npcManager,
                        PlayerManager playerManager,
                        DynamicObjectManager dynamicObjectManager,
                        TeleportManager teleportManager,
                        RealmEventHandler crossRealmEventHandler,
                        MapSdb mapSdb, int interval) {
        this(id, realmMap, eventSender, itemManager, npcManager, playerManager, dynamicObjectManager, teleportManager, crossRealmEventHandler, mapSdb,
                interval, LocalDateTime::now);
    }

    public DungeonRealm(int id,
                        RealmMap realmMap,
                        RealmEntityEventSender eventSender,
                        GroundItemManager itemManager,
                        AbstractNpcManager npcManager,
                        PlayerManager playerManager,
                        DynamicObjectManager dynamicObjectManager,
                        TeleportManager teleportManager,
                        RealmEventHandler crossRealmEventHandler,
                        MapSdb mapSdb,
                        int interval,
                        Supplier<LocalDateTime> timeSupplier) {
        super(id, realmMap, eventSender, itemManager, npcManager, playerManager, dynamicObjectManager, teleportManager, crossRealmEventHandler, mapSdb);
        Validate.isTrue(interval == 180000 || interval == 360000);
        Validate.notNull(timeSupplier);
        this.interval = interval;
        this.dateTimeSupplier = timeSupplier;
        closing = false;
    }

    @Override
    protected Logger log() {
        return log;
    }

    public boolean isHalfHourInterval() {
        return interval == 180000;
    }

    private boolean isOpening() {
        var minute = dateTimeSupplier.get().getMinute();
        if (isHalfHourInterval()) {
            return minute <= 4 || minute >= 30 && minute <= 34;
        } else {
            return minute <= 4;
        }
    }


    private String buildTip() {
        var now = dateTimeSupplier.get();
        LocalDateTime nextTime;
        if (isHalfHourInterval()) {
            if (now.getMinute() < 30) {
                nextTime = now.withMinute(30).withSecond(0);
            } else {
                nextTime = now.plusHours(1).withMinute(0).withSecond(0);
            }
        } else {
            nextTime = now.plusHours(1).withMinute(0).withSecond(0);
        }
        var seconds = nextTime.toEpochSecond(ZoneOffset.UTC) - now.toEpochSecond(ZoneOffset.UTC);
        StringBuilder builder = new StringBuilder("当前无法进入，");
        if (seconds / 60 != 0) {
            builder.append(seconds / 60).append("分");
        }
        if (seconds % 60 != 0) {
            builder.append(seconds % 60).append("秒");
        }
        return builder.append("后开启。").toString();
    }

    @Override
    void handleTeleportEvent(RealmTeleportEvent teleportEvent) {
        if (closing) {
            teleportEvent.getConnection().write(PlayerTextEvent.bottom(teleportEvent.player(), "当前无法进入，请稍后重试。"));
            getCrossRealmEventHandler().handle(new RealmTeleportEvent(teleportEvent.player(), exitRealmIt(), exitCoordinate(), teleportEvent.getConnection()));
        }
        acceptTeleport(teleportEvent);
        /*if (isOpening()) {
            acceptTeleport(teleportEvent);
        } else {
            teleportEvent.getConnection().write(PlayerTextEvent.bottom(teleportEvent.player(), buildTip()));
            getCrossRealmEventHandler().handle(new RealmTeleportEvent(teleportEvent.player(), exitRealmIt(), exitCoordinate(), teleportEvent.getConnection()));
        }*/
    }

    private int exitRealmIt() {
        return getMapSdb().getTargetServerID(id());
    }

    private Coordinate exitCoordinate() {
        return Coordinate.xy(getMapSdb().getTargetX(id()), getMapSdb().getTargetY(id()));
    }

    private void teleportOut(Player player) {
        onPlayerTeleport(new RealmTeleportEvent(player, exitRealmIt(), exitCoordinate()));
    }

    public void close() {
        if (closing) {
            return;
        }
        closing = true;
        playerManager().allPlayers().forEach(this::teleportOut);
    }

    @Override
    public void update() {
        doUpdateEntities();
    }

    @Override
    public String toString() {
        return "DungeonRealm{" +
                "id =" + id() +
                '}';
    }
}
