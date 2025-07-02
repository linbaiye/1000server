package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.AttackableEntity;
import org.y1000.entities.players.event.PlayerAttributeMessage;
import org.y1000.entities.players.event.PlayerTextMessage;
import org.y1000.kungfu.attack.AttackKungFu;
import org.y1000.message.PlayerAttackMessage;
import org.y1000.message.PlayerChangeStateMessage;

@Slf4j
final class CombatController {
    private final AttackableEntity enemy;
    private final PlayerImpl player;
    private int resourceNoticeTimer;

    CombatController(PlayerImpl player, AttackableEntity target) {
        this.enemy = target;
        this.player = player;
        resourceNoticeTimer = 0;
    }

    private void readyToFight() {
        player.changeDirection(player.coordinate().computeDirection(enemy.coordinate()));
        player.changeState(PlayerStandState.fightStand(player));
        player.sendMessage(PlayerChangeStateMessage.allVisible(player));
    }

    private void attack() {
        AttackKungFu kungFu = player.attackKungFu();
        AttackAction action = kungFu.computeAttackAction();
        player.changeDirection(player.coordinate().computeDirection(enemy.coordinate()));
        player.changeState(new PlayerAttackState(player, action));
        kungFu.consumeAttributes(player);
        player.cooldownAttack();
        PlayerAttributeMessage message = PlayerAttackMessage.attack(player, action, player.attackKungFu().computeEffectId());
        player.sendMessage(message);
    }

    private void start() {
        player.disableBreathKungNoTip();
        player.disableFootKungFuAndSync();
        if (player.maxCooldown() > 0) {
            log.debug("Cooling down.");
            readyToFight();
            return;
        }
        if (!enemy.canBeAttackedNow()) {
            log.debug("Enemy can't be attacked now.");
            readyToFight();
            return;
        }
        String ret = player.attackKungFu().checkResourceToAttack(player);
        if (ret != null) {
            player.sendMessage(PlayerTextMessage.of(player, ret));
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


    void update(int delta) {
        if (player.maxCooldown() > 0)
            return;
        AttackKungFu kungFu = player.attackKungFu();
        String ret = kungFu.checkResourceToAttack(player);
        if (ret != null) {
            resourceNoticeTimer -= delta;
            if (resourceNoticeTimer <= 0) {
                player.sendMessage(PlayerTextMessage.of(player, ret));
                resourceNoticeTimer = 2000;
            }
            return;
        }
        if (!enemy.canBeAttackedNow()) {
            return;
        }
        if (player.attackKungFu().isWithinAttackRange(player.coordinate(), enemy.coordinate())) {
            attack();
        }
    }

    public static CombatController createAndStart(PlayerImpl player, AttackableEntity target) {
        CombatController combatController = new CombatController(player, target);
        combatController.start();;
        return combatController;
    }
}
