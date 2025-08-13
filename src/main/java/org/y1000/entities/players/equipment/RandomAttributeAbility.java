package org.y1000.entities.players.equipment;

import lombok.Getter;
import org.y1000.entities.players.Armor;
import org.y1000.entities.players.Damage;

@Getter
public class RandomAttributeAbility implements EquipmentAbility {

    private final Damage damage;
    private final int attackSpeed;
    private final int recovery;
    private final int avoid;
    private final Armor armor;

    public RandomAttributeAbility(Damage damage,
                                  int attackSpeed,
                                  int recovery,
                                  int avoid,
                                  Armor armor) {
        this.damage = damage;
        this.attackSpeed = attackSpeed;
        this.recovery = recovery;
        this.avoid = avoid;
        this.armor = armor;
    }

    @Override
    public EquipmentAbilityType abilityType() {
        return EquipmentAbilityType.RandomAttribute;
    }
}
