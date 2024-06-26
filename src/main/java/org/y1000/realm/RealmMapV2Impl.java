package org.y1000.realm;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.y1000.Server;
import org.y1000.entities.AttackableEntity;
import org.y1000.entities.Entity;
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

    private final Map<Coordinate, Entity> occupyingCreatures;
    private final Map<Entity, Coordinate> creatureCoordinateMap;

    public RealmMapV2Impl(byte[][] movableMask, String name) {
        Objects.requireNonNull(name);
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
        occupyingCreatures = new HashMap<>();
        creatureCoordinateMap = new HashMap<>();
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
        if (occupyingCreatures.containsKey(coordinate)) {
            return false;
        }
        var cell = movableMask[coordinate.y()][coordinate.x()];
        return ((cell & 0x1) == 0) && ((cell & 0x2) == 0);
    }

    public void occupy(Entity entity) {
        if (!isInRange(entity.coordinate())) {
            throw new IllegalArgumentException("Invalid coordinate " + entity.coordinate());
        }
        free(entity);
        occupyingCreatures.put(entity.coordinate(), entity);
        creatureCoordinateMap.put(entity, entity.coordinate());
        //log.debug("{} occupied {}.", creature.id(), creature.coordinate());
    }

    public void free(Entity creature) {
        var c = creatureCoordinateMap.remove(creature);
        if (c != null) {
            occupyingCreatures.remove(c);
        }
    }

    @Override
    public String name() {
        return name;
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


    public static Optional<RealmMap> read(String name) {
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
            return Optional.of(new RealmMapV2Impl(cellMasks, name));
        } catch (Exception e) {
            log.error("Failed to read map {}.", mapName, e);
        }
        return Optional.empty();
    }
}
