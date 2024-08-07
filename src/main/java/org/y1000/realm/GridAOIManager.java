package org.y1000.realm;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.Entity;
import org.y1000.util.Coordinate;

import java.util.*;
import java.util.stream.Collectors;

final class GridAOIManager implements AOIManager {
    private final List<Entity>[][] mapGrids;
    private final Map<Entity, Coordinate> currentCoordinates;
    private final int width;
    private final int height;

    public GridAOIManager(int mapWidth, int mapHeight) {
        Validate.isTrue(mapWidth > 1);
        Validate.isTrue(mapHeight > 1);
        mapGrids = new List[mapHeight][mapWidth];
        currentCoordinates = new HashMap<>();
        width = mapWidth;
        height = mapHeight;
    }


    private Set<Entity> filterVisible(Entity entity, Coordinate coordinate) {
        int yStart = Math.max(0, coordinate.y() - entity.VISIBLE_Y_RANGE);
        int yEnd = Math.min(height - 1, coordinate.y() + entity.VISIBLE_Y_RANGE);
        Set<Entity> result = new HashSet<>();
        for (int y = yStart; y <= yEnd; y++) {
            int start = Math.max(0, coordinate.x() - entity.VISIBLE_X_RANGE);
            int end = Math.min(width - 1, coordinate.x() + entity.VISIBLE_X_RANGE);
            for (int x = start; x <= end; x++) {
                List<Entity> entities = mapGrids[y][x];
                if (entities != null && !entities.isEmpty()) {
                    result.addAll(entities);
                }
            }
        }
        result.remove(entity);
        return result;
    }

    private Set<Entity> filterVisible(Entity entity) {
        return filterVisible(entity, entity.coordinate());
    }

    private void cleanEntity(Entity entity) {
        Coordinate coordinate = currentCoordinates.remove(entity);
        if (coordinate != null) {
            mapGrids[coordinate.y()][coordinate.x()].remove(entity);
        }
    }


    @Override
    public Set<Entity> add(Entity entity) {
        Validate.notNull(entity);
        Validate.isTrue(entity.coordinate().x() <= width && entity.coordinate().y() <= height);
        if (contains(entity)) {
            return Collections.emptySet();
        }
        Coordinate coordinate = entity.coordinate();
        if (mapGrids[coordinate.y()][coordinate.x()] == null) {
            mapGrids[coordinate.y()][coordinate.x()] = new ArrayList<>();
        }
        mapGrids[coordinate.y()][coordinate.x()].add(entity);
        currentCoordinates.put(entity, entity.coordinate());
        return filterVisible(entity);
    }

    @Override
    public boolean contains(Entity entity) {
        return currentCoordinates.containsKey(entity);
    }

    @Override
    public <E extends Entity> Set<E> filterVisibleEntities(Entity entity, Class<E> type) {
        Validate.notNull(entity);
        Validate.notNull(type);
        if (!contains(entity)) {
            return Collections.emptySet();
        }
        return filterVisible(entity, currentCoordinates.get(entity))
                .stream().filter(e -> type.isAssignableFrom(e.getClass()))
                .map(type::cast)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean outOfScope(Entity source, Entity target) {
        Validate.notNull(source);
        Validate.notNull(target);
        return contains(source) && !source.canBeSeenAt(target.coordinate());
    }

    @Override
    public Set<Entity> update(Entity entity) {
        Validate.notNull(entity);
        if (!contains(entity)) {
            return Collections.emptySet();
        }
        Set<Entity> previousVisible = filterVisible(entity, currentCoordinates.get(entity));
        cleanEntity(entity);
        Set<Entity> currentVisible = add(entity);
        Set<Entity> result = new HashSet<>();
        previousVisible.stream().filter(e -> !currentVisible.contains(e)).forEach(result::add);
        currentVisible.stream().filter(e -> !previousVisible.contains(e)).forEach(result::add);
        return result;
    }

    @Override
    public void remove(Entity entity) {
        Validate.notNull(entity);
        cleanEntity(entity);
    }
}
