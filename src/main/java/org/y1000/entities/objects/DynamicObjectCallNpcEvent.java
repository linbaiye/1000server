package org.y1000.entities.objects;

import org.y1000.entities.ActiveEntity;
import org.y1000.realm.DynamicObjectEventHandler;

import java.util.List;

public class DynamicObjectCallNpcEvent extends AbstractDynamicObjectEvent {

    private final ActiveEntity enemy;
    private final List<String> npcName;

    protected DynamicObjectCallNpcEvent(DynamicObject source,
                                        ActiveEntity enemy,
                                        List<String> npcName) {
        super(source);
        this.enemy = enemy;
        this.npcName = npcName;
    }

    @Override
    public void accept(DynamicObjectEventHandler handler) {
        npcName.forEach(n -> handler.callNpc(n, enemy, source().coordinate()));
    }
}
