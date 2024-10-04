package org.y1000.message;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.creatures.npc.Quester;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.AbstractPlayerEvent;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.message.serverevent.Visibility;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.UpdateQuestWindowPacket;

public final class UpdateQuestWindowEvent extends AbstractPlayerEvent  {

    private final Packet packet;

    public UpdateQuestWindowEvent(Player source,
                                  Quester quester) {
        super(source, Visibility.SELF);
        Validate.notNull(quester);
        this.packet = Packet.newBuilder()
                .setQuestWindow(UpdateQuestWindowPacket.newBuilder()
                        .setId(quester.id())
                        .setQuestName(quester.getQuest().getQuestName())
                        .setQuestDescription(quester.getQuest().getDescription())
                        .setSubmitText(quester.getQuest().getSubmitText())
                        .setNpcName(quester.viewName())
                        .build())
                .build();
    }

    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {

    }

    @Override
    protected Packet buildPacket() {
        return packet;
    }
}
