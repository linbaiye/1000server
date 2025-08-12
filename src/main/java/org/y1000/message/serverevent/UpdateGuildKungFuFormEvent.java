package org.y1000.message.serverevent;

public final class UpdateGuildKungFuFormEvent {
    /*
    private final Packet packet;

    public UpdateGuildKungFuFormEvent(Player source, Command command, String text) {
        super(source);
        this.packet = Packet.newBuilder().setKungFuForm(
                UpdateGuildKungFuFormPacket.newBuilder()
                        .setCommand(command.value())
                        .setText(text)
                        .build()
        ).build();
    }

    public enum Command implements ValueEnum {
        OPEN(1),
        TEXT(2),
        CLOSE(3),
        ;

        private final int v;

        Command(int v) {
            this.v = v;
        }

        @Override
        public int value() {
            return v;
        }
    }

    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {

    }

    @Override
    protected Packet buildPacket() {
        return packet;
    }

    public static UpdateGuildKungFuFormEvent close(Player player) {
        Validate.notNull(player);
        return new UpdateGuildKungFuFormEvent(player, Command.CLOSE, "");
    }

    public static UpdateGuildKungFuFormEvent text(Player player, String text) {
        Validate.notNull(player);
        Validate.notNull(text);
        return new UpdateGuildKungFuFormEvent(player, Command.TEXT, text);
    }A*/
}
