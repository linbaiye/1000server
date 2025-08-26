package org.y1000.guild;

import lombok.Getter;
import org.y1000.entities.*;
import org.y1000.entities.players.Damage;
import org.y1000.entities.players.Player;
import org.y1000.kungfu.attack.AttackKungFu;
import org.y1000.network.I2ClientMessage;
import org.y1000.realm.Realm;
import org.y1000.util.Coordinate;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class Guild extends AbstractActiveEntity implements HurtAbility  {

    @Getter
    private Integer guildId;

    private int currentHealth;

    private final int maxHealth;

    private final String guildName;

    private final Coordinate coordinate;

    @Getter
    private Realm realm;

    @Getter
    private final int icon;

    private AttackKungFu guildKungFu;

    @Getter
    private final Set<GuildMembership> members;

    private String founderName;

    public void setGuildId(int guildId) {
        if (this.guildId != null)
            throw new IllegalStateException();
        this.guildId = guildId;
    }

    public Guild(long id,
                 Coordinate coordinate,
                 int currentHealth,
                 int maxHealth,
                 String name,
                 Integer persistentId,
                 int icon) {
        super(id);
        this.guildId = persistentId;
        this.coordinate = coordinate;
        this.guildName = name;
        this.maxHealth = maxHealth;
        this.currentHealth = currentHealth;
        this.icon = icon;
        this.members = new HashSet<>();
    }

    public Guild(long id,
                 Coordinate coordinate,
                 int currentHealth,
                 int maxHealth,
                 String name,
                 Integer persistentId,
                 int icon,
                 Realm realm,
                 Set<GuildMembership> members,
                 String founderName) {
        super(id);
        this.guildId = persistentId;
        this.coordinate = coordinate;
        this.guildName = name;
        this.maxHealth = maxHealth;
        this.currentHealth = currentHealth;
        this.icon = icon;
        this.members = members;
        this.realm = realm;
        this.realm.map().occupy(this);
        this.founderName = founderName;
    }

    public String guildName() {
        return guildName;
    }

    public int getMaxLife() {
        return maxHealth;
    }

    public Optional<AttackKungFu> guildKungFu() {
        return Optional.ofNullable(guildKungFu);
    }

    @Override
    public boolean canBeAttacked() {
        return currentLife() > 0;
    }

    public void foundedBy(Player player) {
        if (guildId != null || player.guildMembership().isPresent()) {
            return;
        }
        founderName = player.viewName();
        realm = player.getRealm();
        realm.map().occupy(this);
        GuildMembership membership = GuildMembership.founder(player, this);
        members.add(membership);
        player.joinGuild(membership);
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
        return currentHealth;
    }

    @Override
    public int maxLife() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass() || guildId == null) return false;
        Guild that = (Guild) o;
        return Objects.equals(guildId, that.guildId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(guildId);
    }

    @Override
    public void update(int delta) {

    }

    public boolean has(Player player) {
        return player.guildMembership().map(members::contains).orElse(false);
    }

    public boolean isFounder(Player player) {
        return player.guildMembership().map(m -> m.isFounder() && members.contains(m))
                .orElse(false);
    }

    public void addMember(Player player) {
        if (player.guildMembership().isPresent())
            return;
        if (has(player))
            return;
        GuildMembership member = GuildMembership.member(player, this.guildName);
        members.add(member);
        player.joinGuild(member);
    }

    public boolean removeMember(long playerId) {
        return members.removeIf(m -> m.playerId() == playerId);
    }

    public boolean canGrantKungFu(Player player) {
        return player.guildMembership().map(m -> m.canGrantKungFu() && members.contains(m))
                .orElse(false);
    }
    public boolean canInvite(Player player) {
        return canGrantKungFu(player);
    }

    public void registerGuildKungFu(AttackKungFu attackKungFu) {
        if (this.guildKungFu != null)
            return;
        this.guildKungFu = attackKungFu;
    }


    @Override
    public <AB> Optional<AB> findAbility(Class<AB> type) {
        return Optional.empty();
    }

    @Override
    public Coordinate coordinate() {
        return coordinate;
    }
    public I2ClientMessage captureDemoSnapshot() {
        return GroundItemSnapshot.ofDemo(this );
    }

    @Override
    public I2ClientMessage captureSnapshot() {
        return GroundItemSnapshot.of(this);
    }

    @Override
    public Optional<String> clickText() {
        return Optional.of("门派：" + guildName + "，门主：" + founderName + "。");
    }
}
