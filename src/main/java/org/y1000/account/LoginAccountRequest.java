package org.y1000.account;

public record LoginAccountRequest(String name, String password) implements AccountMessage {

}
