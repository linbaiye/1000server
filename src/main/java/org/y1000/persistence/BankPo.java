package org.y1000.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bank")
public class BankPo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, name = "player_id")
    private long playerId;

    private int capacity;

    private int unlocked;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankPo bankPo = (BankPo) o;
        return playerId == bankPo.playerId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId);
    }
}
