package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.PlayerTextMessage;
import org.y1000.input.Login;
import org.y1000.repository.PlayerRepository;
import org.y1000.sdb.MapSdb;
import org.y1000.util.Coordinate;

import java.time.LocalDateTime;
import java.util.function.Supplier;


@Slf4j
final class DungeonRealm extends AbstractRealm {
    private boolean closing;
    private final int durationSeconds;
    private final boolean[] minuteAnnouncement;
    private final Supplier<LocalDateTime> timeSupplier;
    private final LocalDateTime closeTime;

    public DungeonRealm(int id, RealmMap realmMap,
                        GroundItemManager itemManager,
                        NpcManager npcManager,
                        PlayerManager playerManager,
                        DynamicObjectManager dynamicObjectManager,
                        TeleportManager teleportManager,
                        RealmEventSender crossRealmEventSender,
                        MapSdb mapSdb,
                        PlayerRepository playerRepository,
                        int interval) {
        this(id, realmMap, itemManager, npcManager, playerManager, dynamicObjectManager, teleportManager, crossRealmEventSender, mapSdb, playerRepository, interval, LocalDateTime::now);
    }

    public DungeonRealm(int id, RealmMap realmMap,
                        GroundItemManager itemManager,
                        NpcManager npcManager,
                        PlayerManager playerManager,
                        DynamicObjectManager dynamicObjectManager,
                        TeleportManager teleportManager,
                        RealmEventSender crossRealmEventSender,
                        MapSdb mapSdb,
                        PlayerRepository playerRepository,
                        int interval,
                        Supplier<LocalDateTime> timeSupplier) {
        super(id, realmMap, itemManager, npcManager, playerManager, dynamicObjectManager, teleportManager, crossRealmEventSender, mapSdb, playerRepository);
        this.durationSeconds = interval / 100;
        Validate.isTrue(durationSeconds == 1800 || durationSeconds == 3600);
        if (!(playerManager instanceof DungeonPlayerManager dungeonPlayerManager)) {
            throw new IllegalArgumentException();
        }
        dungeonPlayerManager.setDeadPlayerTeleportor(this::teleportPlayerOut);
        int minutes = durationSeconds == 1800 ? 30 : 60;
        minuteAnnouncement = new boolean[minutes + 1];
        for (int i = 0; i <= minutes; i++) {
            minuteAnnouncement[i] = false;
        }
        this.timeSupplier = timeSupplier;
        closeTime = computeCloseTime(timeSupplier.get(), minutes);
        closing = false;
    }

    private LocalDateTime computeCloseTime(LocalDateTime now, int openMinutes) {
        if (openMinutes == 30) {
            if ((now.getMinute() == 29 || now.getMinute() == 59) && now.getSecond() >= 58)
                return now.plusMinutes(30);
            return now.getMinute() > 30 ? now.withMinute(59).withSecond(58) : now.withMinute(29).withSecond(58);
        } else {
            if (now.getMinute() == 59 && now.getSecond() >= 58)
                return now.plusHours(1);
            return now.withMinute(59).withSecond(58);
        }
    }

    @Override
    public void update() {
        doUpdateEntities();
        LocalDateTime time = timeSupplier.get();
        sendBroadcast(time.getMinute());
    }

    private void sendBroadcast(int minute) {
        if (minuteAnnouncement[minute]) {
            return;
        }
        int minuteLeft;
        if (isHalfHourInterval())
            minuteLeft = minute > 30 ? 60 - minute : 30 - minute;
        else
            minuteLeft = 60 - minute;
        var text = "剩下" + minuteLeft + "分钟。";
        minuteAnnouncement[minute] = true;
        getPlayerManager().allPlayers().forEach(p -> p.sendEvent(PlayerTextMessage.systip(p, text)));
    }

    public boolean needToClose() {
        if (closing)
            return false;
        var now = timeSupplier.get();
        return closeTime.isBefore(now) || closeTime.equals(now);
    }


    public boolean isHalfHourInterval() {
        return durationSeconds == 1800;
    }

    @Override
    Logger log() {
        return log;
    }


    @Override
    protected void handleLogin(Login login) {
        acceptLogin(login.playerId(), login.connection(), null);
        //sendCrossRealmEvent(new ProxyLoginEvent(exitRealmIt(), login.playerId(), exitCoordinate(), login.connection()));
    }

    private int exitRealmIt() {
        return getMapSdb().getTargetServerID(id());
    }

    private Coordinate exitCoordinate() {
        return Coordinate.xy(getMapSdb().getTargetX(id()), getMapSdb().getTargetY(id()));
    }


    private void teleportPlayerOut(Player player)  {
        teleportTo(player, exitRealmIt(), exitCoordinate());
    }

    @Override
    public void shutdown() {
        playerManager().shutdown();
    }

    public void close() {
        if (closing) {
            return;
        }
        closing = true;
        log().debug("Set to closing.");
        playerManager().allPlayers().forEach(this::teleportPlayerOut);
    }


    @Override
    public String toString() {
        return "[Dungeon " + id() + "]";
    }

    @Override
    public void init() {
        doInit();
    }

}
