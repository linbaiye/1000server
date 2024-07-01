package org.y1000.entities.creatures.npc;


import org.y1000.util.Coordinate;

public final class DevirtueMerchantAI extends AbstractWanderingNpcAI {

    @Override
    protected void onHurtDone(Npc npc) {
        var merchant = (DevirtueMerchant)npc;
        if (merchant.state() instanceof NpcHurtState hurtState) {
            npc.changeState(hurtState.previousState());
            npc.state().afterHurt(merchant);
        } else {
            throw new IllegalStateException();
        }
    }


    public DevirtueMerchantAI(Coordinate destination, Coordinate previousCoordinate) {
        super(destination, previousCoordinate);
    }

    public DevirtueMerchantAI() {
    }
}
