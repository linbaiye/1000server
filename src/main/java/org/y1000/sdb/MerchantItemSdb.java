package org.y1000.sdb;


import org.apache.commons.lang3.Validate;
import org.y1000.entities.creatures.npc.MerchantItem;

import java.util.*;

public record MerchantItemSdb(List<MerchantItem> buy, List<MerchantItem> sell)  {

    public MerchantItemSdb {
        Validate.notNull(buy);
        Validate.notNull(sell);
    }
}