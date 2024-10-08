package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.y1000.message.PlayerTextEvent;
import org.y1000.realm.event.RealmTeleportEvent;
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
                                RealmEntityEventSender eventSender,
                                GroundItemManager itemManager,
                                NpcManager npcManager,
                                PlayerManager playerManager,
                                DynamicObjectManager dynamicObjectManager,
                                TeleportManager teleportManager,
                                CrossRealmEventSender crossRealmEventSender,
                                MapSdb mapSdb, int interval,
                                ChatManager chatManager,
                                Set<Integer> whitelistedIds) {
        this(id, realmMap, eventSender, itemManager, npcManager, playerManager, dynamicObjectManager, teleportManager, crossRealmEventSender, mapSdb,
                interval, LocalDateTime::now, chatManager, whitelistedIds);
    }

    public EntranceDungeonRealm(int id,
                                RealmMap realmMap,
                                RealmEntityEventSender eventSender,
                                GroundItemManager itemManager,
                                NpcManager npcManager,
                                PlayerManager playerManager,
                                DynamicObjectManager dynamicObjectManager,
                                TeleportManager teleportManager,
                                CrossRealmEventSender crossRealmEventSender,
                                MapSdb mapSdb,
                                int interval,
                                Supplier<LocalDateTime> timeSupplier,
                                ChatManager chatManager,
                                Set<Integer> whitelistedIds) {
        super(id, realmMap, eventSender, itemManager, npcManager, playerManager, dynamicObjectManager, teleportManager, crossRealmEventSender, mapSdb, chatManager, interval);
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
            builder.append(seconds % 60).append("秒");
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
    void handleTeleportEvent(RealmTeleportEvent teleportEvent) {
        if (isClosing()) {
            teleportEvent.getConnection().write(PlayerTextEvent.systemTip(teleportEvent.player(), "当前无法进入，请稍后重试。"));
            getCrossRealmEventHandler().send(new RealmTeleportEvent(teleportEvent.player(), exitRealmIt(), exitCoordinate(), teleportEvent.getConnection(), id()));
        }
        if (whitelistedIds.contains(teleportEvent.fromRealmId())) {
            acceptIfAffordableElseReject(teleportEvent);
            return;
        }
        if (isOpening()) {
            acceptIfAffordableElseReject(teleportEvent);
        } else {
            teleportEvent.getConnection().write(PlayerTextEvent.systemTip(teleportEvent.player(), buildTip()));
            getCrossRealmEventHandler().send(new RealmTeleportEvent(teleportEvent.player(), exitRealmIt(),
                    teleportEvent.rejectCoordinate().orElse(exitCoordinate()), teleportEvent.getConnection(), id()));
        }
    }

    @Override
    public String toString() {
        return "DungeonRealm{" +
                "id =" + id() +
                '}';
    }
}
