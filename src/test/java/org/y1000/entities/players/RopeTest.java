package org.y1000.entities.players;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class RopeTest {

    private Player dragger;
    private Player dragged;

    @BeforeEach
    void setUp() {
        dragger = Mockito.mock(Player.class);
        dragged = Mockito.mock(Player.class);
    }

    @Test
    void drag() {
        Rope rope = new Rope(dragged, dragger);
    }


}