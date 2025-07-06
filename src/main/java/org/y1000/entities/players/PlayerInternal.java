package org.y1000.entities.players;

import org.y1000.entities.creatures.npc.Npc;
import org.y1000.item.Equipment;
import org.y1000.kungfu.FootKungFu;
import org.y1000.kungfu.attack.AttackKungFu;
import org.y1000.kungfu.breath.BreathKungFu;

/**
 * Package level interface for updating Player.
 */
interface PlayerInternal extends Player {

    void tryEquipFromSlot(int slotId, Equipment equipment);

    void tryUseAttackKungFu(AttackKungFu newKungFu);

    void disableFootKungFuAndSync();

    void stopFight();

    boolean updateCombat(int delta);

    void changeState(PlayerState playerState);

    void acceptAttack(Npc target);

    void toggleBreathKungFu(BreathKungFu newKungFu);

    void toggleFootKungFu(FootKungFu footKungFu);
}
