package org.y1000.account;

public record RegisterAccountRequest(String username, String password) implements AccountMessage {
}
