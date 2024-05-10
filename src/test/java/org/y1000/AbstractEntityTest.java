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

    public static void main(String[] args) {
        int n = 0;
        var k = new int[n][n];
        System.out.println(k.length);
    }
}
