package com.fadhli.auth_server.util;

import org.passay.*;

import java.util.List;

public class PasswordValidatorUtil {

    private static final PasswordValidator validator = new PasswordValidator(List.of(
            new LengthRule(8, 20),
            new CharacterRule(EnglishCharacterData.UpperCase, 1),
            new CharacterRule(EnglishCharacterData.LowerCase, 1),
            new CharacterRule(EnglishCharacterData.Digit, 1),
            new CharacterRule(EnglishCharacterData.Special, 1),
            new WhitespaceRule()
    ));

    public static List<String> validatePassword(String password) {
        RuleResult result = validator.validate(new PasswordData(password));
        if (result.isValid()) {
            return List.of();
        }

        return validator.getMessages(result);
    }
}
