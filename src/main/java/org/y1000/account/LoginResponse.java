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
    private List<String> charNames;
    public static LoginResponse badCredentials() {
        return new LoginResponse(404, Collections.emptyList());
    }

    public static LoginResponse badRequest() {
        return new LoginResponse(400, Collections.emptyList());
    }
}
