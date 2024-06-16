package org.y1000.kungfu.protect;

public interface ProtectionFixedParameters {

    default int bodyArmor() {
        return 0;
    }

    default int headArmor() {
        return 0;
    }

    default int armArmor() {
        return 0;
    }

    default int legArmor() {
        return 0;
    }

    String enableSound();

    String disableSound();
}

