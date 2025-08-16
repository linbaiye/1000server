package org.y1000.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.y1000.kungfu.KungFuBook;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "kung_fu_book")
@Data
public class KungFuBookPo {
    @Id
    private long playerId;

    @JoinColumn
    @JdbcTypeCode(SqlTypes.JSON)
    private List<KungFuSlotPo> unnamed;

    @JoinColumn
    @JdbcTypeCode(SqlTypes.JSON)
    private List<KungFuSlotPo> basic;

    public static KungFuBookPo convert(long id, KungFuBook kungFuBook) {
        KungFuBookPo kungFuBookPo = new KungFuBookPo();
        kungFuBookPo.setPlayerId(id);
        kungFuBookPo.basic = new ArrayList<>();
        kungFuBook.foreachBasic((s, kf) -> {
            kungFuBookPo.basic.add(KungFuSlotPo.convert(s, kf));
        });
        kungFuBookPo.unnamed = new ArrayList<>();
        kungFuBook.foreachUnnamed((s, kf) -> {
            kungFuBookPo.unnamed.add(KungFuSlotPo.convert(s, kf));
        });
        return kungFuBookPo;
    }

}
