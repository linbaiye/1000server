package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.ShowQuestPacket;

public final class NpcQuestMessage extends Abstract2PlayerMessageEvent{
    public NpcQuestMessage(Player player, Packet packet) {
        super(player, packet);
    }

    public static NpcQuestMessage toPlayer(Player player, long npcId, String npcName, String questName, String abstraction, String description, String submit) {
        ShowQuestPacket packet = ShowQuestPacket.newBuilder()
                .setId(npcId)
                .setNpcName(npcName)
                .setAbstraction(abstraction)
                .setDescription(description)
                .setSubmit(submit)
                .setQuest(questName)
                .build();
        return new NpcQuestMessage(player, Packet.newBuilder().setShowQuest(packet).build());
    }
}
