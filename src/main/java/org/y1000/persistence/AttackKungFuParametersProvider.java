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
@Table(name = "guild_kung_fu")
@NoArgsConstructor
@AllArgsConstructor
public class AttackKungFuParametersProvider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    @Enumerated(EnumType.STRING)
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
    private int icon;

}
