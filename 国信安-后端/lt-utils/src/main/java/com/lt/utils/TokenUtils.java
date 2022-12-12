package com.lt.utils;

import com.lt.model.user.User;

import java.security.PublicKey;
import java.util.Objects;

public class TokenUtils {

    public static boolean CheckToken(String username, String token, PublicKey key) {
        token = token.replace("lt ", "");
        Payload<User> payload = JwtUtils.getInfoFromToken(token, key, User.class);
        User user = payload.getUserInfo();
        if(!Objects.equals(user.getUsername(), username)){
            return true;
        }
        return false;
    }
}
