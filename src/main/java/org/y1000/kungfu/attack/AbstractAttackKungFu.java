package org.y1000.kungfu.attack;

import lombok.Getter;
import org.slf4j.Logger;
import org.y1000.entities.AttackableActiveEntity;
import org.y1000.entities.Direction;
import org.y1000.entities.players.*;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.EntitySoundEvent;
import org.y1000.entities.players.event.*;
import org.y1000.entities.players.fight.*;
import org.y1000.kungfu.AbstractKungFu;
import org.y1000.kungfu.KungFuType;
import org.y1000.message.PlayerTextEvent;
import org.y1000.message.clientevent.ClientAttackEvent;

import java.util.List;

@Getter
public abstract class AbstractAttackKungFu extends AbstractKungFu implements AttackKungFu {


    protected AbstractAttackKungFu(String name, int exp, AttackKungFuParameters parameters) {
        super(name, exp);
        this.parameters = parameters;
    }

    private record DamageMultiplier(int levelStart, int levelEnd, int multiplier) {
        public boolean contains(int skillLevel) {
            return levelEnd >= skillLevel && levelStart <= skillLevel;
        }

        public int multiply(int damage) {
            return damage + multiplier * damage / 100;
        }
    }

    private static final List<DamageMultiplier> DAMAGE_MULTIPLIERS = List.of(
            new DamageMultiplier(0,4999,0),
            new DamageMultiplier(5000,5999,2),
            new DamageMultiplier(6000,6999,6),
            new DamageMultiplier(7000,7999,10),
            new DamageMultiplier(8000,8999,15),
            new DamageMultiplier(9000,9998,20),
            new DamageMultiplier(9999,9999,25)
    );

    private static final int INIT_SKILL_DIV_DAMAGE = 5000;

    private static final int INIT_SKILL_DIV_ATTACKSPEED = 25000;

    private static final int INIT_SKILL_DIV_ARMOR = 5000;

    /*
    p^.rLifeData.damageBody + p^.rLifeData.damageBody * p^.rcSkillLevel div INI_SKILL_DIV_DAMAGE;
     */

    private final AttackKungFuParameters parameters;

    protected abstract Logger logger();

    protected PlayerTextEvent checkAttributeResources(Player player) {
        if (player.power() < parameters.powerToSwing()) {
            return PlayerTextEvent.noPower(player);
        }
        if (player.innerPower() < parameters.innerPowerToSwing()) {
            return PlayerTextEvent.noInnerPower(player);
        }
        if (player.outerPower() < parameters.outerPowerToSwing()) {
            return PlayerTextEvent.noOuterPower(player);
        }
        int lifeToSwing = parameters.lifeToSwing();
        if (player.currentLife() <= lifeToSwing) {
            return PlayerTextEvent.insufficientLife(player);
        }
        return null;
    }

    protected abstract boolean checkResourcesAndSendError(Player player);

    protected void useAttributeResources(Player player) {
        var ret = checkAttributeResources(player);
        if (ret != null) {
            return;
        }
        player.consumeLife(parameters.lifeToSwing());
        player.consumeOuterPower(parameters.outerPowerToSwing());
        player.consumeInnerPower(parameters.innerPowerToSwing());
        player.consumePower(parameters.powerToSwing());
        player.emitEvent(new PlayerAttributeEvent(player));
    }

    protected abstract PlayerAttackState useResourcesAndCreateState(PlayerImpl player);

    protected abstract int computeAbove5000SoundOffset(int level);

    private String computeSound(int nr) {
        if (level() < 5000) {
            return String.valueOf(nr);
        }
        return String.valueOf(nr +  computeAbove5000SoundOffset(level()));
    }

    public String strikeSound() {
        return computeSound(getParameters().strikeSound());
    }

    public String swingSound() {
        return computeSound(getParameters().swingSound());
    }

    private void doSingleAttack(PlayerImpl player) {
        var hit = player.getFightingEntity().attackedBy(player);
        player.emitEvent(new EntitySoundEvent(player, hit ? strikeSound() : swingSound()));
    }

    private void doAttack(PlayerImpl player, Direction direction, boolean sendAttackEvent) {
        int cooldown = player.cooldown();
        if (cooldown > 0) {
            player.changeState(new PlayerCooldownState(cooldown));
            return;
        }
        if (!isRanged() && player.getFightingEntity().coordinate().directDistance(player.coordinate()) > 1) {
            player.changeState(new PlayerWaitDistanceState(player.getStateMillis(State.COOLDOWN)));
            return;
        }
        var ok = checkResourcesAndSendError(player);
        if (!ok) {
            player.changeState(new PlayerCooldownState(player.getStateMillis(State.COOLDOWN)));
            return;
        }
        player.changeDirection(direction);
        player.cooldownAttack();
        var state = useResourcesAndCreateState(player);
        player.changeState(state);
        if (sendAttackEvent)
            player.emitEvent(PlayerAttackEvent.of(player, computeEffectId()));
        if (!isRanged()) {
            player.assistantKungFu().ifPresentOrElse(
                    assistantKungFu -> player.emitEvent(PlayerAttackAoeEvent.melee(player, player.getFightingEntity(), assistantKungFu)),
                    () -> doSingleAttack(player));
        } else {
            player.emitEvent(new EntitySoundEvent(player, swingSound()));
        }
    }

    @Override
    public void attackAgain(PlayerImpl player) {
        if (player.getFightingEntity() == null || !player.canChaseOrAttack(player.getFightingEntity())) {
            player.changeState(PlayerStillState.chillOut(player));
            return;
        }
        Direction direction = player.coordinate().computeDirection(player.getFightingEntity().coordinate());
        doAttack(player, direction, true);
    }

    protected void doStartAttack(PlayerImpl player, ClientAttackEvent event, AttackableActiveEntity target) {
        if (!player.canChaseOrAttack(target)) {
            player.emitEvent(new PlayerAttackEventResponse(player, event, false, computeEffectId()));
            return;
        }
        var ok = checkResourcesAndSendError(player);
        if (!ok) {
            player.emitEvent(new PlayerAttackEventResponse(player, event, false, computeEffectId()));
            return;
        }
        Direction direction = event.direction();
        player.setFightingEntity(target);
        player.disableFootKungFuNoTip();
        player.disableBreathKungNoTip();
        doAttack(player, direction, false);
        player.changeDirection(direction);
        player.emitEvent(new PlayerAttackEventResponse(player, event, true, computeEffectId()));
    }


    @Override
    public KungFuType kungFuType() {
        return getType().toKungFuType();
    }

    @Override
    public int recovery() {
        return parameters.recovery();
    }

    @Override
    public int attackSpeed() {
        var innate = parameters.attackSpeed();
        return innate - innate * level() / INIT_SKILL_DIV_ATTACKSPEED;
    }

    @Override
    public int bodyArmor() {
        return applyLevelToArmor(parameters.bodyArmor());
    }

    @Override
    public int avoidance() {
        return parameters.avoidance();
    }


    @Override
    public int bodyDamage() {
        /*
      rcLifeData.DamageBody   := rLifeData.damageBody  + rLifeData.damageBody * rcSkillLevel div INI_SKILL_DIV_DAMAGE;
      rcLifeData.DamageHead   := rLifeData.DamageHead  + rLifeData.damageHead * rcSkillLevel div INI_SKILL_DIV_DAMAGE;
      rcLifeData.DamageArm    := rLifeData.DamageArm   + rLifeData.damageArm  * rcSkillLevel div INI_SKILL_DIV_DAMAGE;
      rcLifeData.DamageLeg    := rLifeData.DamageLeg   + rLifeData.damageLeg  * rcSkillLevel div INI_SKILL_DIV_DAMAGE;
      rcLifeData.AttackSpeed  := rLifeData.AttackSpeed - rLifeData.AttackSpeed * rcSkillLevel div INI_SKILL_DIV_ATTACKSPEED;
      rcLifeData.avoid        := rLifeData.avoid   ;//    + rLifeData.avoid;
      rcLifeData.recovery     := rLifeData.recovery;//    + rLifeData.recovery;
      rcLifeData.armorBody    := rLifeData.armorBody   + rLifeData.armorBody * rcSkillLevel div INI_SKILL_DIV_ARMOR;
      rcLifeData.armorHead    := rLifeData.armorHead   + rLifeData.armorHead * rcSkillLevel div INI_SKILL_DIV_ARMOR;
      rcLifeData.armorArm     := rLifeData.armorArm    + rLifeData.armorArm  * rcSkillLevel div INI_SKILL_DIV_ARMOR;
      rcLifeData.armorLeg     := rLifeData.armorLeg    + rLifeData.armorLeg  * rcSkillLevel div INI_SKILL_DIV_ARMOR;
         */
        int val = applyLevelToDamage(parameters.bodyDamage());
        return DAMAGE_MULTIPLIERS.stream()
                .filter(m -> m.contains(level()))
                .findAny()
                .map(m -> m.multiply(val))
                .orElse(val);
    }

    @Override
    public Armor armor() {
        return new Armor(bodyArmor(), headArmor(), armArmor(), legArmor());
    }

    private int applyLevelToDamage(int damage) {
        return damage + damage * level() / INIT_SKILL_DIV_DAMAGE;
    }

    private int applyLevelToArmor(int armor) {
        return armor + armor * level() / INIT_SKILL_DIV_ARMOR;
    }

    @Override
    public int headDamage() {
        return applyLevelToDamage(parameters.headDamage());
    }

    @Override
    public int legDamage() {
        return applyLevelToDamage(parameters.legDamage());
    }

    @Override
    public int armDamage() {
        return applyLevelToDamage(parameters.armDamage());
    }

    @Override
    public int headArmor() {
        return applyLevelToArmor(parameters.headArmor());
    }

    @Override
    public int armArmor() {
        return applyLevelToArmor(parameters.armArmor());
    }

    @Override
    public int legArmor() {
        return applyLevelToArmor(parameters.legArmor());
    }

    @Override
    public Damage damage() {
        return new Damage(bodyDamage(), headDamage(), armDamage(), legDamage());
    }

    private int effectIdPrefix() {
        return switch (getType()) {
            case QUANFA -> 110;
            case SWORD -> 120;
            case BLADE -> 130;
            case AXE -> 140;
            case SPEAR -> 150;
            case BOW -> 160;
            case THROW -> 170;
        };
    }

    @Override
    public Integer computeEffectId() {
        return level() == 9999 ? effectIdPrefix() +  parameters.effectId() : null;
    }

    @Override
    public String description() {
        StringBuilder descriptionBuilder = getDescriptionBuilder();
        descriptionBuilder.append("攻击速度: ").append(attackSpeed()).append("\n")
                .append("恢复: ").append(recovery()).append("\n")
                .append("闪躲: ").append(avoidance()).append("\n");
        var dmg = damage();
        var dmgStr = String.format("破坏力 : %d / %d / %d / %d", dmg.bodyDamage(), dmg.headDamage(), dmg.armDamage(), dmg.legDamage());
        descriptionBuilder.append(dmgStr).append("\n");
        var am = armor();
        var str = String.format("防御力: %d / %d / %d / %d", am.body(), am.head(), am.arm(), am.leg());
        return descriptionBuilder.append(str).append("\n").toString();
    }
}
