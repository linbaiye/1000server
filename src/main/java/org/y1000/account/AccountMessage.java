package org.y1000.account;

public sealed interface AccountMessage permits CreateCharacterRequest, LoginAccountRequest, LoginCharacterRequest, RegisterAccountRequest {
}
