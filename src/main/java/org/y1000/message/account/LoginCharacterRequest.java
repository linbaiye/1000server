package org.y1000.message.account;

public record LoginCharacterRequest(String name) implements AccountMessage {
}
