package org.y1000.item;

import java.util.Optional;

public interface ArmorItemAttributeProvider {

    String dropSound();

    String eventSound();

    int avoidance();

    int headArmor();

    int armor();

    int armArmor();

    int legArmor() ;

    int recovery();

    boolean isMale();

    String description();

    default int color() {
        return 0;
    }

}
