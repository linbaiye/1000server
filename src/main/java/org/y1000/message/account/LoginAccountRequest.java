package org.y1000.message.account;

public record LoginAccountRequest(String name, String password) implements AccountMessage {

}
