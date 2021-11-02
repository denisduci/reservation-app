package com.ikub.reservationapp.security.ldap.encoder;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class LdapCustomPasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
//        String hashed = BCrypt.hashpw(rawPassword.toString(), BCrypt.gensalt(12));
//        return hashed;
        String base64;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(rawPassword.toString().getBytes());
            base64 = Base64.getEncoder()
                    .encodeToString(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return "{SHA}" + base64;
    }


    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
//        return BCrypt.checkpw(rawPassword.toString(), encodedPassword);

        return encode(rawPassword).equals(encodedPassword);
    }
}
