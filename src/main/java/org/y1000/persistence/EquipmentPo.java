package org.y1000.persistence;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.y1000.entities.players.Armor;
import org.y1000.entities.players.Damage;
import org.y1000.entities.players.equipment.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "equipment")
@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
public class EquipmentPo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @JoinColumn
    @JdbcTypeCode(SqlTypes.JSON)
    private List<EquipmentAbilityPo> abilities;

    public void merge(Equipment equipment) {
        abilities.clear();
        abilities.addAll(convertAbilities(equipment));
    }

    private static List<EquipmentAbilityPo> convertAbilities(Equipment equipment){
        List<EquipmentAbilityPo> po = new ArrayList<>();
        equipment.findAbility(RandomAttributeAbility.class)
                .map(RandomAttributePo::of)
                .ifPresent(po::add);
        equipment.findAbility(Dyable.class)
                .map(DyablePo::of)
                .ifPresent(po::add);
        return po;
    }

    public static EquipmentPo convert(Equipment equipment) {
        EquipmentPo po = new EquipmentPo();
        po.id = equipment.id();
        po.name = equipment.name();
        po.abilities = convertAbilities(equipment);
        return po;
    }

    @Setter
    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RandomAttributePo extends EquipmentAbilityPo {
        private int attackSpeed;
        private int recovery;
        private int avoid;
        private int bodyDamage;
        private int headDamage;
        private int armDamage;
        private int legDamage;
        private int bodyArmor;
        private int headArmor;
        private int armArmor;
        private int legArmor;

        public static RandomAttributePo of(RandomAttributeAbility ability) {
            RandomAttributePo randomAttributePo = new RandomAttributePo();
            randomAttributePo.attackSpeed = ability.getAttackSpeed();
            randomAttributePo.recovery = ability.getRecovery();
            randomAttributePo.avoid = ability.getAvoid();
            randomAttributePo.bodyDamage = ability.getDamage().bodyDamage();
            randomAttributePo.headDamage = ability.getDamage().headDamage();
            randomAttributePo.armDamage = ability.getDamage().armDamage();
            randomAttributePo.legDamage = ability.getDamage().legDamage();
            randomAttributePo.legArmor = ability.getArmor().leg();
            randomAttributePo.bodyArmor = ability.getArmor().body();
            randomAttributePo.headArmor = ability.getArmor().head();
            randomAttributePo.armArmor = ability.getArmor().arm();
            randomAttributePo.setType(EquipmentAbilityType.RandomAttribute.name());
            return randomAttributePo;
        }

        @Override
        public EquipmentAbility restore() {
            return new RandomAttributeAbility(new Damage(bodyDamage, headDamage, armDamage, legDamage),
                    attackSpeed, recovery, avoid, new Armor(bodyArmor, headArmor, armArmor, legArmor));
        }
    }

    @Setter
    @Getter
    public static class DyablePo extends EquipmentAbilityPo {
        private int color;
        public static DyablePo of(Dyable dyable) {
            DyablePo po = new DyablePo();
            po.color = dyable.color();
            po.setType(EquipmentAbilityType.Dyable.name());
            return po;
        }

        @Override
        public EquipmentAbility restore() {
            return new DyableImpl(color);
        }
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
    @Data
    @JsonSubTypes({
            @JsonSubTypes.Type(value = DyablePo.class, name = "Dyable"),
            @JsonSubTypes.Type(value = RandomAttributePo.class, name = "RandomAttribute")
    })
    public abstract static class EquipmentAbilityPo {
        private String type;

        public abstract EquipmentAbility restore();
    }
}
