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

    CombatController(PlayerImpl player, ActiveEntity entity) {
        this.player = player;
        enemy = entity;
        hurtAbility = entity.findAbility(HurtAbility.class).orElseThrow();
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
        int hit = hurtAbility.attacked(player, player.damage(), player.hit());
        player.sendEvent(PlayerSoundEvent.sound(player, hit == -1 ? kungFu.swingSound() : kungFu.strikeSound()));
    }

    private void start() {
        player.disableBreathAndSync();
        player.disableFootKungFuAndSync();
        if (player.maxCooldown() > 0) {
            log.debug("Cooling down.");
            readyToFight();
            return;
        }
        if (!hurtAbility.canBeAttacked()) {
            log.debug("Enemy can't be attacked now.");
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
        if (!hurtAbility.canBeAttacked()) {
            return false;
        }
        if (player.attackKungFu().isWithinAttackRange(player.coordinate(), enemy.coordinate())) {
            attack();
            return true;
        }
        return false;
    }

    static CombatController createAndStart(PlayerImpl player, ActiveEntity target) {
        CombatController combatController = new CombatController(player, target);
        combatController.start();;
        return combatController;
    }
}
