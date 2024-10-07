package org.y1000.item;

import org.y1000.message.ValueEnum;

import java.util.Arrays;
import java.util.Map;

public enum ItemType implements ValueEnum  {
    DYE(1),

    KUNGFU(2),

    MONEY(3),

    SELLING_GOODS(5),

    EQUIPMENT(6),

    ARROW(7),

    KNIFE(8),

    GUILD_STONE(9),

    HOUSE_STONE_GUARDIAN(10),

    PILL(13),

    BUFF_PILL(14),

//    ETC(17),

    BANK_INVENTORY(21),
    ROSE_DISCOLOR(22),
    SEE_THROUGH_NOTE(23),

//    CRAFTED_EQUIPMENT(24),
    DIGGER(27),
    CRAFT_MATERIAL(28),
//    CRAFTED_EQUIPMENT_MORE(29),


    STACK(Integer.MAX_VALUE),
    UNCATEGORIED(Integer.MAX_VALUE - 1),

            ;


    public static final String MONEY_NAME = "钱币";


    private static final Map<Integer, Integer> ITEM_TYPE_MAPPING = Map.of(4, 5,
            24, 6,
            29, 6);
    private final int v;

    ItemType(int v) {
        this.v = v;
    }

    @Override
    public int value() {
        return v;
    }

    public static boolean contains(int v) {
        return ITEM_TYPE_MAPPING.containsKey(v) || Arrays.stream(values()).anyMatch(e -> e.value() == v);
    }

    public static ItemType fromValue(int v) {
        if (ITEM_TYPE_MAPPING.containsKey(v)) {
            v = ITEM_TYPE_MAPPING.get(v);
        }
        return ValueEnum.fromValueOrThrow(values(), v);
    }
}
