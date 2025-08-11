package org.y1000.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.y1000.message.I2ClientMessage;
import org.y1000.network.gen.LoginResponsePacket;
import org.y1000.network.gen.Packet;

import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse implements I2ClientMessage  {
    private int code;
    private List<String> charNames;
    private String msg;
    public static LoginResponse badCredentials() {
        return new LoginResponse(1, Collections.emptyList(), "账号或密码错误");
    }

    public static LoginResponse serverError() {
        return new LoginResponse(1, Collections.emptyList(), "服务器错误");
    }

    public static LoginResponse badRequest() {
        return new LoginResponse(1, Collections.emptyList(), "请输入账号密码");
    }


    public static LoginResponse ok(List<String> chars) {
        return new LoginResponse(0, chars, "登录成功");
    }

    @Override
    public Packet toPacket() {
        return Packet.newBuilder().setLoginResponse(LoginResponsePacket.newBuilder().setCode(code).setDescription(msg).addAllCharacters(charNames)).build();
    }
}
