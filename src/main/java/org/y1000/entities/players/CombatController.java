package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.ActiveEntity;
import org.y1000.entities.HurtAbility;
import org.y1000.entities.PlayerSoundEvent;
import org.y1000.entities.players.event.PlayerTextMessage;
import org.y1000.kungfu.attack.AttackKungFu;
import org.y1000.entities.players.event.PlayerAttackEvent;
import org.y1000.message.PlayerChangeStateEvent;

@Slf4j
final class CombatController {
    private final ActiveEntity enemy;
    private final PlayerImpl player;
    private int resourceNoticeTimer;
    private final HurtAbility hurtAbility;

    CombatController(PlayerImpl player, ActiveEntity entity, HurtAbility hurtAbility) {
        this.player = player;
        enemy = entity;
        this.hurtAbility = hurtAbility;
        resourceNoticeTimer = 0;
    }

    private void readyToFight() {
        player.changeDirection(player.coordinate().computeDirection(enemy.coordinate()));
        player.changeState(PlayerStandState.fightStand(player));
        player.sendEvent(PlayerChangeStateEvent.allVisible(player));
    }

    private void attack() {
        AttackKungFu kungFu = player.attackKungFu();
        AttackAction action = kungFu.computeAttackAction();
        player.changeDirection(player.coordinate().computeDirection(enemy.coordinate()));
        player.changeState(new PlayerAttackState(player, action));
        var message = PlayerAttackEvent.attack(player, action, player.attackKungFu().computeEffectId());
        player.sendEvent(message);
        kungFu.consumeAttributes(player);
        player.cooldownAttack();
        int exp = hurtAbility.attacked(player, player.damage(), player.hit());
        if (exp > 0)
            player.doGainExp(exp, kungFu);
        player.sendEvent(PlayerSoundEvent.sound(player, exp == -1 ? kungFu.swingSound() : kungFu.strikeSound()));
    }

    private void start() {
        player.disableBreathAndSync();
        player.disableFootKungFuAndSync();
        if (player.maxCooldown() > 0) {
            log.debug("Cooling down.");
            readyToFight();
            return;
        }
        String ret = player.attackKungFu().checkResourceToAttack(player);
        if (ret != null) {
            player.sendEvent(PlayerTextMessage.of(player, ret));
            resourceNoticeTimer = 2000;
            readyToFight();
            log.debug("No resource to attack.");
            return;
        }
        if (player.attackKungFu().isWithinAttackRange(player.coordinate(), enemy.coordinate())) {
            attack();
        } else {
            readyToFight();
        }
    }


    boolean update(int delta) {
        if (!hurtAbility.canBeSwung()) {
            player.stopCombat();
            return false;
        }
        if (player.maxCooldown() > 0)
            return false;
        AttackKungFu kungFu = player.attackKungFu();
        String ret = kungFu.checkResourceToAttack(player);
        if (ret != null) {
            resourceNoticeTimer -= delta;
            if (resourceNoticeTimer <= 0) {
                player.sendEvent(PlayerTextMessage.of(player, ret));
                resourceNoticeTimer = 2000;
            }
            return false;
        }
        if (player.attackKungFu().isWithinAttackRange(player.coordinate(), enemy.coordinate())) {
            attack();
            return true;
        }
        return false;
    }

    static CombatController startIfAllowed(PlayerImpl player, ActiveEntity target) {
        HurtAbility ability = target.findAbility(HurtAbility.class).orElse(null);
        if (ability == null || !ability.canBeSwung() || player.attackKungFu().isRanged())
            return null;
        CombatController combatController = new CombatController(player, target);
        combatController.start();;
        return combatController;
    }
}
