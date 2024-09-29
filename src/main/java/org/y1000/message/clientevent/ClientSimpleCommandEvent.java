package org.y1000.message.clientevent;

/**
 * Toxic.
 * @param command
 */
@Deprecated
public record ClientSimpleCommandEvent(SimpleCommand command) implements ClientEvent {

    public static ClientSimpleCommandEvent parse(int v) {
        return new ClientSimpleCommandEvent(SimpleCommand.fromValue(v));
    }

    public boolean isAskingPosition() {
        return command == SimpleCommand.NPC_POSITION;
    }

    public boolean isQuit() {
        return command == SimpleCommand.CLIENT_QUIT;
    }

    public static ClientSimpleCommandEvent QUIT = new ClientSimpleCommandEvent(SimpleCommand.CLIENT_QUIT);

}
