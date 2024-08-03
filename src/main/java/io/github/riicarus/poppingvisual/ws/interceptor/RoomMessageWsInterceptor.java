package io.github.riicarus.poppingvisual.ws.interceptor;

import io.github.riicarus.poppingvisual.util.JwtUtil;
import io.github.riicarus.poppingvisual.inner.room.WebSocketAttributeKeys;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * room message websocket interceptor
 *
 * @author Riicarus
 * @create 2024-8-3 17:02
 * @since 1.0.0
 */
@Component
@Slf4j
public class RoomMessageWsInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response,
                                   @NonNull WebSocketHandler wsHandler, @NonNull Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest req) {
            attributes.put(WebSocketAttributeKeys.ROOM_ID, req.getServletRequest().getParameter(WebSocketAttributeKeys.ROOM_ID));
            String token = req.getServletRequest().getParameter(WebSocketAttributeKeys.TOKEN);
            Map<String, String> payload = JwtUtil.parse(token);
            attributes.put(WebSocketAttributeKeys.UID, payload.get(WebSocketAttributeKeys.UID));
            attributes.put(WebSocketAttributeKeys.USERNAME, payload.get(WebSocketAttributeKeys.USERNAME));
            return true;
        }
        return false;
    }

    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response,
                               @NonNull WebSocketHandler wsHandler, Exception exception) {
        // do nothing
    }
}
