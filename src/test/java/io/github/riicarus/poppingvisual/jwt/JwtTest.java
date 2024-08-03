package io.github.riicarus.poppingvisual.jwt;

import io.github.riicarus.poppingvisual.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Map;

/**
 * jwt test
 *
 * @author Riicarus
 * @create 2024-8-3 17:35
 * @since 1.0.0
 */
@Slf4j
class JwtTest {

    @Test
    void testSingJwt() {
        Map<String, String> payload1 = Map.of(
                "uid", "123",
                "username", "alice"
        );
        Map<String, String> payload2 = Map.of(
                "uid", "789",
                "username", "badman"
        );
        log.info("jwt-alice: {}", JwtUtil.sign(payload1));
        log.info("jwt-badman: {}", JwtUtil.sign(payload2));
    }

}
