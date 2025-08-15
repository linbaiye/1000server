package org.y1000.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.y1000.guild.GuildStone;
import org.y1000.kungfu.attack.AttackKungFu;
import org.y1000.kungfu.attack.AttackKungFuParameters;
import org.y1000.kungfu.attack.AttackKungFuType;

@Data
@Entity
@Table(name = "guild_kung_fu")
@AllArgsConstructor
@NoArgsConstructor
public class GuildKungFuPo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "guild_id")
    private GuildStonePo guildStone;

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


    public static GuildKungFuPo convert(GuildStonePo guildStone, AttackKungFu attackKungFu) {
        if (guildStone == null || attackKungFu == null)
            return null;
        AttackKungFuParameters parameters = attackKungFu.originParameters();
        GuildKungFuPo guildKungFuPo = new GuildKungFuPo();
        guildKungFuPo.attackSpeed = parameters.attackSpeed();
        guildKungFuPo.recovery = parameters.recovery();
        guildKungFuPo.avoid = parameters.avoidance();
        guildKungFuPo.headDamage = parameters.headDamage();
        guildKungFuPo.armDamage = parameters.armDamage();
        guildKungFuPo.bodyDamage = parameters.bodyDamage();
        guildKungFuPo.legDamage = parameters.legDamage();
        guildKungFuPo.headArmor = parameters.headArmor();
        guildKungFuPo.armArmor = parameters.armArmor();
        guildKungFuPo.bodyArmor = parameters.bodyArmor();
        guildKungFuPo.legArmor = parameters.legArmor();
        guildKungFuPo.swingPower = parameters.powerToSwing();
        guildKungFuPo.swingInnerPower = parameters.innerPowerToSwing();
        guildKungFuPo.swingOuterPower = parameters.outerPowerToSwing();
        guildKungFuPo.swingLife = parameters.lifeToSwing();
        guildKungFuPo.swingSound = parameters.swingSound();
        guildKungFuPo.strikeSound = parameters.swingSound();
        guildKungFuPo.effectColor = parameters.effectId();
        guildKungFuPo.icon = parameters.icon();
        guildKungFuPo.guildStone = guildStone;
        return guildKungFuPo;
    }
}
