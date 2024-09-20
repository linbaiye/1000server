package org.y1000.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.y1000.kungfu.attack.AttackKungFuParameters;

@Data
@Entity
@Builder
@Table(name = "guild_kungfu")
@NoArgsConstructor
@AllArgsConstructor
public class GuildKungFuParametersProvider implements AttackKungFuParameters {
    private Long id;
    private String name;
    private int speed;
    private int recovery;
    private int avoid;
    private int headDamage;
    private int armDamage;
    private int bodyDamage;
    private int legDamage;
    private int headArmor;
    private int armArmor;
    private int bodyArmor;
    private int legArmor;
    private int powerToSwing;
    private int innerPowerToSwing;
    private int outerPowerToSwing;
    private int lifeToSwing;
    private int swingSound;
    private int strikeSound;
    @Override
    public int powerToSwing() {
        return powerToSwing;
    }

    @Override
    public int innerPowerToSwing() {
        return innerPowerToSwing;
    }

    @Override
    public int recovery() {
        return recovery;
    }

    @Override
    public int outerPowerToSwing() {
        return outerPowerToSwing;
    }

    @Override
    public int lifeToSwing() {
        return lifeToSwing;
    }

    @Override
    public int headDamage() {
        return headDamage;
    }

    @Override
    public int bodyDamage() {
        return bodyDamage;
    }

    @Override
    public int armDamage() {
        return armDamage;
    }

    @Override
    public int legDamage() {
        return legDamage;
    }

    @Override
    public int attackSpeed() {
        return speed;
    }

    @Override
    public int avoidance() {
        return avoid;
    }

    @Override
    public int strikeSound() {
        return strikeSound;
    }

    @Override
    public int swingSound() {
        return swingSound;
    }

    @Override
    public int bodyArmor() {
        return bodyArmor;
    }

    @Override
    public int headArmor() {
        return headArmor;
    }

    @Override
    public int armArmor() {
        return armArmor;
    }

    @Override
    public int legArmor() {
        return legArmor;
    }

    @Override
    public int effectId() {
        return 0;
    }
}
