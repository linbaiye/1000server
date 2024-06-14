package org.y1000.kungfu.attack;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.slf4j.Logger;
import org.y1000.entities.Direction;
import org.y1000.entities.PhysicalEntity;
import org.y1000.entities.creatures.State;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.entities.players.PlayerStillState;
import org.y1000.entities.players.event.PlayerAttackAoeEvent;
import org.y1000.entities.players.event.PlayerAttackEvent;
import org.y1000.entities.players.event.PlayerAttackEventResponse;
import org.y1000.entities.players.event.PlayerCooldownEvent;
import org.y1000.entities.players.fight.*;
import org.y1000.kungfu.AbstractKungFu;
import org.y1000.kungfu.KungFuType;
import org.y1000.message.PlayerTextEvent;
import org.y1000.message.clientevent.ClientAttackEvent;

@Getter
@SuperBuilder
public abstract class AbstractAttackKungFu extends AbstractKungFu implements AttackKungFu {

    private int bodyDamage;
    private int headDamage;
    private int armDamage;
    private int legDamage;

    private int bodyArmor;
    private int headArmor;
    private int armArmor;
    private int legArmor;

    private int attackSpeed;

    private final AttackKungFuParameters parameters;

    protected abstract Logger logger();

    private boolean usePower(PlayerImpl player) {
        if (player.power() < parameters.powerToSwing()) {
            player.emitEvent(PlayerTextEvent.noPower(player));
            return false;
        }
        if (player.innerPower() < parameters.innerPowerToSwing()) {
            player.emitEvent(PlayerTextEvent.noInnerPower(player));
            return false;
        }
        if (player.outerPower() < parameters.outerPowerToSwing()) {
            player.emitEvent(PlayerTextEvent.noOuterPower(player));
            return false;
        }
        int lifeToSwing = parameters.lifeToSwing();
        if (player.currentLife() < lifeToSwing) {
            player.emitEvent(PlayerTextEvent.noLife(player));
            return false;
        }
        if (player.currentLife() == lifeToSwing) {
            lifeToSwing -= 1;
        }
        player.consumeLife(lifeToSwing);
        player.consumeOuterPower(parameters.outerPowerToSwing());
        player.consumeInnerPower(parameters.innerPowerToSwing());
        player.consumePower(parameters.powerToSwing());
        return true;
    }

    private boolean doAttack(PlayerImpl player, Direction direction) {
        int cooldown = player.cooldown();
        if (cooldown > 0) {
            player.changeState(new PlayerCooldownState(cooldown));
            return false;
        }
        if (!isRanged() && player.getFightingEntity().coordinate().directDistance(player.coordinate()) > 1) {
            player.changeState(new PlayerWaitDistanceState(player.getStateMillis(State.COOLDOWN)));
            return false;
        }
        if (!usePower(player)) {
            player.changeState(new PlayerCooldownState(player.getStateMillis(State.COOLDOWN)));
            player.emitEvent(PlayerCooldownEvent.of(player));
            return false;
        }
        player.changeDirection(direction);
        player.cooldownAttack();
        if (!isRanged()) {
            player.getFightingEntity().attackedBy(player);
            player.assistantKungFu().ifPresent(assistantKungFu -> player.emitEvent(PlayerAttackAoeEvent.melee(player, assistantKungFu)));
        }
        player.changeState(PlayerAttackState.of(player));
        return true;
    }

    @Override
    public void attackAgain(PlayerImpl player) {
        if (player.getFightingEntity() == null || !player.canAttack(player.getFightingEntity())) {
            player.changeState(PlayerStillState.chillOut(player));
            return;
        }
        Direction direction = player.coordinate().computeDirection(player.getFightingEntity().coordinate());
        if (doAttack(player, direction)) {
            player.emitEvent(PlayerAttackEvent.of(player, player.getFightingEntity().id()));
        }
    }

    @Override
    public void startAttack(PlayerImpl player, ClientAttackEvent event, PhysicalEntity target) {
        if (!player.canAttack(target)) {
            player.emitEvent(new PlayerAttackEventResponse(player, event, false));
            return;
        }
        Direction direction = event.direction();
        player.setFightingEntity(target);
        player.disableFootKungFuQuietly();
        player.disableBreathKungFuQuietly();
        doAttack(player, direction);
        player.changeDirection(direction);
        player.emitEvent(new PlayerAttackEventResponse(player, event, true));
    }

    @Override
    public KungFuType kungFuType() {
        return getType().toKungFuType();
    }

    @Override
    public int getRecovery() {
        return parameters.recovery();
    }

    //    @Override
//    public void setConsumingPowerTimer() {
//        consumingPowerTime = 5000;
//    }
//
//    @Override
//    public void consumePowerIfTimeUp(Player player, int delta) {
//        consumingPowerTime -= delta;
//        if (consumingPowerTime > 0) {
//            return;
//        }
//        setConsumingPowerTimer();
//        player.consumeLife(parameters.lifePer5Seconds());
//        player.consumePower(parameters.powerPer5Seconds());
//        player.consumeInnerPower(parameters.innerPowerPer5Seconds());
//        player.consumeOuterPower(parameters.outerPowerPer5Seconds());
//    }


    @Override
    public String toString() {
        return "KungFu {" +
                "name=" + name() +
                "bodyDamage=" + bodyDamage +
                ", headDamage=" + headDamage +
                ", armDamage=" + armDamage +
                ", legDamage=" + legDamage +
                ", bodyArmor=" + bodyArmor +
                ", headArmor=" + headArmor +
                ", armArmor=" + armArmor +
                ", legArmor=" + legArmor +
                ", attackSpeed=" + attackSpeed +
                '}';
    }
}
