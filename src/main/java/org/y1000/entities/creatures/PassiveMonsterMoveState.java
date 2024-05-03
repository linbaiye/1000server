package org.y1000.entities.creatures;

import org.y1000.entities.Direction;
import org.y1000.entities.players.State;

public class PassiveMonsterMoveState extends AbstractCreatureMoveState<PassiveMonster> {

    public PassiveMonsterMoveState(int millisPerUnit, Direction towards) {
        super(State.WALK, millisPerUnit, towards);
    }

    @Override
    public void update(PassiveMonster monster, int delta) {
        walkMillis(monster, delta);
        if (elapsedMillis() >= millisPerUnit()) {
            tryChangeCoordinate(monster, monster.realmMap());
            monster.AI().nextMove();
        }
    }


    public static PassiveMonsterMoveState buffalo(Direction towards) {
        return new PassiveMonsterMoveState(1050, towards);
    }
}
