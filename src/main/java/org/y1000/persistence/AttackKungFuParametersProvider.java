package org.y1000.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.y1000.kungfu.KungFuType;
import org.y1000.kungfu.attack.AttackKungFuParameters;
import org.y1000.kungfu.attack.AttackKungFuType;

@Data
@Entity
@Builder
@Table(name = "attack_kungfu")
@NoArgsConstructor
@AllArgsConstructor
public class AttackKungFuParametersProvider implements AttackKungFuParameters {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private AttackKungFuType type;
    private int attackSpeed;
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
    private int swingPower;
    private int swingInnerPower;
    private int swingOuterPower;
    private int swingLife;
    private int swingSound;
    private int strikeSound;
    private int effectColor;
    @Override
    public int powerToSwing() {
        return swingPower;
    }

    @Override
    public int innerPowerToSwing() {
        return swingInnerPower;
    }

    @Override
    public int recovery() {
        return recovery;
    }

    @Override
    public int outerPowerToSwing() {
        return swingOuterPower;
    }

    @Override
    public int lifeToSwing() {
        return swingLife;
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
        return attackSpeed;
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
        return effectColor;
    }
}
