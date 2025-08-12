package org.y1000.entities.npc;

import org.y1000.entities.npc.event.NpcSnapshot;

/**
 * 需要客户端播放动画的能力。
 */
public interface NpcAnimatedAbility {

    boolean update(int delta);

    NpcSnapshot captureSnapshot(Npc npc);

}
