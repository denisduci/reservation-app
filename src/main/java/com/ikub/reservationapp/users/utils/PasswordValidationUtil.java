package com.ikub.reservationapp.users.utils;

import org.passay.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PasswordValidationUtil {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private static final String PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$";

    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    public boolean isPasswordMatch(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

    public boolean isValid(String password){
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public String encodePassword(String plainPassword) {
        return bCryptPasswordEncoder.encode(plainPassword);
    }

    public List<String> checkPasswordValidation(String password) {
        PasswordValidator validator = new PasswordValidator(Arrays.asList(
                // length between 8 and 16 characters
                new LengthRule(8, 16),
                // at least one upper-case character
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                // at least one lower-case character
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                // at least one digit character
                new CharacterRule(EnglishCharacterData.Digit, 1),
                // at least one symbol (special character)
                new CharacterRule(EnglishCharacterData.Special, 1),
                // no whitespace
                new WhitespaceRule(),
                // rejects passwords that contain a sequence of >= 5 characters alphabetical  (e.g. abcdef)
                new IllegalSequenceRule(EnglishSequenceData.Alphabetical, 5, false),
                // rejects passwords that contain a sequence of >= 5 characters numerical   (e.g. 12345)
                new IllegalSequenceRule(EnglishSequenceData.Numerical, 5, false)
        ));
        RuleResult result = validator.validate(new PasswordData(password));
        if (result.isValid()) {
            return null;
        }
        return validator.getMessages(result);
    }
}
