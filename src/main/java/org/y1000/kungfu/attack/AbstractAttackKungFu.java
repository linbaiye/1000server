package org.y1000.kungfu.attack;

import lombok.Getter;
import org.slf4j.Logger;
import org.y1000.entities.AttackableEntity;
import org.y1000.entities.Direction;
import org.y1000.entities.attribute.Damage;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.EntitySoundEvent;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.entities.players.PlayerStillState;
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

    protected PlayerTextEvent checkAttributeResources(PlayerImpl player) {
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

    protected abstract PlayerTextEvent checkResources(PlayerImpl player);

    protected boolean useAttributeResources(PlayerImpl player) {
        var ret = checkAttributeResources(player);
        if (ret != null) {
            player.emitEvent(ret);
            return false;
        }
        player.consumeLife(parameters.lifeToSwing());
        player.consumeOuterPower(parameters.outerPowerToSwing());
        player.consumeInnerPower(parameters.innerPowerToSwing());
        player.consumePower(parameters.powerToSwing());
        player.emitEvent(new PlayerAttributeEvent(player));
        return true;
    }

    protected abstract boolean useResources(PlayerImpl player);


    private String computeSound(int nr) {
        if (level() < 5000) {
            return String.valueOf(nr);
        }
        return level() < 5000 ? String.valueOf(nr) :
                String.valueOf(level() > 8999 ? nr + 4 : nr + 2);
    }

    private String strikeSound() {
        return computeSound(parameters.strikeSound());
    }

    private String swingSound() {
        return computeSound(parameters.swingSound());
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
        if (!useResources(player)) {
            player.changeState(new PlayerCooldownState(player.getStateMillis(State.COOLDOWN)));
            return;
        }
        player.changeDirection(direction);
        player.cooldownAttack();
        player.changeState(PlayerAttackState.of(player));
        if (sendAttackEvent)
            player.emitEvent(PlayerAttackEvent.of(player, player.getFightingEntity().id()));
        if (!isRanged()) {
            var hit = player.getFightingEntity().attackedBy(player);
            player.emitEvent(new EntitySoundEvent(player, hit ? strikeSound() : swingSound()));
            logger().debug("Done attack event.");
            //player.assistantKungFu().ifPresent(assistantKungFu -> player.emitEvent(PlayerAttackAoeEvent.melee(player, assistantKungFu)));
        } else {
            player.emitEvent(new EntitySoundEvent(player, swingSound()));
        }
    }

    @Override
    public void attackAgain(PlayerImpl player) {
        if (player.getFightingEntity() == null || !player.canPurchaseOrAttack(player.getFightingEntity())) {
            player.changeState(PlayerStillState.chillOut(player));
            return;
        }
        Direction direction = player.coordinate().computeDirection(player.getFightingEntity().coordinate());
        doAttack(player, direction, true);
    }

    protected void doStartAttack(PlayerImpl player, ClientAttackEvent event, AttackableEntity target) {
        if (!player.canPurchaseOrAttack(target)) {
            player.emitEvent(new PlayerAttackEventResponse(player, event, false));
            return;
        }
        var text = checkResources(player);
        if (text != null) {
            player.emitEvent(text);
            player.emitEvent(new PlayerAttackEventResponse(player, event, false));
            return;
        }
        Direction direction = event.direction();
        player.setFightingEntity(target);
        player.disableFootKungFuNoTip();
        player.disableBreathKungNoTip();
        doAttack(player, direction, false);
        player.changeDirection(direction);
        player.emitEvent(new PlayerAttackEventResponse(player, event, true));
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
}
