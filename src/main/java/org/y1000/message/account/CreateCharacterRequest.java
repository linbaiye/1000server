package org.y1000.message.account;

public record CreateCharacterRequest(String name, boolean male) implements AccountMessage {
}
