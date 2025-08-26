package org.y1000.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.y1000.network.I2ClientMessage;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.RegisterResponsePacket;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponse implements I2ClientMessage {
    private int code;
    private String msg;

    @Override
    public Packet toPacket() {
        return Packet.newBuilder().setRegisterResponse(RegisterResponsePacket.newBuilder().setCode(code).setDescription(msg).buildPartial()).build();
    }
}
