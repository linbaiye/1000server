package org.y1000.kungfu;

import org.y1000.kungfu.attack.AttackKungFu;
import org.y1000.kungfu.protect.ProtectKungFu;

public interface KungFuFactory {

    AttackKungFu createAttackKungFu(String name);

    ProtectKungFu createProtection(String name);
}
