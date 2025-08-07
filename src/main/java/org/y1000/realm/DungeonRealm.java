package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.PlayerTextMessage;
import org.y1000.message.input.ClientFoundGuildEvent;
import org.y1000.message.input.Login;
import org.y1000.realm.event.BroadcastTextEvent;
import org.y1000.repository.PlayerRepository;
import org.y1000.sdb.MapSdb;
import org.y1000.util.Coordinate;

import java.time.LocalDateTime;
import java.util.function.Supplier;


@Slf4j
final class DungeonRealm extends AbstractRealm {
    private boolean closing;
    private final int durationSeconds;
    private String openAnnouncement;
    private final boolean[] minuteAnnouncement;
    private final Supplier<LocalDateTime> timeSupplier;

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
        openAnnouncement = mapSdb.getAnnouncement(id);
        dungeonPlayerManager.setDeadPlayerTeleportor(this::teleportPlayerOut);
        int max = durationSeconds == 1800 ? 30 : 60;
        minuteAnnouncement = new boolean[max + 1];
        for (int i = 0; i <= max; i++) {
            minuteAnnouncement[i] = false;
        }
        this.timeSupplier = timeSupplier;
    }


    private boolean isTimeToAnnounce(int minute, int second) {
        if (isHalfHourInterval()) {
            return (minute == 25 || minute == 55) && second > 5;
        } else {
            return minute == 02 && second > 0;
        }
    }

    @Override
    public void update() {
        doUpdateEntities();
        LocalDateTime time = timeSupplier.get();
        sendBroadcast(time.getMinute(), time.getSecond());
    }

    private void sendBroadcast(int minute, int second) {
        if (openAnnouncement != null && isTimeToAnnounce(minute, second)) {
            sendCrossRealmEvent(BroadcastTextEvent.leftUp(openAnnouncement + "将在5分钟后开放。"));
            openAnnouncement = null;
        }
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

    public boolean needToClose(int minute, int second) {
        if (closing)
            return false;
        if (isHalfHourInterval()) {
            return (minute == 29 || minute == 59) && second >= 58;
        } else {
            return minute == 59 && second >= 58;
        }
    }


    public boolean isHalfHourInterval() {
        return durationSeconds == 1800;
    }

    @Override
    Logger log() {
        return log;
    }

    @Override
    void handleGuildCreation(Player source, ClientFoundGuildEvent event) {

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
        playerManager().allPlayers().forEach(this::teleportPlayerOut);
    }

    @Override
    public void init() {
        doInit();
    }
}
