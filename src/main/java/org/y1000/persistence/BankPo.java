package org.y1000.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.y1000.entities.players.inventory.Bank;

import java.util.Objects;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bank")
public class BankPo extends AbstractInventoryPo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, name = "player_id")
    private long playerId;

    private int capacity;

    private int unlocked;

    public void merge(Bank bank) {
        this.capacity = bank.capacity();
        this.unlocked = bank.getUnlocked();
        super.merge(bank);
    }

}
