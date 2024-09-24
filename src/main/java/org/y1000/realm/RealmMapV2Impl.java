package org.y1000.realm;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.y1000.Server;
import org.y1000.entities.Entity;
import org.y1000.entities.objects.DynamicObject;
import org.y1000.entities.players.Player;
import org.y1000.entities.teleport.Teleport;
import org.y1000.util.Coordinate;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

@Slf4j
final class RealmMapV2Impl implements RealmMap {

    private static final int HEADER_SIZE = 28;

    private static final int BLOCK_SIZE = 40;

    // 20bytes for block header.
    // block id(16 bytes), changedCount(4 bytes)
    // cells[1600 * 12 bytes]
    private static final int MAP_BLOCK_DATA_SIZE = 20 + BLOCK_SIZE * BLOCK_SIZE * 12;

    private static final int MAP_CELL_SIZE = 12;

    private final byte[][] movableMask;

    private final int height;
    private final int width;

    private final String name;

    private final String tile;

    private final String object;

    private final String roof;

    private final Map<Coordinate, Set<Entity>> coordinateEntityMap;
    private final Map<Entity, Coordinate> entityCoordinateMap;

    private final Map<Coordinate, Teleport> teleportMap;


    public RealmMapV2Impl(byte[][] movableMask, String name) {
        this(movableMask, name, "", "", "");
    }

    public RealmMapV2Impl(byte[][] movableMask, String name, String tile, String obj, String rof) {
        Objects.requireNonNull(name);
        Validate.notNull(tile);
        Validate.notNull(obj);
        if (movableMask.length == 0) {
            throw new IllegalArgumentException();
        }
        if (movableMask[0].length == 0) {
            throw new IllegalArgumentException();
        }
        this.name = name;
        this.movableMask = movableMask;
        this.height = movableMask.length;
        this.width = movableMask[0].length;
        coordinateEntityMap = new HashMap<>();
        entityCoordinateMap = new HashMap<>();
        teleportMap = new HashMap<>();
        this.tile = tile;
        this.object = obj;
        this.roof = StringUtils.isEmpty(rof) ? null : rof;
    }

    private boolean isInRange(Coordinate coordinate) {
        return coordinate.x() >= 0 && coordinate.x() <= width
                && coordinate.y() >= 0 && coordinate.y() <= height;
    }

    @Override
    public boolean movable(Coordinate coordinate) {
        if (!isInRange(coordinate)) {
            return false;
        }
        if (!coordinateEntityMap.getOrDefault(coordinate, Collections.emptySet()).isEmpty()) {
            return false;
        }
        return tileMovable(coordinate);
    }

    @Override
    public boolean tileMovable(Coordinate coordinate) {
        if (coordinate == null) {
            return false;
        }
        var cell = movableMask[coordinate.y()][coordinate.x()];
        return ((cell & 0x1) == 0) && ((cell & 0x2) == 0);
    }

    public void occupy(Entity entity) {
        Validate.notNull(entity);
        if (!isInRange(entity.coordinate())) {
            throw new IllegalArgumentException("Invalid coordinate " + entity.coordinate());
        }
        if (teleportMap.containsKey(entity.coordinate()) && entity instanceof Player player) {
            teleportMap.get(entity.coordinate()).teleport(player);
            return;
        }
        free(entity);
        coordinateEntityMap.computeIfAbsent(entity.coordinate(), c -> new HashSet<>()).add(entity);
        entityCoordinateMap.put(entity, entity.coordinate());
    }


    private void doRemoveCoordinate(Entity entity, Coordinate coordinate) {
        Set<Entity> entities = coordinateEntityMap.getOrDefault(coordinate, Collections.emptySet());
        entities.remove(entity);
        if (entities.isEmpty()) {
            coordinateEntityMap.remove(coordinate);
        }
    }

    public void free(Entity entity) {
        Validate.notNull(entity);
        var c = entityCoordinateMap.remove(entity);
        if (c != null) {
            doRemoveCoordinate(entity, c);
        }
    }


    @Override
    public void occupy(DynamicObject dynamicObject) {
        Validate.notNull(dynamicObject);
        if (dynamicObject.occupyingCoordinates().stream().anyMatch(c -> !isInRange(c)))  {
            throw new IllegalArgumentException("Coordinate out of range.");
        }
        entityCoordinateMap.put(dynamicObject, dynamicObject.coordinate());
        for (Coordinate coordinate : dynamicObject.occupyingCoordinates()) {
            coordinateEntityMap.computeIfAbsent(coordinate, c -> new HashSet<>()).add(dynamicObject);
        }
    }

    @Override
    public void free(DynamicObject dynamicObject) {
        Validate.notNull(dynamicObject);
        entityCoordinateMap.remove(dynamicObject);
        for (Coordinate coordinate : dynamicObject.occupyingCoordinates()) {
            doRemoveCoordinate(dynamicObject, coordinate);
        }
    }

    @Override
    public String mapFile() {
        return name;
    }

    @Override
    public String roofFile() {
        return roof;
    }

    @Override
    public String objectFile() {
        return object;
    }

    @Override
    public String tileFile() {
        return tile;
    }

    @Override
    public void addTeleport(Teleport teleport) {
        if (teleport == null) {
            return;
        }
        Set<Coordinate> coordinates = teleportMap.keySet();
        if (teleport.teleportCoordinates().stream().anyMatch(coordinates::contains)) {
            throw new IllegalStateException("Conflict coordinate for teleport " + teleport);
        }
        teleport.teleportCoordinates().forEach(p -> teleportMap.put(p, teleport));
    }


    @Builder
    @Getter
    private static class Header {
        private final String idString;
        private final int blockSize;
        private final int width;
        private final int height;

        private static Header parse(ByteBuffer headerBuffer) {
            byte[] idbytes = new byte[16];
            headerBuffer.get(idbytes);
            String idString = new String(idbytes);
            if ("ATZMAP2".equals(idString)) {
                throw new IllegalArgumentException("Invalid idString.");
            }
            return Header.builder().idString(idString)
                    .blockSize(headerBuffer.getInt())
                    .width(headerBuffer.getInt())
                    .height(headerBuffer.getInt())
                    .build();
        }
    }


    public static Optional<RealmMap> read(String name, String tile, String obj, String rof) {
        Validate.notNull(name);
        Validate.notNull(tile);
        Validate.notNull(obj);
        Validate.notNull(rof);
        String mapName = name.endsWith(".map") ? name : name + ".map";
        if (!mapName.startsWith("/maps/")) {
            mapName = "/maps/" + mapName;
        }
        try (InputStream is = Server.class.getResourceAsStream(mapName)) {
            if (is == null) {
                log.error("Map {} does not exist.", mapName);
                return Optional.empty();
            }
            ByteBuffer headerBinary = ByteBuffer.allocate(HEADER_SIZE);
            headerBinary.order(ByteOrder.LITTLE_ENDIAN);
            if (is.read(headerBinary.array(), 0, headerBinary.capacity()) != headerBinary.capacity()) {
                log.error("Map {} contains invalid headerBinary.", mapName);
                return Optional.empty();
            }
            Header header = Header.parse(headerBinary);
            byte[][] cellMasks = new byte[header.height][header.width];
            byte[] cellBinary = new byte[MAP_CELL_SIZE];
            ByteBuffer blockData = ByteBuffer.allocate(MAP_BLOCK_DATA_SIZE);
            blockData.order(ByteOrder.LITTLE_ENDIAN);
            for (int h = 0; h < header.height / BLOCK_SIZE; h++) {
                for (int w = 0; w < header.width / BLOCK_SIZE; w++) {
                    if (is.read(blockData.array()) != MAP_BLOCK_DATA_SIZE) {
                        log.error("Failed to read map block.");
                        return Optional.empty();
                    }
                    for (int y = 0; y < BLOCK_SIZE; y++) {
                        for (int x = 0; x < BLOCK_SIZE; x++) {
                            blockData.get(20 + (y * BLOCK_SIZE + x) * MAP_CELL_SIZE, cellBinary);
                            cellMasks[h *  BLOCK_SIZE + y][w * BLOCK_SIZE + x] = cellBinary[11];
                        }
                    }
                }
            }
            return Optional.of(new RealmMapV2Impl(cellMasks, name, tile, obj, rof));
        } catch (Exception e) {
            log.error("Failed to read map {}.", mapName, e);
        }
        return Optional.empty();
    }
}
