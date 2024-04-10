package org.y1000.message;

import org.y1000.connection.gen.InterpolationsPacket;
import org.y1000.connection.gen.Packet;
import org.y1000.entities.players.Interpolation;

import java.util.Collection;

public class InterpolationsMessage implements ServerEvent {

    private final Collection<Interpolation> interpolations;

    public InterpolationsMessage(Collection<Interpolation> interpolations) {
        this.interpolations = interpolations;
    }

    @Override
    public Packet toPacket() {
        return Packet.newBuilder().setInterpolations(InterpolationsPacket.newBuilder()
                .addAllInterpolations(interpolations.stream().map(Interpolation::toPacket).toList())
                .build()).build();
    }

    @Override
    public void accept(ServerEventVisitor visitor) {

    }

    public static InterpolationsMessage wrap(Collection<Interpolation> interpolations) {
        return new InterpolationsMessage(interpolations);
    }
}
