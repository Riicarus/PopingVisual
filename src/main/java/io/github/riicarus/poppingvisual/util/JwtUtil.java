package io.github.riicarus.poppingvisual.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Map;

import static java.util.stream.Collectors.toUnmodifiableMap;

/**
 * jwt util
 *
 * @author Riicarus
 * @create 2024-8-3 17:16
 * @since 1.0.0
 */
public final class JwtUtil {

    private JwtUtil() {
    }

    private static final String SECRET_KEY = "!@#$%67890";

    public static String sign(Map<String, String> payload) {
        final JWTCreator.Builder builder = JWT.create();
        payload.forEach(builder::withClaim);
        return builder.sign(Algorithm.HMAC256(SECRET_KEY));
    }

    public static Map<String, String> parse(String token) {
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(SECRET_KEY))
                .build().verify(token);
        return decodedJWT.getClaims().entrySet().stream().collect(toUnmodifiableMap(Map.Entry::getKey, e -> e.getValue().asString()));
    }
}
