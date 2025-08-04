package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.y1000.entities.players.event.PlayerTextMessage;
import org.y1000.message.input.Login;
import org.y1000.realm.event.RealmTeleportEvent;
import org.y1000.repository.PlayerRepository;
import org.y1000.sdb.MapSdb;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Set;
import java.util.function.Supplier;

@Slf4j
final class EntranceDungeonRealm extends AbstractDungeonRealm {
    private final Supplier<LocalDateTime> dateTimeSupplier;
    private final Set<Integer> whitelistedIds;

    public EntranceDungeonRealm(int id,
                                RealmMap realmMap,
                                GroundItemManager itemManager,
                                NpcManager npcManager,
                                PlayerManager playerManager,
                                DynamicObjectManager dynamicObjectManager,
                                TeleportManager teleportManager,
                                RealmEventSender crossRealmEventSender,
                                MapSdb mapSdb, int interval,
                                Set<Integer> whitelistedIds,
                                PlayerRepository playerRepository) {
        this(id, realmMap, itemManager, npcManager, playerManager, dynamicObjectManager, teleportManager, crossRealmEventSender, mapSdb,
                interval, LocalDateTime::now, whitelistedIds, playerRepository);
    }

    public EntranceDungeonRealm(int id,
                                RealmMap realmMap,
                                GroundItemManager itemManager,
                                NpcManager npcManager,
                                PlayerManager playerManager,
                                DynamicObjectManager dynamicObjectManager,
                                TeleportManager teleportManager,
                                RealmEventSender crossRealmEventSender,
                                MapSdb mapSdb,
                                int interval,
                                Supplier<LocalDateTime> timeSupplier,
                                Set<Integer> whitelistedIds, PlayerRepository playerRepository) {
        super(id, realmMap, itemManager, npcManager, playerManager, dynamicObjectManager, teleportManager, crossRealmEventSender, mapSdb, interval, playerRepository);
        Validate.notNull(timeSupplier);
        this.dateTimeSupplier = timeSupplier;
        this.whitelistedIds = whitelistedIds != null ? whitelistedIds : Collections.emptySet();
    }

    @Override
    protected Logger log() {
        return log;
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
            builder.append(String.format("%02d秒", seconds % 60));
        }
        return builder.append("后开启。").toString();
    }

    private boolean isOpening() {
        var minute = dateTimeSupplier.get().getMinute();
        if (isHalfHourInterval()) {
            return minute <= 4 || minute >= 30 && minute <= 34;
        } else {
            return minute <= 4;
        }
    }

    @Override
    public void handleTeleportEvent(RealmTeleportEvent teleportEvent) {
        if (isClosing()) {
            teleportEvent.getConnection().writeAndFlush(PlayerTextMessage.bottom(teleportEvent.player(), "当前无法进入，请稍后重试。"));
            getCrossRealmEventHandler().send(RealmTeleportEvent.teleportOut(teleportEvent.player(), exitRealmIt(), exitCoordinate(), teleportEvent.getConnection()));
            return;
        }
        if (whitelistedIds.contains(teleportEvent.fromRealmId()) || isOpening()) {
            getPlayerManager().teleportIn(teleportEvent.player(), this, teleportEvent.toCoordinate(), teleportEvent.getConnection());
        } else {
            teleportEvent.getConnection().writeAndFlush(PlayerTextMessage.bottom(teleportEvent.player(), buildTip()));
            getCrossRealmEventHandler().send(RealmTeleportEvent.teleportOut(teleportEvent.player(), exitRealmIt(), exitCoordinate(), teleportEvent.getConnection()));
        }
    }

    @Override
    protected void handleLogin(Login login) {
        acceptLogin(login);
    }

    @Override
    public String toString() {
        return "DungeonRealm{" +
                "id =" + id() +
                '}';
    }
}
