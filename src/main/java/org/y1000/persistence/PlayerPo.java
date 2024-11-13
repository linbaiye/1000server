package org.y1000.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.creatures.State;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.PlayerExperiencedAgedAttribute;
import org.y1000.entities.players.PlayerLife;
import org.y1000.entities.players.YinYang;
import org.y1000.item.Equipment;
import org.y1000.util.Coordinate;

import java.util.*;

@Data
@Builder
@Entity
@Table(name = "player")
@NoArgsConstructor
@AllArgsConstructor
public class PlayerPo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "player_seq")
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

    @Column(name = "account_id")
    private int accountId;


    @Transient
    private YinYang yinYang;

    @OneToMany(cascade = CascadeType.MERGE)
    @JoinTable(
            joinColumns = @JoinColumn(name = "player_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "equipment_id", referencedColumnName = "id")
    )
    private List<EquipmentPo> equipments = new ArrayList<>();

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


    public void addEquipments(Collection<Equipment> equipmentSet) {
        equipments.clear();
        equipmentSet.stream().map(EquipmentPo::convert)
                .forEach(equipments::add);
    }

    public static PlayerPo convert(Player player, int accountId, int realmId) {
        Validate.notNull(player);
        PlayerPo playerPo = new PlayerPo();
        playerPo.mergeWithoutEquipments(player);
        playerPo.setAccountId(accountId);
        playerPo.setRealmId(realmId);
        playerPo.setMale(player.isMale());
        playerPo.setName(player.viewName());
        playerPo.setId(null);
        return playerPo;
    }

    private void mergeWithoutEquipments(Player player) {
        yin = player.yinyang().yinExp();
        yang = player.yinyang().yangExp();
        life = player.currentLife();
        armLife = player.armLife().currentValue();
        headLife = player.headLife().currentValue();
        legLife = player.legLife().currentValue();
        innerPower = player.innerPower();
        outerPower = player.outerPower();
        revivalExp = player.revivalExp();
        power = player.power();
        innerPowerExp = player.innerPowerAttribute().exp();
        outerPowerExp = player.outerPowerAttribute().exp();
        powerExp = player.powerAttribute().exp();
        x = player.coordinate().x();
        y = player.coordinate().y();
        realmId = player.getRealm() != null ? player.getRealm().id() : 0;
    }

    public void merge(Player player) {
        Validate.notNull(player);
        mergeWithoutEquipments(player);
        addEquipments(player.getEquipments());
    }

    public Optional<EquipmentPo> findEquipment(String name) {
        return equipments != null ? equipments.stream().filter(e -> e.getName().equals(name)).findFirst() :
                Optional.empty();
    }
}
