package org.y1000.message.clientevent;

import lombok.Builder;
import lombok.Data;
import org.y1000.kungfu.attack.AttackKungFuType;

@Data
@Builder
public class ClientCreateGuildKungFuEvent implements ClientEvent {

    private final String name;
    private final int speed;
    private final int recovery;
    private final int avoid;
    private final int headDamage;
    private final int armDamage;
    private final int bodyDamage;
    private final int legDamage;
    private final int headArmor;
    private final int armArmor;
    private final int bodyArmor;
    private final int legArmor;
    private final int powerToSwing;
    private final int innerPowerToSwing;
    private final int outerPowerToSwing;
    private final int lifeToSwing;
    
    private final AttackKungFuType type;
}
