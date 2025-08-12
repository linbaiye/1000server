package org.y1000.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.y1000.network.I2ClientMessage;
import org.y1000.network.gen.Packet;


@Data
@AllArgsConstructor
public class CreateCharResponse implements I2ClientMessage  {
    private int code;

    private String msg;

    private String charName;

    public static CreateCharResponse badRequest(String reason) {
        return new CreateCharResponse(1, reason, null);
    }

    public static CreateCharResponse ok(String name) {
        return new CreateCharResponse(0, null, name);
    }

    public static CreateCharResponse serverError() {
        return new CreateCharResponse(1, "服务器错误", null);
    }

    @Override
    public Packet toPacket() {
        return null;
    }
}
