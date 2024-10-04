package org.y1000.message.clientevent;


public abstract class AbstractClientEvent implements ClientEvent {
    private Long playerId;

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        if (this.playerId == null)
            this.playerId = playerId;
        else
            throw new IllegalStateException();
    }
}
