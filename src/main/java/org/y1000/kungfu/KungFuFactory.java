package org.y1000.kungfu;

import org.y1000.kungfu.attack.AttackKungFu;
import org.y1000.kungfu.protect.ProtectKungFu;
import org.y1000.message.clientevent.ClientCreateGuildKungFuEvent;

public interface KungFuFactory {

    AttackKungFu createAttackKungFu(String name);

    ProtectKungFu createProtection(String name);

    KungFu create(String name);

    AttackKungFu createGuildKungFu(ClientCreateGuildKungFuEvent request);

    /**
     * Check if a guild kungfu can be created with the request.
     * @param request
     * @return null if specification qualified, specific reason if not.
     */
    String checkGuildKungFuSpecification(ClientCreateGuildKungFuEvent request);
}
