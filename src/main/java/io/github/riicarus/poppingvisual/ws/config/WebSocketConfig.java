package io.github.riicarus.poppingvisual.ws.config;

import io.github.riicarus.poppingvisual.ws.handler.RoomMessageWsHandler;
import io.github.riicarus.poppingvisual.ws.interceptor.RoomMessageWsInterceptor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * websocket config, bind websocket handlers and their interceptors
 *
 * @author Riicarus
 * @create 2024-8-3 15:35
 * @since 1.0.0
 */
@Configuration
@EnableWebSocket
@Slf4j
public class WebSocketConfig implements WebSocketConfigurer {

    private final RoomMessageWsHandler roomMessageWsHandler;
    private final RoomMessageWsInterceptor roomMessageWsInterceptor;

    @Autowired
    public WebSocketConfig(RoomMessageWsHandler roomMessageWsHandler, RoomMessageWsInterceptor roomMessageWsInterceptor) {
        this.roomMessageWsHandler = roomMessageWsHandler;
        this.roomMessageWsInterceptor = roomMessageWsInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(@NonNull WebSocketHandlerRegistry registry) {
        registry.addHandler(roomMessageWsHandler, "/api/ws/channel/message").addInterceptors(roomMessageWsInterceptor).setAllowedOrigins("*");
    }
}
