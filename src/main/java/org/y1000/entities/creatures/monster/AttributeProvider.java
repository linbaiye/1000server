package org.y1000.entities.creatures.monster;

public interface AttributeProvider {
    int life();
    int avoidance();
    int recovery();
    int attackSpeed();
    int wanderingRange();
    int armor();
    int hit();
    String attackSound();
    int damage();
}
