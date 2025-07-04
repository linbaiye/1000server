package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.creatures.npc.HurtAbility;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.entities.players.event.PlayerAttributeEvent;
import org.y1000.entities.players.event.PlayerTextMessage;
import org.y1000.kungfu.attack.AttackKungFu;
import org.y1000.message.PlayerAttackEvent;
import org.y1000.message.PlayerChangeStateEvent;

@Slf4j
final class CombatController {
    private final Npc enemy;
    private final PlayerImpl player;
    private int resourceNoticeTimer;
    private final HurtAbility hurtAbility;

    CombatController(PlayerImpl player, Npc target) {
        this.enemy = target;
        this.player = player;
        resourceNoticeTimer = 0;
        hurtAbility = target.findAction(HurtAbility.class).orElseThrow(RuntimeException::new);
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
        kungFu.consumeAttributes(player);
        player.cooldownAttack();
        hurtAbility.attackedBy(player, player.damage(), player.hit());
        PlayerAttributeEvent message = PlayerAttackEvent.attack(player, action, player.attackKungFu().computeEffectId());
        player.sendEvent(message);
    }

    private void start() {
        player.disableBreathKungNoTip();
        player.disableFootKungFuAndSync();
        if (player.maxCooldown() > 0) {
            log.debug("Cooling down.");
            readyToFight();
            return;
        }
        if (!hurtAbility.canBeAttackedNow()) {
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


    void update(int delta) {
        if (player.maxCooldown() > 0)
            return;
        AttackKungFu kungFu = player.attackKungFu();
        String ret = kungFu.checkResourceToAttack(player);
        if (ret != null) {
            resourceNoticeTimer -= delta;
            if (resourceNoticeTimer <= 0) {
                player.sendEvent(PlayerTextMessage.of(player, ret));
                resourceNoticeTimer = 2000;
            }
            return;
        }
        if (!hurtAbility.canBeAttackedNow()) {
            return;
        }
        if (player.attackKungFu().isWithinAttackRange(player.coordinate(), enemy.coordinate())) {
            attack();
        }
    }

    public static CombatController createAndStart(PlayerImpl player, Npc target) {
        CombatController combatController = new CombatController(player, target);
        combatController.start();;
        return combatController;
    }
}
