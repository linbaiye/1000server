package org.y1000;

import org.mockito.Mockito;
import org.y1000.entities.Entity;
import org.y1000.util.Coordinate;

import static org.mockito.Mockito.when;

public abstract class AbstractEntityTest {

    protected Entity mockEntity(Coordinate coordinate) {
        Entity entity = Mockito.mock(Entity.class);
        when(entity.coordinate()).thenReturn(coordinate);
        return entity;
    }
}
