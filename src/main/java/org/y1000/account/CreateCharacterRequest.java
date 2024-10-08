package org.y1000.account;

import lombok.Data;

@Data
public class CreateCharacterRequest {
    private String token;
    private String characterName;
    private boolean male;
}
