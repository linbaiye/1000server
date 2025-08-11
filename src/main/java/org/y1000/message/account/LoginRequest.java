package org.y1000.message.account;

public record LoginRequest(String name, String password) implements AccountMessage {

}
