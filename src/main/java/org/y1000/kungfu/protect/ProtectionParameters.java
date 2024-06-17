package org.y1000.kungfu.protect;

import org.y1000.kungfu.ArmorParameters;
import org.y1000.kungfu.FiveSecondsParameters;
import org.y1000.kungfu.KeepParameters;

public interface ProtectionParameters extends ArmorParameters,
        KeepParameters, FiveSecondsParameters {

    String enableSound();

    String disableSound();


}

