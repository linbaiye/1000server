package org.y1000.persistence;

import jakarta.persistence.*;
import lombok.*;
import org.y1000.kungfu.attack.*;

@Getter
@Setter
@Entity
@Builder
@Table(name = "guild_kung_fu")
@AllArgsConstructor
@NoArgsConstructor
public class GuildKungFuPo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guild_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private GuildPo guild;

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


    public static GuildKungFuPo convert(GuildPo guildStone, AttackKungFu attackKungFu) {
        if (guildStone == null || attackKungFu == null)
            throw new IllegalArgumentException();
        AttackKungFuParameters parameters = ((AbstractAttackKungFu)attackKungFu).parameters();
        if (!(parameters instanceof GuildKungFuParameters kungFuParameters)) {
            throw new IllegalArgumentException();
        }
        GuildKungFuPo provider = kungFuParameters.getProvider();
        GuildKungFuPo guildKungFuPo = new GuildKungFuPo();
        guildKungFuPo.attackSpeed = provider.attackSpeed;
        guildKungFuPo.recovery = provider.recovery;
        guildKungFuPo.avoid = provider.avoid;
        guildKungFuPo.headDamage = provider.headDamage;
        guildKungFuPo.bodyDamage = provider.bodyDamage;
        guildKungFuPo.armDamage = provider.armDamage;
        guildKungFuPo.legDamage = provider.legDamage;
        guildKungFuPo.headArmor = provider.headArmor;
        guildKungFuPo.armArmor = provider.armArmor;
        guildKungFuPo.bodyArmor = provider.bodyArmor;
        guildKungFuPo.legArmor = provider.legArmor;
        guildKungFuPo.swingPower = provider.swingPower;;
        guildKungFuPo.swingInnerPower = provider.swingInnerPower;
        guildKungFuPo.swingOuterPower = provider.swingOuterPower;
        guildKungFuPo.swingLife = provider.swingLife;
        guildKungFuPo.swingSound = provider.swingSound;
        guildKungFuPo.strikeSound = provider.strikeSound;
        guildKungFuPo.effectColor = provider.effectColor;
        guildKungFuPo.icon = provider.icon;
        guildKungFuPo.name = provider.name;
        guildKungFuPo.type = provider.type;
        guildKungFuPo.guild = guildStone;
        return guildKungFuPo;
    }
}
