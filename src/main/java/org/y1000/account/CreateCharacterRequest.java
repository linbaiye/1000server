package org.y1000.account;

public record CreateCharacterRequest(String name, boolean male) implements AccountMessage {
}
