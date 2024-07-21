package org.y1000.message;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.creatures.NpcType;
import org.y1000.network.gen.CreatureInterpolationPacket;
import org.y1000.network.gen.Packet;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.State;
import org.y1000.util.Coordinate;

public final class NpcInterpolation extends AbstractNamedCreatureInterpolation {
    private final NpcType type;
    private final String merchantFileName;
    private final String shape;
    private final String animate;

    public NpcInterpolation(long id, Coordinate coordinate, State state,
                            Direction direction, int elapsedMillis, String name, NpcType type,
                            String animate, String shape) {
        this(id, coordinate, state, direction, elapsedMillis, name, type, animate, shape, null);
    }

    public NpcInterpolation(long id, Coordinate coordinate, State state,
                            Direction direction, int elapsedMillis, String name, NpcType type,
                            String animate, String shape, String textFileName) {
        super(id, coordinate, state, direction, elapsedMillis, name);
        Validate.notNull(shape);
        Validate.notNull(animate);
        this.type = type;
        this.merchantFileName = textFileName;
        this.animate = animate;
        this.shape = shape;
    }

    @Override
    public Packet toPacket() {
        CreatureInterpolationPacket.Builder builder = CreatureInterpolationPacket.newBuilder()
                .setInterpolation(interpolationPacket())
                .setId(getId())
                .setName(getName())
                .setShape(shape)
                .setAnimate(animate)
                .setType(type.value());
        if (merchantFileName != null) {
            builder.setMerchantFile(merchantFileName);
        }
        return Packet.newBuilder().setCreatureInterpolation(builder).build();
    }
}
