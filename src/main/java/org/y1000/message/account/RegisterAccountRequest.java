package org.y1000.message.account;

public record RegisterAccountRequest(String username, String password) implements AccountMessage {
}
