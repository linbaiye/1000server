package org.y1000.entities.creatures.npc;

import java.util.List;

public interface Merchant extends Npc {

    List<MerchantItem> buyItems();

    List<MerchantItem> sellItems();
}
