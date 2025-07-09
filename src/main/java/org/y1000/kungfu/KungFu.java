package org.y1000.kungfu;


import org.y1000.entities.players.Player;

public interface KungFu {
    String name();

    int level();

    int exp();

    /**
     * Gain experience.
     * @param value exp
     * @return true if level up.
     */
    boolean gainPermittedExp(int value);

    boolean isLevelFull();


    KungFuType kungFuType();


    String detailText();


    KungFu duplicate();

    int icon();

    default boolean nameEquals(KungFu kungFu) {
        return kungFu != null && name().equals(kungFu.name());
    }

    void gainExp(Player player, int exp);

}
