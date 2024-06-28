package org.y1000.entities.creatures.npc;


import org.y1000.util.Coordinate;

public final class DevirtueMerchantAI extends AbstractWanderingNpcAI<DevirtueMerchant> {

    public DevirtueMerchantAI(Coordinate destination, Coordinate previousCoordinate) {
        super(destination, previousCoordinate);
    }

    public DevirtueMerchantAI() {
    }

    @Override
    protected void onHurtDone(DevirtueMerchant merchant) {
        if (merchant.state() instanceof NpcHurtState hurtState) {
            hurtState.previousState().afterHurt(merchant);
        } else {
            throw new IllegalStateException();
        }
    }
}
