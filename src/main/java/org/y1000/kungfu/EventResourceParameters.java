package org.y1000.kungfu;

/**
 * Parameters that define an event's usage of resources, like an attack swing consuming resources,
 * a breath gaining resources.
 */
public interface EventResourceParameters {

    int power();

    int innerPower();

    int outerPower();

    int life();
}
