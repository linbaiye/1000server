package org.y1000.message.clientevent;

public record ClientManageGuildEvent(int command, String target) implements ClientEvent {

    private static final int TEACH_KUNGFU = 1;
    private static final int INVITE = 2;
    private static final int KICK = 3;
    private static final int PROMOTE = 4;


    public boolean isTeachKungFu() {
        return command == TEACH_KUNGFU;
    }

    public boolean isInvite() {
        return  command == INVITE;
    }

}
