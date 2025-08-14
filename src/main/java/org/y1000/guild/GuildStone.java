package org.y1000.guild;

import lombok.Getter;
import org.y1000.entities.*;
import org.y1000.entities.players.Damage;
import org.y1000.network.I2ClientMessage;
import org.y1000.realm.Realm;
import org.y1000.util.Coordinate;

import java.util.Objects;
import java.util.Optional;

public final class GuildStone extends AbstractActiveEntity implements HurtAbility  {

    @Getter
    private Integer persistentId;

    private int currentHealth;

    private final int maxHealth;

    private final String guildName;

    private final Coordinate coordinate;

    @Getter
    private Realm realm;

    @Getter
    private final int icon;

    public void setPersistentId(int persistentId) {
        if (this.persistentId != null)
            throw new IllegalStateException();
        this.persistentId = persistentId;
    }

    public GuildStone(long id,
                      Coordinate coordinate,
                      int currentHealth,
                      int maxHealth,
                      String name,
                      Integer persistentId,
                      int icon) {
        super(id);
        this.persistentId = persistentId;
        this.coordinate = coordinate;
        this.guildName = name;
        this.maxHealth = maxHealth;
        this.icon = icon;
    }

    public String guildName() {
        return guildName;
    }

    public int getMaxLife() {
        return 0;
    }

    @Override
    public boolean canBeAttacked() {
        return currentLife() > 0;
    }


    @Override
    public boolean swingAllowed() {
        return false;
    }

    @Override
    public int attacked(ActiveEntity attacker, Damage damage, int accuracy) {
        return 0;
    }

    public int currentLife() {
        return 0;
    }

    @Override
    public int maxLife() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GuildStone that = (GuildStone) o;
        return Objects.equals(persistentId, that.persistentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(persistentId);
    }

    @Override
    public void update(int delta) {

    }

    @Override
    public <AB> Optional<AB> findAbility(Class<AB> type) {
        return Optional.empty();
    }

    @Override
    public Coordinate coordinate() {
        return coordinate;
    }

    @Override
    public I2ClientMessage captureSnapshot() {
        return GroundItemSnapshot.of(this);
    }
}
