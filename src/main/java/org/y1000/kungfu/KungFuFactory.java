package org.y1000.kungfu;

import org.y1000.input.ApplyGuildKungFuInput;
import org.y1000.kungfu.attack.AttackKungFu;

public interface KungFuFactory {

    AttackKungFu createAttackKungFu(String name);

    KungFu create(String name);

    void registerAttackKungFuParameters(ApplyGuildKungFuInput input);

}
