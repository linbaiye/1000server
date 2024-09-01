package org.y1000.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.y1000.entities.creatures.State;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.PlayerExperiencedAgedAttribute;
import org.y1000.entities.players.PlayerLife;
import org.y1000.entities.players.YinYang;
import org.y1000.util.Coordinate;

import java.util.Objects;

@Data
@Builder
@Entity
@Table(name = "player")
@NoArgsConstructor
@AllArgsConstructor
public class PlayerPo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @SequenceGenerator(initialValue = 100000000, name = "player_seq")
    private Long id;

    @Column(unique = true)
    private String name;

    private int yin;

    private int yang;

    private int revivalExp;

    private boolean male;

    private int life;

    private int headLife;

    private int armLife;

    private int legLife;

    private int innerPower;

    private int outerPower;

    private int power;

    private int innerPowerExp;

    private int outerPowerExp;

    private int powerExp;

    private int x;

    private int y;

    private int state;

    private int realmId;

    @Column(updatable = false)
    private int accountId;


    @Transient
    private YinYang yinYang;

    public YinYang yinYang() {
        if (yinYang == null)
            yinYang = new YinYang(yin, yang);
        return yinYang;
    }

    public Coordinate coordinate() {
        return Coordinate.xy(x, y);
    }

    public State state() {
        return State.valueOf(state);
    }

    public PlayerLife life(int innate) {
        return new PlayerLife(innate, yinYang().age(), life);
    }

    public PlayerLife head(int innate) {
        return new PlayerLife(innate, yinYang().age(), headLife);
    }

    public PlayerLife arm(int innate) {
        return new PlayerLife(innate, yinYang().age(), armLife);
    }

    public PlayerLife leg(int innate) {
        return new PlayerLife(innate, yinYang().age(), legLife);
    }

    public PlayerExperiencedAgedAttribute power(int innate) {
        return new PlayerExperiencedAgedAttribute(innate, powerExp, power, yinYang().age());
    }

    public PlayerExperiencedAgedAttribute innerPower(int innate) {
        return new PlayerExperiencedAgedAttribute(innate, innerPowerExp, innerPower, yinYang().age());
    }

    public PlayerExperiencedAgedAttribute outerPower(int innate) {
        return new PlayerExperiencedAgedAttribute(innate, outerPowerExp, outerPower, yinYang().age());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerPo playerPo = (PlayerPo) o;
        return Objects.equals(name, playerPo.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
    public static PlayerPo convert(Player player, int accountId) {
        PlayerPo playerPo = convert(player);
        playerPo.accountId = accountId;
        return playerPo;
    }

    public static PlayerPo convert(Player player) {
        return PlayerPo.builder()
                .id(player.id() == 0 ? null : player.id())
                .name(player.viewName())
                .yin(player.yinyang().yinExp())
                .yang(player.yinyang().yangExp())
                .male(player.isMale())
                .life(player.currentLife())
                .armLife(player.armLife().currentValue())
                .headLife(player.headLife().currentValue())
                .legLife(player.legLife().currentValue())
                .innerPower(player.innerPower())
                .outerPower(player.outerPower())
                .revivalExp(player.revivalExp())
                .power(player.power())
                .innerPowerExp(player.innerPowerAttribute().exp())
                .outerPowerExp(player.outerPowerAttribute().exp())
                .powerExp(player.powerAttribute().exp())
                .x(player.coordinate().x())
                .y(player.coordinate().y())
                .state(player.stateEnum().value())
                .realmId(player.getRealm().id())
                .build();
    }
}
