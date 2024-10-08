package org.y1000.persistence;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Data
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PlayerIdNameKey implements Serializable {

    private long playerId;

    private String name;
}
