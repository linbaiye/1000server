package org.y1000.entities.teleport;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.PlayerTextMessage;
import org.y1000.sdb.CreateGateSdb;
import org.y1000.util.Coordinate;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

public abstract class AbstractTeleport implements Teleport {

    private final Set<Coordinate> coordinates;

    private final long id;

    private final Coordinate coordinate;

    private final int toRealm;

    private final Coordinate toCoordinate;

    private final Coordinate rejectCoordinate;

    private final int realmId;

    private final List<TeleportCost> costs;

    private final TeleportHandler teleportHandler;

    private final int activeSeconds;

    private final int regenSeconds;

    private final Supplier<LocalDateTime> timeSupplier;

    public AbstractTeleport(long id,
                            String idName,
                            CreateGateSdb createGateSdb,
                            TeleportHandler teleportHandler,
                            int realmId) {
        this(id, idName, createGateSdb, teleportHandler, realmId, LocalDateTime::now);
    }

    public AbstractTeleport(long id,
                            String idName,
                            CreateGateSdb createGateSdb,
                            TeleportHandler teleportHandler,
                            int realmId, Supplier<LocalDateTime> timeSupplier) {
        Validate.notNull(idName);
        Validate.notNull(createGateSdb);
        this.costs = parseCosts(idName, createGateSdb);
        this.id = id;
        this.coordinate = parseCoordinate(idName, createGateSdb);
        this.toCoordinate = Coordinate.xy(createGateSdb.getTX(idName), createGateSdb.getTY(idName));
        this.toRealm = createGateSdb.getServerId(idName);
        this.coordinates = parseCoordinates(idName, coordinate, createGateSdb);
        this.teleportHandler = teleportHandler;
        Validate.notNull(coordinate);
        Validate.notNull(toCoordinate);
        this.rejectCoordinate = createGateSdb.getEX(idName) != null ? Coordinate.xy(createGateSdb.getEX(idName), createGateSdb.getEY(idName)) : null;
        this.realmId = realmId;
        if (!this.costs.isEmpty())
            Validate.isTrue(rejectCoordinate != null);
        activeSeconds = createGateSdb.getActiveInterval(idName) / 100;
        regenSeconds = createGateSdb.getRegenInterval(idName) / 100;
        if (regenSeconds != 0) {
            Validate.isTrue(regenSeconds == 3600 || regenSeconds == 1800);
            Validate.isTrue(rejectCoordinate != null);
        }
        if (activeSeconds != 0)
            Validate.isTrue(regenSeconds != 0);
        this.timeSupplier = timeSupplier;
    }

    public long id() {
        return id;
    }

    @Override
    public Coordinate coordinate() {
        return coordinate;
    }

    @Override
    public Set<Coordinate> coordinates() {
        return coordinates;
    }

    private String buildTip() {
        var now = timeSupplier.get();
        LocalDateTime nextTime;
        if (regenSeconds == 1800) {
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

    private boolean isTimeAllowedToEnter() {
        return true;
        /*if (activeSeconds == 0)
            return true;
        LocalDateTime now = timeSupplier.get();
        var curSec = now.getMinute() * 60 + now.getSecond();
        if (regenSeconds == 1800 && curSec > 1800) {
            curSec -= 1800;
        }
        return curSec <= activeSeconds;*/
    }

    private String checkCosts(Player player) {
        for (TeleportCost cost : costs) {
            var ret = cost.check(player);
            if (ret != null)
                return ret;
        }
        return null;
    }

    @Override
    public void onPlayerEntered(Player player) {
        if (player == null) {
            return;
        }
        if (!isTimeAllowedToEnter()) {
            player.sendEvent(PlayerTextMessage.systip(player, buildTip()));
            teleportHandler.teleportTo(player, realmId, rejectCoordinate);
            return;
        }
        String ret = checkCosts(player);
        if (ret != null) {
            player.sendEvent(PlayerTextMessage.systip(player, ret));
            teleportHandler.teleportTo(player, realmId, rejectCoordinate);
            return;
        }
        costs.forEach(c -> c.charge(player));
        teleportHandler.teleportTo(player, toRealm, toCoordinate);
    }

    private static List<TeleportCost> parseCosts(String idName, CreateGateSdb createGateSdb) {
        String needItem = createGateSdb.getNeedItem(idName);
        if (StringUtils.isEmpty(needItem)) {
            return Collections.emptyList();
        }
        String[] split = needItem.split(":");
        if (split.length != 2) {
            return Collections.emptyList();
        }
        return Collections.singletonList(new ItemCost(split[0].trim(), Integer.parseInt(split[1].trim())));
    }


    private static Set<Coordinate> parseCoordinates(String name, Coordinate coordinate, CreateGateSdb gateSdb) {
        int width = gateSdb.getWidth(name);
        if (width <= 1) {
            return Collections.singleton(coordinate);
        }
        var index = width - 1;
        Set<Coordinate> coordinates  = new HashSet<>();
        coordinates.add(coordinate);
        for (int i = -index; i <= index ; i++) {
            for (int j = -index; j <= index; j++) {
                coordinates.add(coordinate.move(i, j));
            }
        }
        return coordinates;
    }

    private static Coordinate parseCoordinate(String idName, CreateGateSdb gateSdb) {
        var coordinate = Coordinate.xy(gateSdb.getX(idName), gateSdb.getY(idName));
        if (!coordinate.equals(Coordinate.Empty)) {
            return coordinate;
        }
        String randomPos = gateSdb.getRandomPos(idName);
        String[] split = randomPos.split(":");
        int total = split.length / 2;
        var index = ThreadLocalRandom.current().nextInt(0, total);
        return Coordinate.xy(Integer.parseInt(split[index * 2]), Integer.parseInt(split[index * 2 + 1]));
    }
}
