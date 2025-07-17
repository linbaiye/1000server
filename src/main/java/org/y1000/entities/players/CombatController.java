package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.ActiveEntity;
import org.y1000.entities.FilterVisibleEvent;
import org.y1000.entities.HurtAbility;
import org.y1000.entities.players.event.PlayerSoundEvent;
import org.y1000.entities.players.event.PlayerTextMessage;
import org.y1000.item.Ammo;
import org.y1000.kungfu.AssistantKungFu;
import org.y1000.kungfu.attack.AbstractRangedKungFu;
import org.y1000.kungfu.attack.AttackKungFu;
import org.y1000.entities.players.event.PlayerAttackEvent;
import org.y1000.util.Coordinate;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    private void aoeMelee(AttackKungFu kungFu, AssistantKungFu assistantKungFu) {
        // attack main target.
        int exp = hurtAbility.attacked(player, player.damage(), player.hit());
        var event = FilterVisibleEvent.filterAOE(player);
        player.sendEvent(event);
        Damage aoeDamage = assistantKungFu.computeDamage(player.damage());
        Set<Coordinate> coordinates = assistantKungFu.affectedCoordinates(player);
        List<ActiveEntity> entities = event.resultStream(ActiveEntity.class)
                .filter(e -> coordinates.contains(e.coordinate())).toList();
        for (ActiveEntity entity : entities) {
            HurtAbility ability = entity.findAbility(HurtAbility.class).orElse(null);
            if (ability == null || !ability.canBeAttacked())
                continue;
            int tmp = ability.attacked(player, aoeDamage, player.hit());
            if (exp < tmp)
                exp = tmp;
        }
        assistantKungFu.gainExp(player, exp);
        player.sendEvent(PlayerSoundEvent.toAll(player, exp == -1 ? kungFu.swingSound() : kungFu.strikeSound()));

    }

    private void singleAttack(AttackKungFu kungFu) {
        int exp = hurtAbility.attacked(player, player.damage(), player.hit());
        if (exp > 0)
            player.doGainExp(exp, kungFu);
        player.sendEvent(PlayerSoundEvent.toAll(player, exp == -1 ? kungFu.swingSound() : kungFu.strikeSound()));
    }

    private void attack() {
        AttackKungFu kungFu = player.attackKungFu();
        AttackAction action = kungFu.computeAttackAction();
        player.cooldownAttack();
        player.changeDirection(player.coordinate().directionByAngle(enemy.coordinate()));
        var message = PlayerAttackEvent.attack(player, action, player.attackKungFu().computeEffectId());
        player.sendEvent(message);
        if (kungFu instanceof AbstractRangedKungFu rangedKungFu) {
            Ammo ammo = rangedKungFu.consumeResources(player);
            player.changeState(PlayerShootState.aimTarget(player, rangedKungFu, enemy, ammo.getFlySprite()));
            player.sendEvent(PlayerSoundEvent.toAll(player, kungFu.swingSound()));
        } else {
            kungFu.consumeAttributes(player);
            player.changeState(new PlayerMeleeState(player, action));
            player.assistantKungFu().ifPresentOrElse(a -> aoeMelee(kungFu, a), () -> singleAttack(kungFu));
        }
    }

    /**
     * Update combat progress, change to attack state if able.
     * @param delta
     * @return -1 if this combat is over, 1 if a strike is carried, 0 when combat should carry on.
     */
    int update(int delta) {
        if (!hurtAbility.swingAllowed()) {
            return -1;
        }
        if (player.maxCooldown() > 0) {
            return 0;
        }
        AttackKungFu kungFu = player.attackKungFu();
        String ret = kungFu.checkResourceToAttack(player);
        if (ret != null) {
            resourceNoticeTimer -= delta;
            if (resourceNoticeTimer <= 0) {
                player.sendEvent(PlayerTextMessage.of(player, ret));
                resourceNoticeTimer = 2000;
            }
            return 0;
        }
        if (player.attackKungFu().isWithinAttackRange(player.coordinate(), enemy.coordinate())) {
            attack();
            return 1;
        }
        return 0;
    }

    static CombatController acceptIfAllowed(PlayerImpl player, ActiveEntity target) {
        HurtAbility ability = target.findAbility(HurtAbility.class).orElse(null);
        if (ability == null || !ability.swingAllowed())
                return null;
        if (!ability.canBeAttacked() && player.attackKungFu().isRanged())
            return null;
        return new CombatController(player, target, ability);
    }
}
