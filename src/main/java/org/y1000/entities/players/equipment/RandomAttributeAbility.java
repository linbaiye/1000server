package org.y1000.entities.players.equipment;

import lombok.Getter;
import org.y1000.entities.players.Armor;
import org.y1000.entities.players.Damage;

import java.util.concurrent.ThreadLocalRandom;

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

    public static RandomAttributeAbility randomDamage(Damage base, int fromPercent, int toPercent) {
        var percent = ThreadLocalRandom.current().nextInt(fromPercent, toPercent + 1);
        return new RandomAttributeAbility(base.multiply((float) percent / 100), 0, 0, 0, Armor.Zero);
    }

    public static RandomAttributeAbility randomDamage(Damage base) {
        return randomDamage(base, -200, 30);
    }
}
