package org.y1000.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.y1000.item.Item;
import org.y1000.item.StackItem;

@Data
@Entity
@Table(name = "item")
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class ItemPo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int color;

    private Long number;

    public void update(Item item) {
        if (item == null)
            return;
        color = item.color();
        if (item instanceof StackItem stackItem)
            number = stackItem.number();
    }
}
