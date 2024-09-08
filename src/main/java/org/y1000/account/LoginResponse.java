package org.y1000.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private int status;
    private String token;
    private List<String> charNames;
    private String msg;
    public static LoginResponse badCredentials() {
        return new LoginResponse(1, null, Collections.emptyList(), "账号或密码错误");
    }

    public static LoginResponse serverError() {
        return new LoginResponse(1, null, Collections.emptyList(), "服务器错误");
    }

    public static LoginResponse badRequest() {
        return new LoginResponse(1, null, Collections.emptyList(), "请输入账号密码");
    }


    public static LoginResponse ok(List<String> chars, String token) {
        return new LoginResponse(0, token, chars, null);
    }
}
