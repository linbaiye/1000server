package org.y1000.kungfu;

import org.y1000.kungfu.attack.AttackKungFu;
import org.y1000.kungfu.protect.ProtectKungFu;
import org.y1000.message.clientevent.ClientCreateGuildKungFuEvent;

public interface KungFuFactory {

    AttackKungFu createAttackKungFu(String name);

    ProtectKungFu createProtection(String name);

    KungFu create(String name);

    void saveGuildKungFuParameter(ClientCreateGuildKungFuEvent request);


}
