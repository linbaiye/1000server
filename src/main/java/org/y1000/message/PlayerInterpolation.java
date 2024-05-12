package org.y1000.message;

import lombok.AccessLevel;
import lombok.Setter;
import org.y1000.entities.players.PlayerAttackState;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.entities.players.kungfu.attack.AttackKungFuType;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.PlayerInterpolationPacket;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.State;
import org.y1000.util.Coordinate;

public class PlayerInterpolation extends AbstractInterpolation {

    private final boolean male;

    @Setter(AccessLevel.PRIVATE)
    private AttackKungFuType attackKungFu = AttackKungFuType.QUANFA;

    @Setter(AccessLevel.PRIVATE)
    private boolean below50 = false;

    @Setter(AccessLevel.PRIVATE)
    private String name = "";

    @Setter(AccessLevel.PRIVATE)
    private int attackSpriteMillis = 0;

    private PlayerInterpolation(long id, Coordinate coordinate, State state, Direction direction, long elapsedMillis, boolean male) {
        super(id, coordinate, state, direction, elapsedMillis);
        this.male = male;
    }


    @Override
    public Packet toPacket() {
        return Packet.newBuilder()
                .setPlayerInterpolation(PlayerInterpolationPacket.newBuilder()
                        .setInterpolation(interpolationPacket())
                        .setId(getId())
                        .setKungFuType(attackKungFu.value())
                        .setName(name)
                        .setKungFuBelow50(below50)
                        .setKungFuSpriteMillis(attackSpriteMillis)
                        .setMale(male))
                .build();
    }

    private void attachKungFu(PlayerImpl player, PlayerAttackState playerState) {
        setBelow50(playerState.isBelow50());
        player.attackKungFu().ifPresent(kungfu -> {
            setAttackKungFu(kungfu.getType());
            setAttackSpriteMillis(kungfu.millisPerSprite(playerState.isBelow50()));
        });
    }

    public static PlayerInterpolation FromPlayer(PlayerImpl player, long elapsedMillis) {
        PlayerInterpolation playerInterpolation = new PlayerInterpolation(player.id(), player.coordinate(),
                player.stateEnum(), player.direction(),
                elapsedMillis, player.isMale());
        playerInterpolation.setName(player.name());
        if (player.state() instanceof PlayerAttackState attackState) {
            playerInterpolation.attachKungFu(player, attackState);
        }
        return playerInterpolation;
    }
}
